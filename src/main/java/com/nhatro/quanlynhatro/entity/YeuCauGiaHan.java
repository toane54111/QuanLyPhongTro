package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_gia_han")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class YeuCauGiaHan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long yeuCauId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_thue_id", nullable = false)
    private NguoiDung khachThue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hop_dong_id", nullable = false)
    private HopDong hopDong;

    @Column(nullable = false)
    private Integer thoiGianGiaHan; // số tháng muốn gia hạn

    @Column(columnDefinition = "TEXT")
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.CHO_PHE_DUYET;

    private LocalDateTime ngayTao = LocalDateTime.now();

    private String lyDoTuChoi;
}
