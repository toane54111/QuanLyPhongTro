package com.nhatro.quanlynhatro.config;

import com.nhatro.quanlynhatro.entity.*;
import com.nhatro.quanlynhatro.enums.*;
import com.nhatro.quanlynhatro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            NguoiDungRepository nguoiDungRepo,
            KhuTroRepository khuTroRepo,
            PhongTroRepository phongTroRepo,
            DichVuRepository dichVuRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Chỉ seed nếu chưa có dữ liệu
            if (nguoiDungRepo.count() > 0) return;

            // === TẠO TÀI KHOẢN ===
            NguoiDung chuTro = NguoiDung.builder()
                    .hoTen("Nguyễn Văn An")
                    .email("chutro@gmail.com")
                    .matKhau(passwordEncoder.encode("123456"))
                    .sdt("0901234567")
                    .cccd("001099012345")
                    .vaiTro(VaiTro.CHU_TRO)
                    .trangThai(TrangThaiTaiKhoan.HOAT_DONG)
                    .build();
            nguoiDungRepo.save(chuTro);

            NguoiDung khach1 = NguoiDung.builder()
                    .hoTen("Trần Thị Bình")
                    .email("khach1@gmail.com")
                    .matKhau(passwordEncoder.encode("123456"))
                    .sdt("0912345678")
                    .cccd("001099054321")
                    .vaiTro(VaiTro.KHACH_THUE)
                    .trangThai(TrangThaiTaiKhoan.HOAT_DONG)
                    .build();
            nguoiDungRepo.save(khach1);

            NguoiDung khach2 = NguoiDung.builder()
                    .hoTen("Lê Văn Cường")
                    .email("khach2@gmail.com")
                    .matKhau(passwordEncoder.encode("123456"))
                    .sdt("0923456789")
                    .cccd("001099067890")
                    .vaiTro(VaiTro.KHACH_THUE)
                    .trangThai(TrangThaiTaiKhoan.HOAT_DONG)
                    .build();
            nguoiDungRepo.save(khach2);

            // === TẠO KHU TRỌ ===
            KhuTro khu1 = KhuTro.builder()
                    .tenKhu("Khu trọ Bình An")
                    .diaChi("123 Nguyễn Văn Linh, Q.7, TP.HCM")
                    .soTang(3)
                    .moTa("Khu trọ cao cấp, gần trường đại học")
                    .build();
            khuTroRepo.save(khu1);

            // === TẠO PHÒNG TRỌ ===
            for (int tang = 1; tang <= 3; tang++) {
                for (int phong = 1; phong <= 4; phong++) {
                    PhongTro p = PhongTro.builder()
                            .khuTro(khu1)
                            .soPhong("P" + tang + "0" + phong)
                            .tang(tang)
                            .dienTich(20.0f + phong * 2)
                            .giaThue(BigDecimal.valueOf(2500000 + phong * 500000L))
                            .trangThai(TrangThaiPhong.TRONG)
                            .moTa("Phòng tầng " + tang + ", có máy lạnh, WC riêng")
                            .build();
                    phongTroRepo.save(p);
                }
            }

            // === TẠO DỊCH VỤ ===
            dichVuRepo.save(DichVu.builder().tenDV("Điện").donGia(BigDecimal.valueOf(3500)).donViTinh("kWh").build());
            dichVuRepo.save(DichVu.builder().tenDV("Nước").donGia(BigDecimal.valueOf(15000)).donViTinh("m³").build());
            dichVuRepo.save(DichVu.builder().tenDV("Wifi").donGia(BigDecimal.valueOf(100000)).donViTinh("tháng").build());
            dichVuRepo.save(DichVu.builder().tenDV("Rác").donGia(BigDecimal.valueOf(20000)).donViTinh("tháng").build());
            dichVuRepo.save(DichVu.builder().tenDV("Giữ xe").donGia(BigDecimal.valueOf(100000)).donViTinh("tháng").build());

            System.out.println("=== ĐÃ KHỞI TẠO DỮ LIỆU MẪU ===");
            System.out.println("Chủ trọ: chutro@gmail.com / 123456");
            System.out.println("Khách 1: khach1@gmail.com / 123456");
            System.out.println("Khách 2: khach2@gmail.com / 123456");
            System.out.println("Khu trọ: 1 khu, 12 phòng (3 tầng x 4 phòng)");
            System.out.println("Dịch vụ: Điện, Nước, Wifi, Rác, Giữ xe");
        };
    }
}
