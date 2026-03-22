package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.KhuTro;
import com.nhatro.quanlynhatro.service.KhuTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/khu-tro")
@RequiredArgsConstructor
public class KhuTroController {

    private final KhuTroService khuTroService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachKhuTro", khuTroService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách khu trọ: " + e.getMessage());
        }
        return "landlord/khu-tro/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("khuTro", new KhuTro());
        model.addAttribute("isEdit", false);
        return "landlord/khu-tro/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute KhuTro khuTro, RedirectAttributes redirectAttributes) {
        try {
            khuTroService.save(khuTro);
            redirectAttributes.addFlashAttribute("success", "Thêm khu trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khu trọ: " + e.getMessage());
        }
        return "redirect:/landlord/khu-tro";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            KhuTro khuTro = khuTroService.findById(id);
            model.addAttribute("khuTro", khuTro);
            model.addAttribute("isEdit", true);
            return "landlord/khu-tro/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy khu trọ: " + e.getMessage());
            return "redirect:/landlord/khu-tro";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute KhuTro khuTro, RedirectAttributes redirectAttributes) {
        try {
            khuTro.setKhuTroId(id);
            khuTroService.update(khuTro);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khu trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật khu trọ: " + e.getMessage());
        }
        return "redirect:/landlord/khu-tro";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            khuTroService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khu trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa khu trọ: " + e.getMessage());
        }
        return "redirect:/landlord/khu-tro";
    }
}
