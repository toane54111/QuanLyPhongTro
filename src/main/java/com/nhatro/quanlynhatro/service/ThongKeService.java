package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.repository.HoaDonRepository;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final HopDongRepository hopDongRepository;
    private final PhongTroRepository phongTroRepository;

    public Map<String, Object> getDoanhThuTheoThang(Integer nam) {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            String kyThanhToan = String.format("%d-%02d", nam, month);
            labels.add("Tháng " + month);

            BigDecimal revenue = hoaDonRepository.findAll().stream()
                    .filter(hd -> kyThanhToan.equals(hd.getKyThanhToan()))
                    .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
                    .map(HoaDon::getTongTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.add(revenue);
        }

        result.put("labels", labels);
        result.put("data", data);
        result.put("nam", nam);
        return result;
    }

    public BigDecimal getTyLeLapDay() {
        long totalRooms = phongTroRepository.count();
        if (totalRooms == 0) return BigDecimal.ZERO;
        long rentedRooms = phongTroRepository.findByTrangThai(TrangThaiPhong.DA_THUE).size();
        return BigDecimal.valueOf(rentedRooms)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP);
    }

    public Map<String, Object> getThongKeTongQuan() {
        Map<String, Object> stats = new HashMap<>();
        long tongPhong = phongTroRepository.count();
        long phongDaThue = phongTroRepository.findByTrangThai(TrangThaiPhong.DA_THUE).size();
        long phongTrong = phongTroRepository.findByTrangThai(TrangThaiPhong.TRONG).size();
        long phongBaoTri = phongTroRepository.findByTrangThai(TrangThaiPhong.BAO_TRI).size();
        long hopDongHieuLuc = hopDongRepository.findByTrangThai(TrangThaiHopDong.DANG_HIEU_LUC).size();
        long hoaDonChuaThanhToan = hoaDonRepository.findByTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN).size();

        stats.put("tongPhong", tongPhong);
        stats.put("phongDaThue", phongDaThue);
        stats.put("phongTrong", phongTrong);
        stats.put("phongBaoTri", phongBaoTri);
        stats.put("hopDongHieuLuc", hopDongHieuLuc);
        stats.put("hoaDonChuaThanhToan", hoaDonChuaThanhToan);
        stats.put("tyLeLapDay", getTyLeLapDay());
        return stats;
    }
}
