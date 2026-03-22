package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.entity.YeuCauChamDut;
import com.nhatro.quanlynhatro.entity.YeuCauGiaHan;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import com.nhatro.quanlynhatro.service.YeuCauChamDutService;
import com.nhatro.quanlynhatro.service.YeuCauGiaHanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tenant/hop-dong")
@RequiredArgsConstructor
public class TenantHopDongController {

    private final NguoiDungService nguoiDungService;
    private final HopDongService hopDongService;
    private final YeuCauGiaHanService yeuCauGiaHanService;
    private final YeuCauChamDutService yeuCauChamDutService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            List<HopDong> hopDongs = hopDongService.findByKhachThueId(currentUser.getUserId());
            model.addAttribute("hopDongs", hopDongs);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/list";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            // Kiểm tra quyền sở hữu
            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem hợp đồng này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @GetMapping("/yeu-cau-gia-han/{hopDongId}")
    public String showGiaHanForm(@PathVariable Long hopDongId, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/yeu-cau-gia-han";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/yeu-cau-gia-han/{hopDongId}")
    public String submitGiaHan(@PathVariable Long hopDongId,
                               @RequestParam Integer thoiGianGiaHan,
                               @RequestParam(required = false) String ghiChu,
                               RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            YeuCauGiaHan yeuCau = YeuCauGiaHan.builder()
                    .khachThue(currentUser)
                    .hopDong(hopDong)
                    .thoiGianGiaHan(thoiGianGiaHan)
                    .ghiChu(ghiChu)
                    .trangThai(TrangThaiYeuCau.CHO_PHE_DUYET)
                    .ngayTao(LocalDateTime.now())
                    .build();
            yeuCauGiaHanService.save(yeuCau);

            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu gia hạn thành công. Vui lòng chờ phê duyệt.");
            return "redirect:/tenant/hop-dong/detail/" + hopDongId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi yêu cầu thất bại: " + e.getMessage());
            return "redirect:/tenant/hop-dong/yeu-cau-gia-han/" + hopDongId;
        }
    }

    @GetMapping("/yeu-cau-cham-dut/{hopDongId}")
    public String showChamDutForm(@PathVariable Long hopDongId, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/yeu-cau-cham-dut";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/yeu-cau-cham-dut/{hopDongId}")
    public String submitChamDut(@PathVariable Long hopDongId,
                                @RequestParam String ngayDuKienTra,
                                @RequestParam(required = false) String lyDo,
                                RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            YeuCauChamDut yeuCau = YeuCauChamDut.builder()
                    .khachThue(currentUser)
                    .hopDong(hopDong)
                    .ngayDuKienTra(LocalDate.parse(ngayDuKienTra))
                    .lyDo(lyDo)
                    .trangThai(TrangThaiYeuCau.CHO_PHE_DUYET)
                    .ngayTao(LocalDateTime.now())
                    .build();
            yeuCauChamDutService.save(yeuCau);

            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu chấm dứt hợp đồng thành công. Vui lòng chờ phê duyệt.");
            return "redirect:/tenant/hop-dong/detail/" + hopDongId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi yêu cầu thất bại: " + e.getMessage());
            return "redirect:/tenant/hop-dong/yeu-cau-cham-dut/" + hopDongId;
        }
    }
}
