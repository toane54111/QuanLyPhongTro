package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "phu_luc_hop_dong")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhuLucHopDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phuLucId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    private HopDong hopDong;

    // Giá thuê mới (thay đổi so với hợp đồng gốc)
    @Column(name = "gia_thue_mmoi", nullable = false, precision = 12, scale = 0)
    private BigDecimal giaThueMMoi;

    // Ngày kết thúc mới
    @Column(nullable = false)
    private LocalDate ngayKetThucMoi;

    // Ngày phụ lục có hiệu lực (set khi khách duyệt, dùng để tính giá hóa đơn)
    private LocalDate ngayHieuLuc;

    // Trạng thái phê duyệt
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.CHO_PHE_DUYET;

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    private LocalDateTime ngayTao = LocalDateTime.now();
}
