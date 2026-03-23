package com.nhatro.quanlynhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long thongBaoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_nhan_id", nullable = false)
    private NguoiDung nguoiNhan;

    @Column(nullable = false, length = 200)
    private String tieuDe;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @Column(length = 200)
    private String link;

    private boolean daDoc = false;

    private LocalDateTime ngayTao = LocalDateTime.now();
}
