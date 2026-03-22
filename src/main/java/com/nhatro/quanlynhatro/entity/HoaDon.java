package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hoa_don", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hop_dong_id", "kyThanhToan"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hoaDonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    private HopDong hopDong;

    @Column(nullable = false, length = 7)
    private String kyThanhToan; // VD: 2026-03

    @Column(precision = 12, scale = 0)
    private BigDecimal tienPhong;

    @Column(precision = 12, scale = 0)
    private BigDecimal tienDien;

    @Column(precision = 12, scale = 0)
    private BigDecimal tienNuoc;

    @Column(precision = 12, scale = 0)
    private BigDecimal phiDichVu;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal tongTien;

    private LocalDate hanThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiHoaDon trangThai = TrangThaiHoaDon.CHUA_THANH_TOAN;

    private LocalDateTime ngayTao = LocalDateTime.now();

    // Relationships
    @OneToMany(mappedBy = "hoaDon")
    private List<GiaoDich> giaoDichs;

    @Transient
    public BigDecimal getDaThanhToan() {
        if (giaoDichs == null) return BigDecimal.ZERO;
        return giaoDichs.stream()
                .map(GiaoDich::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getConNo() {
        return tongTien.subtract(getDaThanhToan());
    }
}
