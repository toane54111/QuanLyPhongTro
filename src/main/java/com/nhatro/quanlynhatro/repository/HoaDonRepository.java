package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    List<HoaDon> findByTrangThai(TrangThaiHoaDon trangThai);
    List<HoaDon> findByHopDong_HopDongId(Long hopDongId);
    List<HoaDon> findByHopDong_KhachThue_UserId(Long khachThueId);
    boolean existsByHopDong_HopDongIdAndKyThanhToan(Long hopDongId, String kyThanhToan);
    List<HoaDon> findByHopDong_HopDongIdAndTrangThai(Long hopDongId, TrangThaiHoaDon trangThai);

    @Query("SELECT h FROM HoaDon h WHERE h.hopDong.khachThue.userId = :khachThueId ORDER BY h.ngayTao DESC")
    List<HoaDon> findByKhachThueOrderByNgayTaoDesc(Long khachThueId);

    // Hóa đơn chưa thanh toán / thanh toán một phần của khách
    @Query("SELECT h FROM HoaDon h WHERE h.hopDong.khachThue.userId = :khachThueId " +
           "AND h.trangThai <> 'DA_THANH_TOAN' ORDER BY h.hanThanhToan ASC")
    List<HoaDon> findUnpaidByKhachThue(Long khachThueId);

    // Hóa đơn đã thanh toán (lịch sử)
    @Query("SELECT h FROM HoaDon h WHERE h.hopDong.khachThue.userId = :khachThueId " +
           "AND h.trangThai = 'DA_THANH_TOAN' ORDER BY h.kyThanhToan DESC")
    List<HoaDon> findPaidByKhachThue(Long khachThueId);

    // Tổng tiền tất cả hóa đơn của khách
    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.hopDong.khachThue.userId = :khachThueId")
    java.math.BigDecimal sumTongTienByKhachThue(Long khachThueId);

    // Hóa đơn theo kỳ thanh toán của khách
    @Query("SELECT h FROM HoaDon h WHERE h.hopDong.khachThue.userId = :khachThueId " +
           "AND h.kyThanhToan = :kyThanhToan")
    List<HoaDon> findByKhachThueAndKyThanhToan(Long khachThueId, String kyThanhToan);
}
