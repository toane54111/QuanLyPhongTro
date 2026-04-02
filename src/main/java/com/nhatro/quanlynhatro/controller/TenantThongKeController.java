package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.service.TenantThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.Map;

@Controller
@RequestMapping("/tenant/thong-ke")
@RequiredArgsConstructor
public class TenantThongKeController {

    private final TenantThongKeService tenantThongKeService;
    private final NguoiDungRepository nguoiDungRepository;

    /**
     * Trang thong ke chinh
     */
    @GetMapping
    public String thongKePage(Model model, Authentication auth) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return "redirect:/login";

        Long khachThueId = nguoiDung.getUserId();
        var hopDongOpt = tenantThongKeService.getHopDongHieuLuc(khachThueId);

        if (hopDongOpt.isEmpty()) {
            model.addAttribute("khongCoHopDong", true);
            return "tenant/thong-ke/index";
        }

        var hopDong = hopDongOpt.get();
        Long phongId = hopDong.getPhongTro().getPhongId();
        int namHienTai = YearMonth.now().getYear();

        model.addAttribute("khongCoHopDong", false);
        model.addAttribute("phongId", phongId);
        model.addAttribute("soPhong", hopDong.getPhongTro().getSoPhong());
        model.addAttribute("namHienTai", namHienTai);

        return "tenant/thong-ke/index";
    }

    /**
     * API: Lay chi tiet theo nam (bieu do + bang chi tiet)
     */
    @GetMapping("/api/chi-tiet")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChiTietApi(
            Authentication auth,
            @RequestParam(defaultValue = "2026") int nam) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return ResponseEntity.notFound().build();

        Long khachThueId = nguoiDung.getUserId();
        var hopDongOpt = tenantThongKeService.getHopDongHieuLuc(khachThueId);
        if (hopDongOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long phongId = hopDongOpt.get().getPhongTro().getPhongId();
        Map<String, Object> data = tenantThongKeService.getChiTietTheoNam(khachThueId, phongId, nam);
        return ResponseEntity.ok(data);
    }

    /**
     * API: Lay tong quan theo nam (summary cards)
     */
    @GetMapping("/api/tong-quan")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTongQuanApi(
            Authentication auth,
            @RequestParam(defaultValue = "2026") int nam) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return ResponseEntity.notFound().build();

        Long khachThueId = nguoiDung.getUserId();
        var hopDongOpt = tenantThongKeService.getHopDongHieuLuc(khachThueId);
        if (hopDongOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long phongId = hopDongOpt.get().getPhongTro().getPhongId();
        Map<String, Object> data = tenantThongKeService.getTongQuanTheoNam(khachThueId, phongId, nam);
        return ResponseEntity.ok(data);
    }
}
