package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<NguoiDung> findByEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }

    public Optional<NguoiDung> findById(Long id) {
        return nguoiDungRepository.findById(id);
    }

    public NguoiDung save(NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }

    public void updateProfile(NguoiDung nguoiDung, String hoTen, String sdt) {
        nguoiDung.setHoTen(hoTen);
        nguoiDung.setSdt(sdt);
        nguoiDungRepository.save(nguoiDung);
    }

    public boolean changePassword(NguoiDung nguoiDung, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, nguoiDung.getMatKhau())) {
            return false;
        }
        nguoiDung.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(nguoiDung);
        return true;
    }
}
