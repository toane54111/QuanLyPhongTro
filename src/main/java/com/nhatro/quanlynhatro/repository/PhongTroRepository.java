package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhongTroRepository extends JpaRepository<PhongTro, Long> {
    List<PhongTro> findByKhuTro_KhuTroId(Long khuTroId);
    List<PhongTro> findByTrangThai(TrangThaiPhong trangThai);
    List<PhongTro> findByKhuTro_KhuTroIdAndTrangThai(Long khuTroId, TrangThaiPhong trangThai);
    boolean existsByKhuTro_KhuTroIdAndSoPhong(Long khuTroId, String soPhong);
}
