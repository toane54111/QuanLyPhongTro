package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.DichVu;
import com.nhatro.quanlynhatro.repository.DichVuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DichVuService {

    private final DichVuRepository dichVuRepository;

    public List<DichVu> findAll() {
        return dichVuRepository.findAll();
    }

    public Optional<DichVu> findOptionalById(Long id) {
        return dichVuRepository.findById(id);
    }

    public DichVu findById(Long id) {
        return dichVuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
    }

    public Optional<DichVu> findByTenDV(String tenDV) {
        return dichVuRepository.findByTenDV(tenDV);
    }

    public boolean existsByTenDV(String tenDV) {
        return dichVuRepository.existsByTenDV(tenDV);
    }

    @Transactional
    public DichVu save(DichVu dichVu) {
        if (dichVuRepository.existsByTenDV(dichVu.getTenDV())) {
            throw new RuntimeException("Dich vu da ton tai: " + dichVu.getTenDV());
        }
        return dichVuRepository.save(dichVu);
    }

    @Transactional
    public DichVu update(DichVu dichVu) {
        DichVu existing = dichVuRepository.findById(dichVu.getDichVuId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay dich vu voi ID: " + dichVu.getDichVuId()));

        existing.setTenDV(dichVu.getTenDV());
        existing.setDonGia(dichVu.getDonGia());
        existing.setDonViTinh(dichVu.getDonViTinh());

        return dichVuRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!dichVuRepository.existsById(id)) {
            throw new RuntimeException("Khong tim thay dich vu voi ID: " + id);
        }
        dichVuRepository.deleteById(id);
    }
}
