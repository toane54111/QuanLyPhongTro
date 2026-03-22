package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "chi_so_dien_nuoc", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"phong_id", "kyGhi"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChiSoDienNuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chiSoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", nullable = false)
    private PhongTro phongTro;

    @Column(nullable = false, length = 7)
    private String kyGhi; // VD: 2026-03

    @Column(nullable = false)
    private Integer dienCu = 0;

    @Column(nullable = false)
    private Integer dienMoi = 0;

    @Column(nullable = false)
    private Integer nuocCu = 0;

    @Column(nullable = false)
    private Integer nuocMoi = 0;

    private LocalDate ngayGhi;

    // Computed
    @Transient
    public int getDienTieuThu() {
        return dienMoi - dienCu;
    }

    @Transient
    public int getNuocTieuThu() {
        return nuocMoi - nuocCu;
    }
}
