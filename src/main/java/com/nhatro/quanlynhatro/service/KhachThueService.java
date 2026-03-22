package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiTaiKhoan;
import com.nhatro.quanlynhatro.enums.VaiTro;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KhachThueService {

    private final NguoiDungRepository nguoiDungRepository;
    private final HopDongRepository hopDongRepository;
    private final PasswordEncoder passwordEncoder;

    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findByVaiTro(VaiTro.KHACH_THUE);
    }

    public NguoiDung findById(Long id) {
        return nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê với ID: " + id));
    }

    @Transactional
    public NguoiDung create(NguoiDung khachThue) {
        if (nguoiDungRepository.existsByEmail(khachThue.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + khachThue.getEmail());
        }
        if (khachThue.getCccd() != null && !khachThue.getCccd().isEmpty()
                && nguoiDungRepository.existsByCccd(khachThue.getCccd())) {
            throw new RuntimeException("CCCD đã tồn tại: " + khachThue.getCccd());
        }
        khachThue.setVaiTro(VaiTro.KHACH_THUE);
        khachThue.setMatKhau(passwordEncoder.encode("123456"));
        khachThue.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
        khachThue.setNgayTao(LocalDateTime.now());
        return nguoiDungRepository.save(khachThue);
    }

    @Transactional
    public NguoiDung update(NguoiDung khachThue) {
        NguoiDung existing = nguoiDungRepository.findById(khachThue.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));
        existing.setHoTen(khachThue.getHoTen());
        existing.setSdt(khachThue.getSdt());
        if (khachThue.getCccd() != null && !khachThue.getCccd().isEmpty()) {
            existing.setCccd(khachThue.getCccd());
        }
        return nguoiDungRepository.save(existing);
    }

    @Transactional
    public void toggleStatus(Long id) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));
        if (user.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG) {
            user.setTrangThai(TrangThaiTaiKhoan.BI_KHOA);
        } else {
            user.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
        }
        nguoiDungRepository.save(user);
    }

    public List<NguoiDung> findTenantsWithoutActiveContract() {
        List<NguoiDung> allTenants = nguoiDungRepository.findByVaiTro(VaiTro.KHACH_THUE);
        return allTenants.stream()
                .filter(t -> !hopDongRepository.existsByKhachThue_UserIdAndTrangThai(
                        t.getUserId(), TrangThaiHopDong.DANG_HIEU_LUC))
                .toList();
    }
}
