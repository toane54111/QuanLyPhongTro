// === FILE: NguoiDungRepository.java ===
package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findByCccd(String cccd);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
}
