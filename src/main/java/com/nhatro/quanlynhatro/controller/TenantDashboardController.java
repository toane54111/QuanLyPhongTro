package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tenant")
public class TenantDashboardController {

    private final NguoiDungRepository nguoiDungRepo;
    private final HopDongRepository hopDongRepo;
    private final HoaDonRepository hoaDonRepo;
    private final YeuCauSuCoRepository suCoRepo;

    public TenantDashboardController(NguoiDungRepository nguoiDungRepo,
                                      HopDongRepository hopDongRepo,
                                      HoaDonRepository hoaDonRepo,
                                      YeuCauSuCoRepository suCoRepo) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.hopDongRepo = hopDongRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.suCoRepo = suCoRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(auth.getName()).orElse(null);
        if (nguoiDung == null) return "redirect:/login";

        Long userId = nguoiDung.getUserId();

        List<HopDong> hopDongHieuLuc = hopDongRepo.findByKhachThue_UserIdAndTrangThai(
                userId, TrangThaiHopDong.DANG_HIEU_LUC);

        long hoaDonChuaTT = hoaDonRepo.findByKhachThueOrderByNgayTaoDesc(userId).stream()
                .filter(h -> h.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN)
                .count();

        long suCoDaGui = suCoRepo.findByKhachThue_UserId(userId).size();

        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("hopDongHieuLuc", hopDongHieuLuc);
        model.addAttribute("soHopDong", hopDongHieuLuc.size());
        model.addAttribute("hoaDonChuaTT", hoaDonChuaTT);
        model.addAttribute("suCoDaGui", suCoDaGui);

        return "tenant/dashboard";
    }
}
