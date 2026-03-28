package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "thanh_vien_phong_tro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThanhVienPhongTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long thanhVienId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private PhongTro phongTro;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(length = 20)
    private String sdt;

    @Column(length = 20)
    private String cccd;

    @Column(nullable = false)
    private LocalDate ngayBatDau;

    @Column(nullable = false)
    private String trangThai; // "Đang ở", "Đã rời đi"
}
