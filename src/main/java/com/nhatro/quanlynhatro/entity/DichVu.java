package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dich_vu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dichVuId;

    @Column(nullable = false, unique = true, length = 50)
    private String tenDV;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(length = 20)
    private String donViTinh;
}
