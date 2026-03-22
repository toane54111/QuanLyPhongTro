package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.DichVu;
import com.nhatro.quanlynhatro.service.DichVuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/dich-vu")
@RequiredArgsConstructor
public class DichVuController {

    private final DichVuService dichVuService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachDichVu", dichVuService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách dịch vụ: " + e.getMessage());
        }
        return "landlord/dich-vu/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("dichVu", new DichVu());
        model.addAttribute("isEdit", false);
        return "landlord/dich-vu/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute DichVu dichVu, RedirectAttributes redirectAttributes) {
        try {
            dichVuService.save(dichVu);
            redirectAttributes.addFlashAttribute("success", "Thêm dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm dịch vụ: " + e.getMessage());
        }
        return "redirect:/landlord/dich-vu";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DichVu dichVu = dichVuService.findById(id);
            model.addAttribute("dichVu", dichVu);
            model.addAttribute("isEdit", true);
            return "landlord/dich-vu/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ: " + e.getMessage());
            return "redirect:/landlord/dich-vu";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute DichVu dichVu, RedirectAttributes redirectAttributes) {
        try {
            dichVu.setDichVuId(id);
            dichVuService.update(dichVu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật dịch vụ: " + e.getMessage());
        }
        return "redirect:/landlord/dich-vu";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dichVuService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa dịch vụ: " + e.getMessage());
        }
        return "redirect:/landlord/dich-vu";
    }
}
