package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.KhuTro;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.repository.KhuTroRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhongTroService {

    private final PhongTroRepository phongTroRepository;
    private final KhuTroRepository khuTroRepository;

    public List<PhongTro> findAll() {
        return phongTroRepository.findAll();
    }

    public List<PhongTro> findAll(Long khuTroId, TrangThaiPhong trangThai) {
        if (khuTroId != null && trangThai != null) {
            return phongTroRepository.findByKhuTro_KhuTroIdAndTrangThai(khuTroId, trangThai);
        } else if (khuTroId != null) {
            return phongTroRepository.findByKhuTro_KhuTroId(khuTroId);
        } else if (trangThai != null) {
            return phongTroRepository.findByTrangThai(trangThai);
        }
        return phongTroRepository.findAll();
    }

    public Optional<PhongTro> findOptionalById(Long id) {
        return phongTroRepository.findById(id);
    }

    public PhongTro findById(Long id) {
        return phongTroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng trọ với ID: " + id));
    }

    public List<PhongTro> findByKhuTroId(Long khuTroId) {
        return phongTroRepository.findByKhuTro_KhuTroId(khuTroId);
    }

    public List<PhongTro> findByTrangThai(TrangThaiPhong trangThai) {
        return phongTroRepository.findByTrangThai(trangThai);
    }

    public List<PhongTro> findEmptyRooms() {
        return phongTroRepository.findByTrangThai(TrangThaiPhong.TRONG);
    }

    public List<PhongTro> findOccupiedRooms() {
        return phongTroRepository.findByTrangThai(TrangThaiPhong.DA_THUE);
    }

    @Transactional
    public PhongTro save(PhongTro phongTro, Long khuTroId) {
        KhuTro khuTro = khuTroRepository.findById(khuTroId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu trọ"));
        phongTro.setKhuTro(khuTro);
        if (phongTro.getTrangThai() == null) {
            phongTro.setTrangThai(TrangThaiPhong.TRONG);
        }
        if (phongTroRepository.existsByKhuTro_KhuTroIdAndSoPhong(khuTroId, phongTro.getSoPhong())) {
            throw new RuntimeException("Số phòng " + phongTro.getSoPhong() + " đã tồn tại trong khu trọ này");
        }
        return phongTroRepository.save(phongTro);
    }

    @Transactional
    public PhongTro update(PhongTro phongTro, Long khuTroId) {
        PhongTro existing = findById(phongTro.getPhongId());
        KhuTro khuTro = khuTroRepository.findById(khuTroId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu trọ"));
        existing.setKhuTro(khuTro);
        existing.setSoPhong(phongTro.getSoPhong());
        existing.setTang(phongTro.getTang());
        existing.setDienTich(phongTro.getDienTich());
        existing.setGiaThue(phongTro.getGiaThue());
        if (phongTro.getTrangThai() != null) {
            existing.setTrangThai(phongTro.getTrangThai());
        }
        existing.setMoTa(phongTro.getMoTa());
        return phongTroRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!phongTroRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy phòng trọ với ID: " + id);
        }
        phongTroRepository.deleteById(id);
    }

    public boolean existsBySoPhong(Long khuTroId, String soPhong) {
        return phongTroRepository.existsByKhuTro_KhuTroIdAndSoPhong(khuTroId, soPhong);
    }

    public Map<TrangThaiPhong, Long> countByTrangThai() {
        Map<TrangThaiPhong, Long> result = new HashMap<>();
        for (TrangThaiPhong tt : TrangThaiPhong.values()) {
            result.put(tt, (long) phongTroRepository.findByTrangThai(tt).size());
        }
        return result;
    }
}
