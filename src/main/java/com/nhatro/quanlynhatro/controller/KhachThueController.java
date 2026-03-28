package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.service.KhachThueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/khach-thue")
@RequiredArgsConstructor
public class KhachThueController {

    private final KhachThueService khachThueService;

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
        return "redirect:/landlord/khach-thue";
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
