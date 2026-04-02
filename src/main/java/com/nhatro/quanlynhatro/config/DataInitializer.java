package com.nhatro.quanlynhatro.config;

import com.nhatro.quanlynhatro.entity.*;
import com.nhatro.quanlynhatro.enums.*;
import com.nhatro.quanlynhatro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            NguoiDungRepository nguoiDungRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // === TẠO / CẬP NHẬT TÀI KHOẢN MẪU ===
            // Tài khoản được tạo hoặc cập nhật mật khẩu BCrypt đúng khi khởi động
            upsertUser(nguoiDungRepo, passwordEncoder,
                    "chutro@gmail.com", "123456", "Nguyễn Văn Chủ",
                    "0901234567", "012345678901",
                    VaiTro.CHU_TRO);

            upsertUser(nguoiDungRepo, passwordEncoder,
                    "t@gmail.com", "123456", "Trần Thị Khách",
                    "0912345678", "012345678902",
                    VaiTro.KHACH_THUE);

            System.out.println("=== ĐÃ KHỞI TẠO / CẬP NHẬT TÀI KHOẢN MẪU ===");
            System.out.println("Chủ trọ: chutro@gmail.com / 123456");
            System.out.println("Khách  : t@gmail.com / 123456");
        };
    }

    /**
     * Tạo user mới nếu chưa tồn tại, hoặc cập nhật mật khẩu BCrypt nếu đã tồn tại.
     * Đảm bảo mật khẩu luôn được mã hóa đúng bởi PasswordEncoder.
     */
    private void upsertUser(NguoiDungRepository repo, PasswordEncoder encoder,
                            String email, String rawPassword, String hoTen,
                            String sdt, String cccd, VaiTro vaiTro) {
        Optional<NguoiDung> existing = repo.findByEmail(email);
        if (existing.isPresent()) {
            // Cập nhật mật khẩu BCrypt đúng cho user đã tồn tại
            NguoiDung user = existing.get();
            user.setMatKhau(encoder.encode(rawPassword));
            repo.save(user);
        } else {
            // Tạo user mới
            NguoiDung user = NguoiDung.builder()
                    .hoTen(hoTen)
                    .email(email)
                    .matKhau(encoder.encode(rawPassword))
                    .sdt(sdt)
                    .cccd(cccd)
                    .vaiTro(vaiTro)
                    .trangThai(TrangThaiTaiKhoan.HOAT_DONG)
                    .build();
            repo.save(user);
        }
    }
}
