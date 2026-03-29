package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.enums.TrangThaiGiaoDich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface GiaoDichRepository extends JpaRepository<GiaoDich, Long> {
    List<GiaoDich> findByHoaDon_HoaDonId(Long hoaDonId);

    // Chỉ tính tổng giao dịch ĐÃ XÁC NHẬN (hoặc null = record cũ)
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g WHERE g.hoaDon.hoaDonId = :hoaDonId AND (g.trangThaiGD = 'DA_XAC_NHAN' OR g.trangThaiGD IS NULL)")
    BigDecimal sumSoTienByHoaDonId(Long hoaDonId);

    // Tổng tiền đã thanh toán của khách (chỉ đã xác nhận hoặc null = record cũ)
    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDich g " +
           "WHERE g.hoaDon.hopDong.khachThue.userId = :khachThueId AND (g.trangThaiGD = 'DA_XAC_NHAN' OR g.trangThaiGD IS NULL)")
    BigDecimal sumTotalPaidByKhachThue(Long khachThueId);

    // Lịch sử giao dịch của khách, sắp xếp mới nhất
    @Query("SELECT g FROM GiaoDich g WHERE g.hoaDon.hopDong.khachThue.userId = :khachThueId " +
           "ORDER BY g.ngayGiaoDich DESC")
    List<GiaoDich> findByKhachThueOrderByNgayDesc(Long khachThueId);

    // Giao dịch chờ xác nhận của một hóa đơn
    List<GiaoDich> findByHoaDon_HoaDonIdAndTrangThaiGD(Long hoaDonId, TrangThaiGiaoDich trangThaiGD);

    // Tất cả giao dịch chờ xác nhận (cho chủ trọ xem)
    @Query("SELECT g FROM GiaoDich g WHERE g.trangThaiGD = 'CHO_XAC_NHAN' ORDER BY g.ngayGiaoDich DESC")
    List<GiaoDich> findAllPending();
}
