package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.YeuCauSuCo;
import com.nhatro.quanlynhatro.enums.TrangThaiSuCo;
import com.nhatro.quanlynhatro.service.YeuCauSuCoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/su-co")
@RequiredArgsConstructor
public class YeuCauSuCoController {

    private final YeuCauSuCoService yeuCauSuCoService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachSuCo", yeuCauSuCoService.findAll());
            model.addAttribute("trangThaiSuCos", TrangThaiSuCo.values());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách sự cố: " + e.getMessage());
        }
        return "landlord/su-co/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            YeuCauSuCo yeuCauSuCo = yeuCauSuCoService.findById(id);
            model.addAttribute("yeuCauSuCo", yeuCauSuCo);
            model.addAttribute("trangThaiSuCos", TrangThaiSuCo.values());
            return "landlord/su-co/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy yêu cầu sự cố: " + e.getMessage());
            return "redirect:/landlord/su-co";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TrangThaiSuCo trangThai,
                               @RequestParam(required = false) String ghiChuXuLy,
                               RedirectAttributes redirectAttributes) {
        try {
            yeuCauSuCoService.updateStatus(id, trangThai, ghiChuXuLy);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái sự cố thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/landlord/su-co/detail/" + id;
    }
}
