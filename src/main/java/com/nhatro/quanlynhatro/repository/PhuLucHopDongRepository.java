package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.PhuLucHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhuLucHopDongRepository extends JpaRepository<PhuLucHopDong, Long> {

    List<PhuLucHopDong> findByHopDong_HopDongId(Long hopDongId);

    List<PhuLucHopDong> findByHopDong_HopDongIdAndTrangThai(Long hopDongId, TrangThaiYeuCau trangThai);

    boolean existsByHopDong_HopDongIdAndTrangThai(Long hopDongId, TrangThaiYeuCau trangThai);

    /**
     * Tìm phụ lục đã duyệt có ngayHieuLuc gần nhất trước hoặc bằng tháng tính tiền
     * Dùng để xác định giá thuê áp dụng cho kỳ thanh toán
     */
    @Query("SELECT p FROM PhuLucHopDong p WHERE p.hopDong.hopDongId = :hopDongId " +
           "AND p.trangThai = 'DA_PHE_DUYET' AND p.ngayHieuLuc IS NOT NULL " +
           "ORDER BY p.ngayHieuLuc DESC")
    List<PhuLucHopDong> findApprovedByHopDongOrderByNgayHieuLucDesc(@Param("hopDongId") Long hopDongId);

    /**
     * Tìm phụ lục đã duyệt mới nhất của hợp đồng
     */
    default Optional<PhuLucHopDong> findLatestApproved(Long hopDongId) {
        List<PhuLucHopDong> list = findApprovedByHopDongOrderByNgayHieuLucDesc(hopDongId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
