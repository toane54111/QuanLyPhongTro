package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tenant/hoa-don")
@RequiredArgsConstructor
public class TenantHoaDonController {

    private final NguoiDungService nguoiDungService;
    private final HoaDonService hoaDonService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            List<HoaDon> hoaDons = hoaDonService.findByKhachThueId(currentUser.getUserId());
            model.addAttribute("hoaDons", hoaDons);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hoa-don/list";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            // Kiểm tra quyền sở hữu
            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hoa-don/detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hoa-don";
        }
    }

    @PostMapping("/thanh-toan/{id}")
    public String thanhToan(@PathVariable Long id,
                            @RequestParam String phuongThuc,
                            RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            // Kiểm tra quyền sở hữu
            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thanh toán hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            PhuongThucThanhToan pt = PhuongThucThanhToan.valueOf(phuongThuc);
            hoaDonService.thanhToan(hoaDon, pt);

            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán hóa đơn thành công!");
            return "redirect:/tenant/hoa-don/detail/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phương thức thanh toán không hợp lệ");
            return "redirect:/tenant/hoa-don/detail/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại: " + e.getMessage());
            return "redirect:/tenant/hoa-don/detail/" + id;
        }
    }
}
