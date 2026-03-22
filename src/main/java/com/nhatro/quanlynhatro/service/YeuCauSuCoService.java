package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.YeuCauSuCo;
import com.nhatro.quanlynhatro.enums.TrangThaiSuCo;
import com.nhatro.quanlynhatro.repository.YeuCauSuCoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YeuCauSuCoService {

    private final YeuCauSuCoRepository yeuCauSuCoRepository;

    public List<YeuCauSuCo> findAll() {
        return yeuCauSuCoRepository.findAll();
    }

    public Optional<YeuCauSuCo> findOptionalById(Long id) {
        return yeuCauSuCoRepository.findById(id);
    }

    public YeuCauSuCo findById(Long id) {
        return yeuCauSuCoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu sự cố với ID: " + id));
    }

    public List<YeuCauSuCo> findByKhachThueId(Long khachThueId) {
        return yeuCauSuCoRepository.findByKhachThue_UserId(khachThueId);
    }

    public List<YeuCauSuCo> findByTrangThai(TrangThaiSuCo trangThai) {
        return yeuCauSuCoRepository.findByTrangThai(trangThai);
    }

    public List<YeuCauSuCo> findAllOrderByNgayTaoDesc() {
        return yeuCauSuCoRepository.findAllByOrderByNgayTaoDesc();
    }

    @Transactional
    public YeuCauSuCo create(YeuCauSuCo yeuCauSuCo) {
        yeuCauSuCo.setTrangThai(TrangThaiSuCo.MOI);
        yeuCauSuCo.setNgayTao(LocalDateTime.now());
        return yeuCauSuCoRepository.save(yeuCauSuCo);
    }

    public YeuCauSuCo save(YeuCauSuCo yeuCauSuCo) {
        if (yeuCauSuCo.getNgayTao() == null) {
            yeuCauSuCo.setNgayTao(LocalDateTime.now());
        }
        return yeuCauSuCoRepository.save(yeuCauSuCo);
    }

    @Transactional
    public YeuCauSuCo updateStatus(Long ticketId, TrangThaiSuCo trangThai, String ghiChuXuLy) {
        YeuCauSuCo yeuCau = yeuCauSuCoRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay yeu cau su co voi ID: " + ticketId));

        yeuCau.setTrangThai(trangThai);
        if (ghiChuXuLy != null && !ghiChuXuLy.isEmpty()) {
            yeuCau.setGhiChuXuLy(ghiChuXuLy);
        }

        return yeuCauSuCoRepository.save(yeuCau);
    }
}
