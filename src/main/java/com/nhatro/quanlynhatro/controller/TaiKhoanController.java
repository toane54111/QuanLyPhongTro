package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.VaiTro;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TaiKhoanController {

    private final NguoiDungService nguoiDungService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getRedirectPrefix(NguoiDung user) {
        return user.getVaiTro() == VaiTro.CHU_TRO ? "/landlord/tai-khoan" : "/tenant/tai-khoan";
    }

    // === Hiển thị thông tin tài khoản ===

    @GetMapping("/tenant/tai-khoan")
    public String tenantTaiKhoan(Model model) {
        return showTaiKhoan(model);
    }

    @GetMapping("/landlord/tai-khoan")
    public String landlordTaiKhoan(Model model) {
        return showTaiKhoan(model);
    }

    private String showTaiKhoan(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            model.addAttribute("nguoiDung", currentUser);
            return "common/tai-khoan";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // === Cập nhật thông tin cá nhân ===

    @PostMapping("/tenant/tai-khoan/update")
    public String tenantUpdateProfile(@RequestParam String hoTen,
                                      @RequestParam(required = false) String sdt,
                                      RedirectAttributes redirectAttributes) {
        return updateProfile(hoTen, sdt, redirectAttributes);
    }

    @PostMapping("/landlord/tai-khoan/update")
    public String landlordUpdateProfile(@RequestParam String hoTen,
                                        @RequestParam(required = false) String sdt,
                                        RedirectAttributes redirectAttributes) {
        return updateProfile(hoTen, sdt, redirectAttributes);
    }

    private String updateProfile(String hoTen, String sdt, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();

            if (hoTen == null || hoTen.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Họ tên không được để trống");
                return "redirect:" + getRedirectPrefix(currentUser);
            }

            nguoiDungService.updateProfile(currentUser, hoTen.trim(), sdt != null ? sdt.trim() : null);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:" + getRedirectPrefix(currentUser);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
            return "redirect:/login";
        }
    }

    // === Đổi mật khẩu ===

    @PostMapping("/tenant/tai-khoan/change-password")
    public String tenantChangePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        return changePassword(oldPassword, newPassword, confirmPassword, redirectAttributes);
    }

    @PostMapping("/landlord/tai-khoan/change-password")
    public String landlordChangePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword,
                                         @RequestParam String confirmPassword,
                                         RedirectAttributes redirectAttributes) {
        return changePassword(oldPassword, newPassword, confirmPassword, redirectAttributes);
    }

    private String changePassword(String oldPassword, String newPassword, String confirmPassword,
                                  RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            String redirectUrl = getRedirectPrefix(currentUser);

            if (newPassword == null || newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự");
                return "redirect:" + redirectUrl;
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu không khớp");
                return "redirect:" + redirectUrl;
            }

            boolean success = nguoiDungService.changePassword(currentUser, oldPassword, newPassword);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu cũ không đúng");
            }
            return "redirect:" + redirectUrl;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đổi mật khẩu thất bại: " + e.getMessage());
            return "redirect:/login";
        }
    }
}
