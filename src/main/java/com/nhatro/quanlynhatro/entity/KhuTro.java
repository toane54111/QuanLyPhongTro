package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "khu_tro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long khuTroId;

    @Column(nullable = false, length = 100)
    private String tenKhu;

    @Column(nullable = false)
    private String diaChi;

    private Integer soTang;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    // Relationships
    @OneToMany(mappedBy = "khuTro", cascade = CascadeType.ALL)
    private List<PhongTro> phongTros;
}
