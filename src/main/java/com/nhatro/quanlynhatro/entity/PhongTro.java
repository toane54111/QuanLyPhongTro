package com.nhatro.quanlynhatro.entity;

import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "phong_tro", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"khu_tro_id", "so_phong"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhongTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phongId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khu_tro_id", nullable = false)
    private KhuTro khuTro;

    @Column(nullable = false, length = 10)
    private String soPhong;

    private Integer tang;

    private Float dienTich;

    @Column(precision = 12, scale = 0)
    private BigDecimal giaThue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiPhong trangThai = TrangThaiPhong.TRONG;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    // Relationships
    @OneToMany(mappedBy = "phongTro")
    private List<HopDong> hopDongs;

    @OneToMany(mappedBy = "phongTro")
    private List<ChiSoDienNuoc> chiSoDienNuocs;

    @OneToMany(mappedBy = "phongTro")
    private List<YeuCauSuCo> yeuCauSuCos;

    @OneToMany(mappedBy = "phongTro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiSanPhongTro> taiSanPhongTros;

    @OneToMany(mappedBy = "phongTro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThanhVienPhongTro> thanhViens;

    @Transient
    public HopDong getHopDongHienTai() {
        if (hopDongs == null) return null;
        return hopDongs.stream()
                .filter(hd -> hd.getTrangThai().name().equals("DANG_HIEU_LUC"))
                .findFirst()
                .orElse(null);
    }
}


