package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.service.YeuCauChamDutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/yeu-cau-cham-dut")
@RequiredArgsConstructor
public class YeuCauChamDutController {

    private final YeuCauChamDutService yeuCauChamDutService;

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("danhSachYeuCau", yeuCauChamDutService.findAll());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách yêu cầu chấm dứt: " + e.getMessage());
        }
        return "landlord/yeu-cau/cham-dut-list";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            java.math.BigDecimal tienHoanCoc = yeuCauChamDutService.tinhTienHoanCoc(id);
            yeuCauChamDutService.approve(id);
            redirectAttributes.addFlashAttribute("success",
                    "Phê duyệt chấm dứt hợp đồng thành công! Tiền hoàn cọc: "
                    + String.format("%,.0f", tienHoanCoc) + " VNĐ");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi phê duyệt: " + e.getMessage());
        }
        return "redirect:/landlord/yeu-cau-cham-dut";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id,
                         @RequestParam String lyDoTuChoi,
                         RedirectAttributes redirectAttributes) {
        try {
            yeuCauChamDutService.reject(id, lyDoTuChoi);
            redirectAttributes.addFlashAttribute("success", "Đã từ chối yêu cầu chấm dứt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi từ chối yêu cầu: " + e.getMessage());
        }
        return "redirect:/landlord/yeu-cau-cham-dut";
    }
}
