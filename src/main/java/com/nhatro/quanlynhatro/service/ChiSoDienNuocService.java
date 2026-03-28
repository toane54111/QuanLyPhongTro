package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.repository.ChiSoDienNuocRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChiSoDienNuocService {

    private final ChiSoDienNuocRepository chiSoDienNuocRepository;
    private final PhongTroRepository phongTroRepository;

    public List<ChiSoDienNuoc> findAll(String kyGhi) {
        if (kyGhi != null && !kyGhi.isEmpty()) {
            return chiSoDienNuocRepository.findByKyGhi(kyGhi);
        }
        return chiSoDienNuocRepository.findAll();
    }

    public ChiSoDienNuoc findById(Long id) {
        return chiSoDienNuocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chỉ số điện nước với ID: " + id));
    }

    public List<ChiSoDienNuoc> findByKyGhi(String kyGhi) {
        return chiSoDienNuocRepository.findByKyGhi(kyGhi);
    }

    public Optional<ChiSoDienNuoc> findByPhongIdAndKyGhi(Long phongId, String kyGhi) {
        return chiSoDienNuocRepository.findByPhongTro_PhongIdAndKyGhi(phongId, kyGhi);
    }

    @Transactional
    public ChiSoDienNuoc save(ChiSoDienNuoc chiSo, Long phongId) {
        PhongTro phongTro = phongTroRepository.findById(phongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng trọ"));
        chiSo.setPhongTro(phongTro);

        if (chiSoDienNuocRepository.existsByPhongTro_PhongIdAndKyGhi(phongId, chiSo.getKyGhi())) {
            throw new RuntimeException("Chỉ số điện nước đã tồn tại cho phòng này trong kỳ " + chiSo.getKyGhi());
        }
        if (chiSo.getDienMoi() < chiSo.getDienCu()) {
            throw new RuntimeException("Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ");
        }
        if (chiSo.getNuocMoi() < chiSo.getNuocCu()) {
            throw new RuntimeException("Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ");
        }
        return chiSoDienNuocRepository.save(chiSo);
    }

    @Transactional
    public ChiSoDienNuoc update(ChiSoDienNuoc chiSo, Long phongId) {
        ChiSoDienNuoc existing = findById(chiSo.getChiSoId());
        PhongTro phongTro = phongTroRepository.findById(phongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng trọ"));

        existing.setPhongTro(phongTro);
        existing.setKyGhi(chiSo.getKyGhi());
        existing.setDienCu(chiSo.getDienCu());
        existing.setDienMoi(chiSo.getDienMoi());
        existing.setNuocCu(chiSo.getNuocCu());
        existing.setNuocMoi(chiSo.getNuocMoi());
        existing.setNgayGhi(chiSo.getNgayGhi());

        if (existing.getDienMoi() < existing.getDienCu()) {
            throw new RuntimeException("Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ");
        }
        if (existing.getNuocMoi() < existing.getNuocCu()) {
            throw new RuntimeException("Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ");
        }
        return chiSoDienNuocRepository.save(existing);
    }

    public Optional<ChiSoDienNuoc> getLatestByPhongId(Long phongId) {
        return chiSoDienNuocRepository.findTopByPhongTro_PhongIdOrderByKyGhiDesc(phongId);
    }

    public boolean existsByPhongIdAndKyGhi(Long phongId, String kyGhi) {
        return chiSoDienNuocRepository.existsByPhongTro_PhongIdAndKyGhi(phongId, kyGhi);
    }
}
