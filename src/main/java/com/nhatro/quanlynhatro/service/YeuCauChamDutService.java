package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.entity.YeuCauChamDut;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.repository.GiaoDichRepository;
import com.nhatro.quanlynhatro.repository.HoaDonRepository;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import com.nhatro.quanlynhatro.repository.YeuCauChamDutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YeuCauChamDutService {

    private final YeuCauChamDutRepository yeuCauChamDutRepository;
    private final HopDongRepository hopDongRepository;
    private final PhongTroRepository phongTroRepository;
    private final HoaDonRepository hoaDonRepository;
    private final GiaoDichRepository giaoDichRepository;

    public List<YeuCauChamDut> findAll() {
        return yeuCauChamDutRepository.findAll();
    }

    public Optional<YeuCauChamDut> findById(Long id) {
        return yeuCauChamDutRepository.findById(id);
    }

    public List<YeuCauChamDut> findByTrangThai(TrangThaiYeuCau trangThai) {
        return yeuCauChamDutRepository.findByTrangThai(trangThai);
    }

    public YeuCauChamDut save(YeuCauChamDut yeuCauChamDut) {
        if (yeuCauChamDut.getNgayTao() == null) {
            yeuCauChamDut.setNgayTao(LocalDateTime.now());
        }
        return yeuCauChamDutRepository.save(yeuCauChamDut);
    }

    @Transactional
    public YeuCauChamDut create(YeuCauChamDut yeuCau) {
        Long khachThueId = yeuCau.getKhachThue().getUserId();

        // Validate tenant has active contract
        if (!hopDongRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiHopDong.DANG_HIEU_LUC)) {
            throw new RuntimeException("Khach thue khong co hop dong dang hieu luc");
        }

        // Validate no pending termination request
        if (yeuCauChamDutRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiYeuCau.CHO_PHE_DUYET)) {
            throw new RuntimeException("Khach thue da co yeu cau cham dut dang cho phe duyet");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.CHO_PHE_DUYET);
        yeuCau.setNgayTao(LocalDateTime.now());
        return yeuCauChamDutRepository.save(yeuCau);
    }

    @Transactional
    public YeuCauChamDut approve(Long yeuCauId) {
        YeuCauChamDut yeuCau = yeuCauChamDutRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu chấm dứt với ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó");
        }

        // Approve the request
        yeuCau.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        yeuCauChamDutRepository.save(yeuCau);

        // <<include>> UC11: Thực hiện chấm dứt hợp đồng
        HopDong hopDong = yeuCau.getHopDong();

        // Bước 1: Kiểm tra hóa đơn chưa thanh toán
        List<HoaDon> hoaDonChuaTT = hoaDonRepository.findByHopDong_HopDongIdAndTrangThai(
                hopDong.getHopDongId(), TrangThaiHoaDon.CHUA_THANH_TOAN);
        List<HoaDon> hoaDonMotPhan = hoaDonRepository.findByHopDong_HopDongIdAndTrangThai(
                hopDong.getHopDongId(), TrangThaiHoaDon.THANH_TOAN_MOT_PHAN);

        // Bước 2: Tính tổng nợ chưa thanh toán
        BigDecimal tongNo = BigDecimal.ZERO;
        for (HoaDon hd : hoaDonChuaTT) {
            tongNo = tongNo.add(hd.getTongTien());
        }
        for (HoaDon hd : hoaDonMotPhan) {
            tongNo = tongNo.add(hd.getConNo());
        }

        // Bước 3: Tính tiền cọc hoàn trả = Tiền cọc - Nợ chưa thanh toán
        BigDecimal tienCoc = hopDong.getTienCoc() != null ? hopDong.getTienCoc() : BigDecimal.ZERO;
        BigDecimal tienHoanCoc = tienCoc.subtract(tongNo);
        if (tienHoanCoc.compareTo(BigDecimal.ZERO) < 0) {
            tienHoanCoc = BigDecimal.ZERO;
        }

        // Bước 4: Cập nhật hợp đồng → "Đã chấm dứt"
        hopDong.setTrangThai(TrangThaiHopDong.DA_CHAM_DUT);
        hopDongRepository.save(hopDong);

        // Bước 5: Cập nhật phòng → "Trống"
        PhongTro phongTro = hopDong.getPhongTro();
        phongTro.setTrangThai(TrangThaiPhong.TRONG);
        phongTroRepository.save(phongTro);

        // Bước 6: Ghi nhận giao dịch hoàn cọc
        if (tienHoanCoc.compareTo(BigDecimal.ZERO) > 0) {
            GiaoDich giaoDichHoanCoc = GiaoDich.builder()
                    .hoaDon(null)
                    .soTien(tienHoanCoc)
                    .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                    .ngayGiaoDich(LocalDateTime.now())
                    .ghiChu("Hoàn cọc hợp đồng phòng " + phongTro.getSoPhong()
                            + " - Cọc: " + tienCoc + " - Nợ: " + tongNo
                            + " - Hoàn: " + tienHoanCoc)
                    .build();
            giaoDichRepository.save(giaoDichHoanCoc);
        }

        return yeuCau;
    }

    /**
     * Tính tiền hoàn cọc dự kiến cho yêu cầu chấm dứt
     */
    public BigDecimal tinhTienHoanCoc(Long yeuCauId) {
        YeuCauChamDut yeuCau = yeuCauChamDutRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        HopDong hopDong = yeuCau.getHopDong();

        BigDecimal tongNo = BigDecimal.ZERO;
        List<HoaDon> hoaDonChuaTT = hoaDonRepository.findByHopDong_HopDongIdAndTrangThai(
                hopDong.getHopDongId(), TrangThaiHoaDon.CHUA_THANH_TOAN);
        List<HoaDon> hoaDonMotPhan = hoaDonRepository.findByHopDong_HopDongIdAndTrangThai(
                hopDong.getHopDongId(), TrangThaiHoaDon.THANH_TOAN_MOT_PHAN);
        for (HoaDon hd : hoaDonChuaTT) {
            tongNo = tongNo.add(hd.getTongTien());
        }
        for (HoaDon hd : hoaDonMotPhan) {
            tongNo = tongNo.add(hd.getConNo());
        }

        BigDecimal tienCoc = hopDong.getTienCoc() != null ? hopDong.getTienCoc() : BigDecimal.ZERO;
        BigDecimal hoanCoc = tienCoc.subtract(tongNo);
        return hoanCoc.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : hoanCoc;
    }

    @Transactional
    public YeuCauChamDut reject(Long yeuCauId, String lyDo) {
        YeuCauChamDut yeuCau = yeuCauChamDutRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau cham dut voi ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yeu cau nay da duoc xu ly truoc do");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.DA_TU_CHOI);
        yeuCau.setLyDoTuChoi(lyDo);
        return yeuCauChamDutRepository.save(yeuCau);
    }
}
