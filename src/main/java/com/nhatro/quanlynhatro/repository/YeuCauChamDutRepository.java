package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.YeuCauChamDut;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface YeuCauChamDutRepository extends JpaRepository<YeuCauChamDut, Long> {
    List<YeuCauChamDut> findByTrangThai(TrangThaiYeuCau trangThai);
    boolean existsByKhachThue_UserIdAndTrangThai(Long khachThueId, TrangThaiYeuCau trangThai);
}
