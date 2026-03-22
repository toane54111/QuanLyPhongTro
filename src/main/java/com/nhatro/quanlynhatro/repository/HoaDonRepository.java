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
}
