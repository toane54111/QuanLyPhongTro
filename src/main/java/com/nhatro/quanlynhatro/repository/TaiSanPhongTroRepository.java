package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.TaiSanPhongTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanPhongTroRepository extends JpaRepository<TaiSanPhongTro, Long> {
    List<TaiSanPhongTro> findByPhongTro_PhongId(Long phongId);
}
