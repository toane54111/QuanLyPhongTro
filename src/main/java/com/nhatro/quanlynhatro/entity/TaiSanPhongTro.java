package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tai_san_phong_tro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaiSanPhongTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taiSanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private PhongTro phongTro;

    @Column(nullable = false, length = 200)
    private String tenTaiSan;

    @Column(length = 100)
    private String tinhTrang;
}
