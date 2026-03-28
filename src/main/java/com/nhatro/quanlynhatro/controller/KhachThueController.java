package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.service.ChiSoDienNuocService;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.KhachThueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/landlord/khach-thue")
@RequiredArgsConstructor
public class KhachThueController {

    private final KhachThueService khachThueService;
    private final HopDongService hopDongService;
    private final HoaDonService hoaDonService;
    private final ChiSoDienNuocService chiSoDienNuocService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachKhachThue", khachThueService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách khách thuê: " + e.getMessage());
        }
        return "landlord/khach-thue/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("khachThue", new NguoiDung());
        model.addAttribute("isEdit", false);
        return "landlord/khach-thue/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute NguoiDung khachThue, RedirectAttributes redirectAttributes) {
        try {
            khachThueService.create(khachThue);
            redirectAttributes.addFlashAttribute("success", "Thêm khách thuê thành công! Mật khẩu mặc định: 123456");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khách thuê: " + e.getMessage());
        }
        return "redirect:/landlord/khach-thue";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung khachThue = khachThueService.findById(id);
            model.addAttribute("khachThue", khachThue);
            model.addAttribute("isEdit", true);
            return "landlord/khach-thue/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách thuê: " + e.getMessage());
            return "redirect:/landlord/khach-thue";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute NguoiDung khachThue, RedirectAttributes redirectAttributes) {
        try {
            khachThue.setUserId(id);
            khachThueService.update(khachThue);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin khách thuê thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật khách thuê: " + e.getMessage());
        }
        return "redirect:/landlord/khach-thue/detail/" + id;
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung khachThue = khachThueService.findById(id);
            model.addAttribute("khachThue", khachThue);

            // Hợp đồng hiện tại (đang hiệu lực)
            List<HopDong> activeContracts = hopDongService.findByKhachThueIdAndTrangThai(
                    id, TrangThaiHopDong.DANG_HIEU_LUC);
            HopDong hopDongHienTai = activeContracts.isEmpty() ? null : activeContracts.get(0);
            model.addAttribute("hopDongHienTai", hopDongHienTai);

            // Tất cả hợp đồng
            List<HopDong> allContracts = hopDongService.findByKhachThueId(id);
            model.addAttribute("danhSachHopDong", allContracts);

            // Lịch sử hóa đơn
            List<HoaDon> hoaDons = hoaDonService.findByKhachThueId(id);
            model.addAttribute("danhSachHoaDon", hoaDons);

            // Chỉ số điện nước (theo phòng hiện tại)
            if (hopDongHienTai != null) {
                Long phongId = hopDongHienTai.getPhongTro().getPhongId();
                List<ChiSoDienNuoc> chiSos = chiSoDienNuocService.findByPhongId(phongId);
                model.addAttribute("danhSachChiSo", chiSos);
            } else {
                model.addAttribute("danhSachChiSo", Collections.emptyList());
            }

            return "landlord/khach-thue/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách thuê: " + e.getMessage());
            return "redirect:/landlord/khach-thue";
        }
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            khachThueService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/landlord/khach-thue";
    }
}
