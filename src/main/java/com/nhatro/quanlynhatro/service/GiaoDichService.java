package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.repository.GiaoDichRepository;
import com.nhatro.quanlynhatro.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiaoDichService {

    private final GiaoDichRepository giaoDichRepository;
    private final HoaDonRepository hoaDonRepository;

    public List<GiaoDich> findAll() {
        return giaoDichRepository.findAll();
    }

    public Optional<GiaoDich> findById(Long id) {
        return giaoDichRepository.findById(id);
    }

    public List<GiaoDich> findByHoaDonId(Long hoaDonId) {
        return giaoDichRepository.findByHoaDon_HoaDonId(hoaDonId);
    }

    @Transactional
    public GiaoDich recordPayment(Long hoaDonId, BigDecimal soTien, PhuongThucThanhToan phuongThuc, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay hoa don voi ID: " + hoaDonId));

        if (hoaDon.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new RuntimeException("Hoa don nay da duoc thanh toan day du");
        }

        if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("So tien thanh toan phai lon hon 0");
        }

        // Create transaction
        GiaoDich giaoDich = GiaoDich.builder()
                .hoaDon(hoaDon)
                .soTien(soTien)
                .phuongThuc(phuongThuc)
                .ngayGiaoDich(LocalDateTime.now())
                .ghiChu(ghiChu)
                .build();

        GiaoDich saved = giaoDichRepository.save(giaoDich);

        // Calculate total paid
        BigDecimal totalPaid = giaoDichRepository.sumSoTienByHoaDonId(hoaDonId);

        // Update invoice status
        if (totalPaid.compareTo(hoaDon.getTongTien()) >= 0) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        } else {
            hoaDon.setTrangThai(TrangThaiHoaDon.THANH_TOAN_MOT_PHAN);
        }
        hoaDonRepository.save(hoaDon);

        return saved;
    }

    public BigDecimal getTotalPaidForInvoice(Long hoaDonId) {
        return giaoDichRepository.sumSoTienByHoaDonId(hoaDonId);
    }
}
