package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiGiaoDich;
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50) default 'DA_XAC_NHAN'")
    @Builder.Default
    private TrangThaiGiaoDich trangThaiGD = TrangThaiGiaoDich.DA_XAC_NHAN;

    @Builder.Default
    private LocalDateTime ngayGiaoDich = LocalDateTime.now();

    @Column(length = 255)
    private String ghiChu;

    @PrePersist
    private void prePersist() {
        if (trangThaiGD == null) {
            trangThaiGD = TrangThaiGiaoDich.DA_XAC_NHAN;
        }
        if (ngayGiaoDich == null) {
            ngayGiaoDich = LocalDateTime.now();
        }
    }
}
