package com.nhatro.quanlynhatro.repository;

import com.nhatro.quanlynhatro.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Long> {
    List<ThongBao> findByNguoiNhan_UserIdOrderByNgayTaoDesc(Long userId);
    List<ThongBao> findByNguoiNhan_UserIdAndDaDocFalseOrderByNgayTaoDesc(Long userId);
    long countByNguoiNhan_UserIdAndDaDocFalse(Long userId);
}
