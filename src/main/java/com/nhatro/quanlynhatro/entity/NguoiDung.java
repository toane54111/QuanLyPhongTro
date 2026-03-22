package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiTaiKhoan;
import com.nhatro.quanlynhatro.enums.VaiTro;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String hoTen;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String matKhau;

    @Column(length = 15)
    private String sdt;

    @Column(unique = true, length = 12)
    private String cccd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiTaiKhoan trangThai = TrangThaiTaiKhoan.HOAT_DONG;

    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(columnDefinition = "INT DEFAULT 0")
    private int soLanSaiMatKhau = 0;

    // Relationships
    @OneToMany(mappedBy = "khachThue")
    private List<HopDong> hopDongs;

    @OneToMany(mappedBy = "khachThue")
    private List<YeuCauSuCo> yeuCauSuCos;

    @OneToMany(mappedBy = "khachThue")
    private List<YeuCauGiaHan> yeuCauGiaHans;

    @OneToMany(mappedBy = "khachThue")
    private List<YeuCauChamDut> yeuCauChamDuts;
}
