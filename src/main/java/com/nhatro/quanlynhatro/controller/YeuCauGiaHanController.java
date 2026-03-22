package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.service.YeuCauGiaHanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/yeu-cau-gia-han")
@RequiredArgsConstructor
public class YeuCauGiaHanController {

    private final YeuCauGiaHanService yeuCauGiaHanService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachYeuCau", yeuCauGiaHanService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách yêu cầu gia hạn: " + e.getMessage());
        }
        return "landlord/yeu-cau/gia-han-list";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            yeuCauGiaHanService.approve(id);
            redirectAttributes.addFlashAttribute("success", "Phê duyệt yêu cầu gia hạn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi phê duyệt: " + e.getMessage());
        }
        return "redirect:/landlord/yeu-cau-gia-han";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         @RequestParam String lyDoTuChoi,
                         RedirectAttributes redirectAttributes) {
        try {
            yeuCauGiaHanService.reject(id, lyDoTuChoi);
            redirectAttributes.addFlashAttribute("success", "Đã từ chối yêu cầu gia hạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi từ chối yêu cầu: " + e.getMessage());
        }
        return "redirect:/landlord/yeu-cau-gia-han";
    }
}
