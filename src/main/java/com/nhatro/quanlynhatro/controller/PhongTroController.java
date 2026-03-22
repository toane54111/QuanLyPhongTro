package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.service.KhuTroService;
import com.nhatro.quanlynhatro.service.PhongTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/phong-tro")
@RequiredArgsConstructor
public class PhongTroController {

    private final PhongTroService phongTroService;
    private final KhuTroService khuTroService;

    @GetMapping
    public String list(@RequestParam(required = false) Long khuTroId,
                       @RequestParam(required = false) TrangThaiPhong trangThai,
                       Model model) {
        try {
            model.addAttribute("danhSachPhongTro", phongTroService.findAll(khuTroId, trangThai));
            model.addAttribute("danhSachKhuTro", khuTroService.findAll());
            model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
            model.addAttribute("selectedKhuTroId", khuTroId);
            model.addAttribute("selectedTrangThai", trangThai);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách phòng trọ: " + e.getMessage());
        }
        return "landlord/phong-tro/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("phongTro", new PhongTro());
        model.addAttribute("danhSachKhuTro", khuTroService.findAll());
        model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
        model.addAttribute("isEdit", false);
        return "landlord/phong-tro/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute PhongTro phongTro,
                         @RequestParam Long khuTroId,
                         RedirectAttributes redirectAttributes) {
        try {
            phongTroService.save(phongTro, khuTroId);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PhongTro phongTro = phongTroService.findById(id);
            model.addAttribute("phongTro", phongTro);
            model.addAttribute("danhSachKhuTro", khuTroService.findAll());
            model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
            model.addAttribute("isEdit", true);
            return "landlord/phong-tro/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng trọ: " + e.getMessage());
            return "redirect:/landlord/phong-tro";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute PhongTro phongTro,
                         @RequestParam Long khuTroId,
                         RedirectAttributes redirectAttributes) {
        try {
            phongTro.setPhongId(id);
            phongTroService.update(phongTro, khuTroId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            phongTroService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }
}
