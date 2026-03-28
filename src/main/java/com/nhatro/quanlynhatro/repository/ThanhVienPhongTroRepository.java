package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.ThanhVienPhongTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhVienPhongTroRepository extends JpaRepository<ThanhVienPhongTro, Long> {
    List<ThanhVienPhongTro> findByPhongTro_PhongId(Long phongId);
}
