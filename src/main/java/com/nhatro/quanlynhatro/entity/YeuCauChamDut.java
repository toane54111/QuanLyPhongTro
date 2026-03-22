package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "yeu_cau_cham_dut")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class YeuCauChamDut {

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
    private LocalDate ngayDuKienTra;

    @Column(columnDefinition = "TEXT")
    private String lyDo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.CHO_PHE_DUYET;

    private LocalDateTime ngayTao = LocalDateTime.now();

    private String lyDoTuChoi;
}
