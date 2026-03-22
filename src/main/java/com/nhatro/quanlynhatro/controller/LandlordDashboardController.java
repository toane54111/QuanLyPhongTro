package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.enums.*;
import com.nhatro.quanlynhatro.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/landlord")
public class LandlordDashboardController {

    private final PhongTroRepository phongTroRepo;
    private final HopDongRepository hopDongRepo;
    private final HoaDonRepository hoaDonRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final YeuCauSuCoRepository suCoRepo;

    public LandlordDashboardController(PhongTroRepository phongTroRepo,
                                        HopDongRepository hopDongRepo,
                                        HoaDonRepository hoaDonRepo,
                                        NguoiDungRepository nguoiDungRepo,
                                        YeuCauSuCoRepository suCoRepo) {
        this.phongTroRepo = phongTroRepo;
        this.hopDongRepo = hopDongRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.nguoiDungRepo = nguoiDungRepo;
        this.suCoRepo = suCoRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long tongPhong = phongTroRepo.count();
        long phongTrong = phongTroRepo.findByTrangThai(TrangThaiPhong.TRONG).size();
        long phongDaThue = phongTroRepo.findByTrangThai(TrangThaiPhong.DA_THUE).size();
        long phongBaoTri = phongTroRepo.findByTrangThai(TrangThaiPhong.BAO_TRI).size();
        long hopDongHieuLuc = hopDongRepo.findByTrangThai(TrangThaiHopDong.DANG_HIEU_LUC).size();
        long hoaDonChuaTT = hoaDonRepo.findByTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN).size();
        long tongKhachThue = nguoiDungRepo.findByVaiTro(VaiTro.KHACH_THUE).size();
        long suCoMoi = suCoRepo.findByTrangThai(TrangThaiSuCo.MOI).size();

        double tyLeLapDay = tongPhong > 0 ? (double) phongDaThue / tongPhong * 100 : 0;

        model.addAttribute("tongPhong", tongPhong);
        model.addAttribute("phongTrong", phongTrong);
        model.addAttribute("phongDaThue", phongDaThue);
        model.addAttribute("phongBaoTri", phongBaoTri);
        model.addAttribute("hopDongHieuLuc", hopDongHieuLuc);
        model.addAttribute("hoaDonChuaTT", hoaDonChuaTT);
        model.addAttribute("tongKhachThue", tongKhachThue);
        model.addAttribute("suCoMoi", suCoMoi);
        model.addAttribute("tyLeLapDay", String.format("%.0f", tyLeLapDay));

        return "landlord/dashboard";
    }
}
