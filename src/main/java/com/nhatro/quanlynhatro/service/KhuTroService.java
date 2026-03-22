package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.KhuTro;
import com.nhatro.quanlynhatro.repository.KhuTroRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KhuTroService {

    private final KhuTroRepository khuTroRepository;
    private final PhongTroRepository phongTroRepository;

    public List<KhuTro> findAll() {
        return khuTroRepository.findAll();
    }

    public Optional<KhuTro> findOptionalById(Long id) {
        return khuTroRepository.findById(id);
    }

    public KhuTro findById(Long id) {
        return khuTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu trọ với ID: " + id));
    }

    @Transactional
    public KhuTro save(KhuTro khuTro) {
        return khuTroRepository.save(khuTro);
    }

    @Transactional
    public KhuTro update(KhuTro khuTro) {
        KhuTro existing = khuTroRepository.findById(khuTro.getKhuTroId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay khu tro voi ID: " + khuTro.getKhuTroId()));

        existing.setTenKhu(khuTro.getTenKhu());
        existing.setDiaChi(khuTro.getDiaChi());
        existing.setSoTang(khuTro.getSoTang());
        existing.setMoTa(khuTro.getMoTa());

        return khuTroRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!khuTroRepository.existsById(id)) {
            throw new RuntimeException("Khong tim thay khu tro voi ID: " + id);
        }
        khuTroRepository.deleteById(id);
    }

    public long countRoomsByKhuTroId(Long khuTroId) {
        return phongTroRepository.findByKhuTro_KhuTroId(khuTroId).size();
    }
}
