package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.YeuCauSuCo;
import com.nhatro.quanlynhatro.enums.TrangThaiSuCo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface YeuCauSuCoRepository extends JpaRepository<YeuCauSuCo, Long> {
    List<YeuCauSuCo> findByTrangThai(TrangThaiSuCo trangThai);
    List<YeuCauSuCo> findByKhachThue_UserId(Long khachThueId);
    List<YeuCauSuCo> findByPhongTro_PhongId(Long phongId);
    List<YeuCauSuCo> findAllByOrderByNgayTaoDesc();
}
