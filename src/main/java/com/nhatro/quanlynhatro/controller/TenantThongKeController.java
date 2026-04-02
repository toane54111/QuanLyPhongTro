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

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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

        YearMonth currentMonth = YearMonth.now();
        String kyHienTai = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String kyTruoc = currentMonth.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        model.addAttribute("khongCoHopDong", false);
        model.addAttribute("phongId", phongId);
        model.addAttribute("soPhong", hopDong.getPhongTro().getSoPhong());
        model.addAttribute("kyHienTai", kyHienTai);
        model.addAttribute("kyTruoc", kyTruoc);

        // Chi so thang hien tai
        Map<String, Object> chiSoHT = tenantThongKeService.getChiSoThangHienTai(phongId, kyHienTai);
        Map<String, Object> chiSoTruoc = tenantThongKeService.getChiSoThangHienTai(phongId, kyTruoc);

        model.addAttribute("chiSoHienTai", chiSoHT);
        model.addAttribute("chiSoTruoc", chiSoTruoc);

        // Tinh % thay doi dien
        Object dienHTObj = chiSoHT.get("dienTieuThu");
        Object dienTruocObj = chiSoTruoc.get("dienTieuThu");
        BigDecimal dienHienTai = dienHTObj != null ? toBigDecimal(dienHTObj) : BigDecimal.ZERO;
        BigDecimal dienTruoc = dienTruocObj != null ? toBigDecimal(dienTruocObj) : BigDecimal.ZERO;
        BigDecimal ptThayDoiDien = tenantThongKeService.tinhPhanTramThayDoi(dienHienTai, dienTruoc);
        model.addAttribute("ptThayDoiDien", ptThayDoiDien);

        // Tinh % thay doi nuoc
        Object nuocHTObj = chiSoHT.get("nuocTieuThu");
        Object nuocTruocObj = chiSoTruoc.get("nuocTieuThu");
        BigDecimal nuocHienTai = nuocHTObj != null ? toBigDecimal(nuocHTObj) : BigDecimal.ZERO;
        BigDecimal nuocTruoc = nuocTruocObj != null ? toBigDecimal(nuocTruocObj) : BigDecimal.ZERO;
        BigDecimal ptThayDoiNuoc = tenantThongKeService.tinhPhanTramThayDoi(nuocHienTai, nuocTruoc);
        model.addAttribute("ptThayDoiNuoc", ptThayDoiNuoc);

        // Tinh % thay doi tien dien
        Object tienDienHTObj = chiSoHT.get("tienDien");
        Object tienDienTruocObj = chiSoTruoc.get("tienDien");
        BigDecimal tienDienHT = tienDienHTObj != null ? toBigDecimal(tienDienHTObj) : BigDecimal.ZERO;
        BigDecimal tienDienTruoc = tienDienTruocObj != null ? toBigDecimal(tienDienTruocObj) : BigDecimal.ZERO;
        BigDecimal ptThayDoiTienDien = tenantThongKeService.tinhPhanTramThayDoi(tienDienHT, tienDienTruoc);
        model.addAttribute("ptThayDoiTienDien", ptThayDoiTienDien);

        // Tinh % thay doi tien nuoc
        Object tienNuocHTObj = chiSoHT.get("tienNuoc");
        Object tienNuocTruocObj = chiSoTruoc.get("tienNuoc");
        BigDecimal tienNuocHT = tienNuocHTObj != null ? toBigDecimal(tienNuocHTObj) : BigDecimal.ZERO;
        BigDecimal tienNuocTruoc = tienNuocTruocObj != null ? toBigDecimal(tienNuocTruocObj) : BigDecimal.ZERO;
        BigDecimal ptThayDoiTienNuoc = tenantThongKeService.tinhPhanTramThayDoi(tienNuocHT, tienNuocTruoc);
        model.addAttribute("ptThayDoiTienNuoc", ptThayDoiTienNuoc);

        // Tong quan tai chinh
        Map<String, Object> taiChinh = tenantThongKeService.getTongQuanTaiChinh(khachThueId);
        model.addAttribute("taiChinh", taiChinh);

        return "tenant/thong-ke/index";
    }

    /**
     * API: Lay chi so dien nuoc theo thang (cho bieu do)
     */
    @GetMapping("/api/chi-so")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChiSoApi(Authentication auth, @RequestParam(defaultValue = "6") int soThang) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return ResponseEntity.notFound().build();

        Long khachThueId = nguoiDung.getUserId();
        var hopDongOpt = tenantThongKeService.getHopDongHieuLuc(khachThueId);
        if (hopDongOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long phongId = hopDongOpt.get().getPhongTro().getPhongId();
        Map<String, Object> data = tenantThongKeService.getChiSoTheoThang(phongId, soThang);
        return ResponseEntity.ok(data);
    }

    /**
     * API: Lay hoa don theo thang (cho bieu do)
     */
    @GetMapping("/api/hoa-don")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHoaDonApi(Authentication auth, @RequestParam(defaultValue = "6") int soThang) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return ResponseEntity.notFound().build();

        Long khachThueId = nguoiDung.getUserId();
        Map<String, Object> data = tenantThongKeService.getHoaDonTheoThang(khachThueId, soThang);
        return ResponseEntity.ok(data);
    }

    /**
     * API: Lay chi so thang hien tai
     */
    @GetMapping("/api/chi-so/hien-tai")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChiSoHienTai(Authentication auth) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return ResponseEntity.notFound().build();

        Long khachThueId = nguoiDung.getUserId();
        var hopDongOpt = tenantThongKeService.getHopDongHieuLuc(khachThueId);
        if (hopDongOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long phongId = hopDongOpt.get().getPhongTro().getPhongId();
        String kyHienTai = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Map<String, Object> data = tenantThongKeService.getChiSoThangHienTai(phongId, kyHienTai);
        return ResponseEntity.ok(data);
    }

    /**
     * Helper: Convert Object to BigDecimal
     */
    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return BigDecimal.valueOf(((Number) obj).doubleValue());
        try {
            return new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
