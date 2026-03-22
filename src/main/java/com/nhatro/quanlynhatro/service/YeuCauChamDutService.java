package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.entity.YeuCauChamDut;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import com.nhatro.quanlynhatro.repository.YeuCauChamDutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YeuCauChamDutService {

    private final YeuCauChamDutRepository yeuCauChamDutRepository;
    private final HopDongRepository hopDongRepository;
    private final PhongTroRepository phongTroRepository;

    public List<YeuCauChamDut> findAll() {
        return yeuCauChamDutRepository.findAll();
    }

    public Optional<YeuCauChamDut> findById(Long id) {
        return yeuCauChamDutRepository.findById(id);
    }

    public List<YeuCauChamDut> findByTrangThai(TrangThaiYeuCau trangThai) {
        return yeuCauChamDutRepository.findByTrangThai(trangThai);
    }

    public YeuCauChamDut save(YeuCauChamDut yeuCauChamDut) {
        if (yeuCauChamDut.getNgayTao() == null) {
            yeuCauChamDut.setNgayTao(LocalDateTime.now());
        }
        return yeuCauChamDutRepository.save(yeuCauChamDut);
    }

    @Transactional
    public YeuCauChamDut create(YeuCauChamDut yeuCau) {
        Long khachThueId = yeuCau.getKhachThue().getUserId();

        // Validate tenant has active contract
        if (!hopDongRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiHopDong.DANG_HIEU_LUC)) {
            throw new RuntimeException("Khach thue khong co hop dong dang hieu luc");
        }

        // Validate no pending termination request
        if (yeuCauChamDutRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiYeuCau.CHO_PHE_DUYET)) {
            throw new RuntimeException("Khach thue da co yeu cau cham dut dang cho phe duyet");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.CHO_PHE_DUYET);
        yeuCau.setNgayTao(LocalDateTime.now());
        return yeuCauChamDutRepository.save(yeuCau);
    }

    @Transactional
    public YeuCauChamDut approve(Long yeuCauId) {
        YeuCauChamDut yeuCau = yeuCauChamDutRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau cham dut voi ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yeu cau nay da duoc xu ly truoc do");
        }

        // Approve the request
        yeuCau.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        yeuCauChamDutRepository.save(yeuCau);

        // Terminate the contract
        HopDong hopDong = yeuCau.getHopDong();
        hopDong.setTrangThai(TrangThaiHopDong.DA_CHAM_DUT);
        hopDongRepository.save(hopDong);

        // Set room to TRONG
        PhongTro phongTro = hopDong.getPhongTro();
        phongTro.setTrangThai(TrangThaiPhong.TRONG);
        phongTroRepository.save(phongTro);

        return yeuCau;
    }

    @Transactional
    public YeuCauChamDut reject(Long yeuCauId, String lyDo) {
        YeuCauChamDut yeuCau = yeuCauChamDutRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau cham dut voi ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yeu cau nay da duoc xu ly truoc do");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.DA_TU_CHOI);
        yeuCau.setLyDoTuChoi(lyDo);
        return yeuCauChamDutRepository.save(yeuCau);
    }
}
