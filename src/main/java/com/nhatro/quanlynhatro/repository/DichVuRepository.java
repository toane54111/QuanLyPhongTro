package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DichVuRepository extends JpaRepository<DichVu, Long> {
    Optional<DichVu> findByTenDV(String tenDV);
    boolean existsByTenDV(String tenDV);
}
