package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HopDongRepository extends JpaRepository<HopDong, Long> {
    List<HopDong> findByTrangThai(TrangThaiHopDong trangThai);
    List<HopDong> findByKhachThue_UserId(Long khachThueId);
    Optional<HopDong> findByPhongTro_PhongIdAndTrangThai(Long phongId, TrangThaiHopDong trangThai);
    boolean existsByPhongTro_PhongIdAndTrangThai(Long phongId, TrangThaiHopDong trangThai);
    boolean existsByKhachThue_UserIdAndTrangThai(Long khachThueId, TrangThaiHopDong trangThai);
    List<HopDong> findByKhachThue_UserIdAndTrangThai(Long khachThueId, TrangThaiHopDong trangThai);
}
