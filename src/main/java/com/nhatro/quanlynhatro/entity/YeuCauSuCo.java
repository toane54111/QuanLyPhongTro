package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.MucDoUuTien;
import com.nhatro.quanlynhatro.enums.TrangThaiSuCo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_su_co")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class YeuCauSuCo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private PhongTro phongTro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id", nullable = false)
    private NguoiDung khachThue;

    @Column(length = 50)
    private String loaiSuCo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String moTa;

    @Column(length = 500)
    private String hinhAnh;

    @Enumerated(EnumType.STRING)
    private MucDoUuTien mucDoUuTien = MucDoUuTien.TRUNG_BINH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiSuCo trangThai = TrangThaiSuCo.MOI;

    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String ghiChuXuLy;
}
