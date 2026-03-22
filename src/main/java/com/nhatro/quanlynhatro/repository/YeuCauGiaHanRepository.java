package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.YeuCauGiaHan;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface YeuCauGiaHanRepository extends JpaRepository<YeuCauGiaHan, Long> {
    List<YeuCauGiaHan> findByTrangThai(TrangThaiYeuCau trangThai);
    boolean existsByKhachThue_UserIdAndTrangThai(Long khachThueId, TrangThaiYeuCau trangThai);
}
