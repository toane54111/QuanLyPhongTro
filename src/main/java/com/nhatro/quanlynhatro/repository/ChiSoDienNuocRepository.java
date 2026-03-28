package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChiSoDienNuocRepository extends JpaRepository<ChiSoDienNuoc, Long> {
    List<ChiSoDienNuoc> findByKyGhi(String kyGhi);
    Optional<ChiSoDienNuoc> findByPhongTro_PhongIdAndKyGhi(Long phongId, String kyGhi);
    boolean existsByPhongTro_PhongIdAndKyGhi(Long phongId, String kyGhi);
    Optional<ChiSoDienNuoc> findTopByPhongTro_PhongIdOrderByKyGhiDesc(Long phongId);
    List<ChiSoDienNuoc> findByPhongTro_PhongIdOrderByKyGhiDesc(Long phongId);
}
