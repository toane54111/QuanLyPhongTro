package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.YeuCauGiaHan;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.YeuCauGiaHanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YeuCauGiaHanService {

    private final YeuCauGiaHanRepository yeuCauGiaHanRepository;
    private final HopDongRepository hopDongRepository;

    public List<YeuCauGiaHan> findAll() {
        return yeuCauGiaHanRepository.findAll();
    }

    public Optional<YeuCauGiaHan> findById(Long id) {
        return yeuCauGiaHanRepository.findById(id);
    }

    public List<YeuCauGiaHan> findByTrangThai(TrangThaiYeuCau trangThai) {
        return yeuCauGiaHanRepository.findByTrangThai(trangThai);
    }

    public YeuCauGiaHan save(YeuCauGiaHan yeuCauGiaHan) {
        if (yeuCauGiaHan.getNgayTao() == null) {
            yeuCauGiaHan.setNgayTao(LocalDateTime.now());
        }
        return yeuCauGiaHanRepository.save(yeuCauGiaHan);
    }

    @Transactional
    public YeuCauGiaHan create(YeuCauGiaHan yeuCau) {
        Long khachThueId = yeuCau.getKhachThue().getUserId();

        // Validate tenant has active contract
        if (!hopDongRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiHopDong.DANG_HIEU_LUC)) {
            throw new RuntimeException("Khach thue khong co hop dong dang hieu luc");
        }

        // Validate no pending extension request
        if (yeuCauGiaHanRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiYeuCau.CHO_PHE_DUYET)) {
            throw new RuntimeException("Khach thue da co yeu cau gia han dang cho phe duyet");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.CHO_PHE_DUYET);
        yeuCau.setNgayTao(LocalDateTime.now());
        return yeuCauGiaHanRepository.save(yeuCau);
    }

    @Transactional
    public YeuCauGiaHan approve(Long yeuCauId) {
        YeuCauGiaHan yeuCau = yeuCauGiaHanRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau gia han voi ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yeu cau nay da duoc xu ly truoc do");
        }

        // Approve the request
        yeuCau.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        yeuCauGiaHanRepository.save(yeuCau);

        // Extend the contract
        HopDong hopDong = yeuCau.getHopDong();
        hopDong.setNgayKetThuc(hopDong.getNgayKetThuc().plusMonths(yeuCau.getThoiGianGiaHan()));
        hopDong.setTrangThai(TrangThaiHopDong.DANG_HIEU_LUC);
        hopDongRepository.save(hopDong);

        return yeuCau;
    }

    @Transactional
    public YeuCauGiaHan reject(Long yeuCauId, String lyDo) {
        YeuCauGiaHan yeuCau = yeuCauGiaHanRepository.findById(yeuCauId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau gia han voi ID: " + yeuCauId));

        if (yeuCau.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Yeu cau nay da duoc xu ly truoc do");
        }

        yeuCau.setTrangThai(TrangThaiYeuCau.DA_TU_CHOI);
        yeuCau.setLyDoTuChoi(lyDo);
        return yeuCauGiaHanRepository.save(yeuCau);
    }
}
