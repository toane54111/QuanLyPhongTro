package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/landlord/thong-ke")
@RequiredArgsConstructor
public class ThongKeController {

    private final ThongKeService thongKeService;

    @GetMapping
    public String index(Model model) {
        try {
            int namHienTai = LocalDate.now().getYear();
            model.addAttribute("namHienTai", namHienTai);
            model.addAttribute("thongKeTongQuan", thongKeService.getThongKeTongQuan());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thống kê: " + e.getMessage());
        }
        return "landlord/thong-ke/index";
    }

    @GetMapping("/api/doanh-thu")
    @ResponseBody
    public Map<String, Object> getDoanhThuTheoThang(@RequestParam(required = false) Integer nam) {
        if (nam == null) {
            nam = LocalDate.now().getYear();
        }
        return thongKeService.getDoanhThuTheoThang(nam);
    }
}
