package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.DichVu;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.repository.ChiSoDienNuocRepository;
import com.nhatro.quanlynhatro.repository.DichVuRepository;
import com.nhatro.quanlynhatro.repository.HoaDonRepository;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TenantThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final HopDongRepository hopDongRepository;
    private final ChiSoDienNuocRepository chiSoDienNuocRepository;
    private final DichVuRepository dichVuRepository;

    /**
     * Lay thong tin chi so dien nuoc thang hien tai cho 1 phong
     */
    public Map<String, Object> getChiSoThangHienTai(Long phongId, String kyGhi) {
        Map<String, Object> result = new HashMap<>();
        Optional<ChiSoDienNuoc> chiSoOpt = chiSoDienNuocRepository.findByPhongTro_PhongIdAndKyGhi(phongId, kyGhi);

        if (chiSoOpt.isEmpty()) {
            result.put("coDuLieu", false);
            result.put("dienCu", 0);
            result.put("dienMoi", 0);
            result.put("dienTieuThu", 0);
            result.put("nuocCu", 0);
            result.put("nuocMoi", 0);
            result.put("nuocTieuThu", 0);
            result.put("donGiaDien", BigDecimal.ZERO);
            result.put("donGiaNuoc", BigDecimal.ZERO);
            result.put("tienDien", BigDecimal.ZERO);
            result.put("tienNuoc", BigDecimal.ZERO);
            return result;
        }

        ChiSoDienNuoc chiSo = chiSoOpt.get();
        BigDecimal donGiaDien = getDonGiaDichVu("Điện");
        BigDecimal donGiaNuoc = getDonGiaDichVu("Nước");

        result.put("coDuLieu", true);
        result.put("kyGhi", kyGhi);
        result.put("dienCu", chiSo.getDienCu());
        result.put("dienMoi", chiSo.getDienMoi());
        result.put("dienTieuThu", chiSo.getDienTieuThu());
        result.put("nuocCu", chiSo.getNuocCu());
        result.put("nuocMoi", chiSo.getNuocMoi());
        result.put("nuocTieuThu", chiSo.getNuocTieuThu());
        result.put("donGiaDien", donGiaDien);
        result.put("donGiaNuoc", donGiaNuoc);
        result.put("tienDien", donGiaDien.multiply(BigDecimal.valueOf(chiSo.getDienTieuThu())));
        result.put("tienNuoc", donGiaNuoc.multiply(BigDecimal.valueOf(chiSo.getNuocTieuThu())));

        return result;
    }

    /**
     * Lay thong tin chi so dien nuoc thang truoc de so sanh
     */
    public Map<String, Object> getChiSoThangTruoc(Long phongId, String kyGhiHienTai) {
        String[] parts = kyGhiHienTai.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        month--;
        if (month == 0) {
            month = 12;
            year--;
        }
        String kyGhiTruoc = String.format("%d-%02d", year, month);

        return getChiSoThangHienTai(phongId, kyGhiTruoc);
    }

    /**
     * Lay danh sach chi so dien nuoc theo thang (cho bieu do)
     */
    public Map<String, Object> getChiSoTheoThang(Long phongId, int soThang) {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> dienData = new ArrayList<>();
        List<Integer> nuocData = new ArrayList<>();
        List<BigDecimal> tienDienData = new ArrayList<>();
        List<BigDecimal> tienNuocData = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -soThang + 1);

        BigDecimal donGiaDien = getDonGiaDichVu("Điện");
        BigDecimal donGiaNuoc = getDonGiaDichVu("Nước");

        for (int i = 0; i < soThang; i++) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String kyGhi = String.format("%d-%02d", year, month);

            labels.add("T" + month);
            Optional<ChiSoDienNuoc> chiSoOpt = chiSoDienNuocRepository.findByPhongTro_PhongIdAndKyGhi(phongId, kyGhi);

            if (chiSoOpt.isPresent()) {
                ChiSoDienNuoc chiSo = chiSoOpt.get();
                dienData.add(chiSo.getDienTieuThu());
                nuocData.add(chiSo.getNuocTieuThu());
                tienDienData.add(donGiaDien.multiply(BigDecimal.valueOf(chiSo.getDienTieuThu())));
                tienNuocData.add(donGiaNuoc.multiply(BigDecimal.valueOf(chiSo.getNuocTieuThu())));
            } else {
                dienData.add(0);
                nuocData.add(0);
                tienDienData.add(BigDecimal.ZERO);
                tienNuocData.add(BigDecimal.ZERO);
            }

            cal.add(Calendar.MONTH, 1);
        }

        result.put("labels", labels);
        result.put("dienData", dienData);
        result.put("nuocData", nuocData);
        result.put("tienDienData", tienDienData);
        result.put("tienNuocData", tienNuocData);
        result.put("donGiaDien", donGiaDien);
        result.put("donGiaNuoc", donGiaNuoc);

        return result;
    }

    /**
     * Lay danh sach hoa don theo thang (cho bieu do)
     */
    public Map<String, Object> getHoaDonTheoThang(Long khachThueId, int soThang) {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> tongTienData = new ArrayList<>();
        List<BigDecimal> tienPhongData = new ArrayList<>();
        List<BigDecimal> tienDienData = new ArrayList<>();
        List<BigDecimal> tienNuocData = new ArrayList<>();
        List<BigDecimal> phiDichVuData = new ArrayList<>();
        List<Integer> daThanhToanData = new ArrayList<>();
        List<Integer> chuaThanhToanData = new ArrayList<>();
        List<Integer> quaHanData = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -soThang + 1);

        for (int i = 0; i < soThang; i++) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String kyThanhToan = String.format("%d-%02d", year, month);

            labels.add("T" + month);

            List<HoaDon> hoaDons = hoaDonRepository.findByKhachThueAndKyThanhToan(khachThueId, kyThanhToan);

            if (!hoaDons.isEmpty()) {
                HoaDon hoaDon = hoaDons.get(0);
                tongTienData.add(hoaDon.getTongTien());
                tienPhongData.add(hoaDon.getTienPhong());
                tienDienData.add(hoaDon.getTienDien());
                tienNuocData.add(hoaDon.getTienNuoc());
                phiDichVuData.add(hoaDon.getPhiDichVu() != null ? hoaDon.getPhiDichVu() : BigDecimal.ZERO);

                switch (hoaDon.getTrangThai()) {
                    case DA_THANH_TOAN -> {
                        daThanhToanData.add(1);
                        chuaThanhToanData.add(0);
                        quaHanData.add(0);
                    }
                    case CHUA_THANH_TOAN, THANH_TOAN_MOT_PHAN -> {
                        boolean quaHan = hoaDon.getHanThanhToan() != null
                                && hoaDon.getHanThanhToan().isBefore(LocalDate.now());
                        daThanhToanData.add(0);
                        if (quaHan) {
                            chuaThanhToanData.add(0);
                            quaHanData.add(1);
                        } else {
                            chuaThanhToanData.add(1);
                            quaHanData.add(0);
                        }
                    }
                }
            } else {
                tongTienData.add(BigDecimal.ZERO);
                tienPhongData.add(BigDecimal.ZERO);
                tienDienData.add(BigDecimal.ZERO);
                tienNuocData.add(BigDecimal.ZERO);
                phiDichVuData.add(BigDecimal.ZERO);
                daThanhToanData.add(0);
                chuaThanhToanData.add(0);
                quaHanData.add(0);
            }

            cal.add(Calendar.MONTH, 1);
        }

        result.put("labels", labels);
        result.put("tongTienData", tongTienData);
        result.put("tienPhongData", tienPhongData);
        result.put("tienDienData", tienDienData);
        result.put("tienNuocData", tienNuocData);
        result.put("phiDichVuData", phiDichVuData);
        result.put("daThanhToanData", daThanhToanData);
        result.put("chuaThanhToanData", chuaThanhToanData);
        result.put("quaHanData", quaHanData);

        return result;
    }

    /**
     * Lay tong quan tai chinh (da thanh toan, con no)
     */
    public Map<String, Object> getTongQuanTaiChinh(Long khachThueId) {
        Map<String, Object> result = new HashMap<>();

        List<HoaDon> allInvoices = hoaDonRepository.findByHopDong_KhachThue_UserId(khachThueId);

        BigDecimal tongDaThanhToan = BigDecimal.ZERO;
        BigDecimal tongConNo = BigDecimal.ZERO;
        BigDecimal tongTatCa = BigDecimal.ZERO;
        int soHoaDonDaTT = 0;
        int soHoaDonChuaTT = 0;

        for (HoaDon hd : allInvoices) {
            tongTatCa = tongTatCa.add(hd.getTongTien());
            BigDecimal daTra = hd.getDaThanhToan();
            BigDecimal conNo = hd.getConNo();

            tongDaThanhToan = tongDaThanhToan.add(daTra);
            if (conNo.compareTo(BigDecimal.ZERO) > 0) {
                tongConNo = tongConNo.add(conNo);
                soHoaDonChuaTT++;
            } else {
                soHoaDonDaTT++;
            }
        }

        result.put("tongDaThanhToan", tongDaThanhToan);
        result.put("tongConNo", tongConNo);
        result.put("tongTatCa", tongTatCa);
        result.put("soHoaDonDaTT", soHoaDonDaTT);
        result.put("soHoaDonChuaTT", soHoaDonChuaTT);

        return result;
    }

    /**
     * Lay hop dong hieu luc cua khach thue (de lay phongId)
     */
    public Optional<HopDong> getHopDongHieuLuc(Long khachThueId) {
        List<HopDong> hopDongs = hopDongRepository.findByKhachThue_UserIdAndTrangThai(
                khachThueId,
                com.nhatro.quanlynhatro.enums.TrangThaiHopDong.DANG_HIEU_LUC
        );
        return hopDongs.isEmpty() ? Optional.empty() : Optional.of(hopDongs.get(0));
    }

    /**
     * Helper: Lay don gia dich vu
     */
    private BigDecimal getDonGiaDichVu(String tenDV) {
        return dichVuRepository.findByTenDV(tenDV)
                .map(DichVu::getDonGia)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Tinh % thay doi giua 2 gia tri
     */
    public BigDecimal tinhPhanTramThayDoi(BigDecimal giaTriHienTai, BigDecimal giaTriTruoc) {
        if (giaTriTruoc == null || giaTriTruoc.compareTo(BigDecimal.ZERO) == 0) {
            if (giaTriHienTai != null && giaTriHienTai.compareTo(BigDecimal.ZERO) > 0) {
                return BigDecimal.valueOf(100);
            }
            return BigDecimal.ZERO;
        }
        return giaTriHienTai.subtract(giaTriTruoc)
                .divide(giaTriTruoc, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, java.math.RoundingMode.HALF_UP);
    }
}
