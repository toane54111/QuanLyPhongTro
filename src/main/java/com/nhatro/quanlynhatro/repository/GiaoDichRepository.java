package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface GiaoDichRepository extends JpaRepository<GiaoDich, Long> {
    List<GiaoDich> findByHoaDon_HoaDonId(Long hoaDonId);

    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g WHERE g.hoaDon.hoaDonId = :hoaDonId")
    BigDecimal sumSoTienByHoaDonId(Long hoaDonId);

    // Tổng tiền đã thanh toán của khách (qua tất cả hóa đơn)
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g " +
           "WHERE g.hoaDon.hopDong.khachThue.userId = :khachThueId")
    BigDecimal sumTotalPaidByKhachThue(Long khachThueId);

    // Lịch sử giao dịch của khách, sắp xếp mới nhất
    @Query("SELECT g FROM GiaoDich g WHERE g.hoaDon.hopDong.khachThue.userId = :khachThueId " +
           "ORDER BY g.ngayGiaoDich DESC")
    List<GiaoDich> findByKhachThueOrderByNgayDesc(Long khachThueId);
}
