package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "giao_dich")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giaoDichId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", nullable = true)
    private HoaDon hoaDon;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhuongThucThanhToan phuongThuc;

    private LocalDateTime ngayGiaoDich = LocalDateTime.now();

    @Column(length = 255)
    private String ghiChu;
}
