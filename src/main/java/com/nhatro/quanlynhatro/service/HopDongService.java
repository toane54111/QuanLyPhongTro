package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.repository.GiaoDichRepository;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.repository.PhongTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HopDongService {

    private final HopDongRepository hopDongRepository;
    private final PhongTroRepository phongTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final GiaoDichRepository giaoDichRepository;

    public List<HopDong> findAll(TrangThaiHopDong trangThai) {
        if (trangThai != null) {
            return hopDongRepository.findByTrangThai(trangThai);
        }
        return hopDongRepository.findAll();
    }

    public Optional<HopDong> findById(Long id) {
        return hopDongRepository.findById(id);
    }

    public HopDong getById(Long id) {
        return hopDongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + id));
    }

    public List<HopDong> findByKhachThueId(Long khachThueId) {
        return hopDongRepository.findByKhachThue_UserId(khachThueId);
    }

    public List<HopDong> findByKhachThueIdAndTrangThai(Long khachThueId, TrangThaiHopDong trangThai) {
        return hopDongRepository.findByKhachThue_UserIdAndTrangThai(khachThueId, trangThai);
    }

    public Optional<HopDong> findActiveByKhachThueId(Long khachThueId) {
        List<HopDong> activeContracts = hopDongRepository.findByKhachThue_UserIdAndTrangThai(
                khachThueId, TrangThaiHopDong.DANG_HIEU_LUC);
        return activeContracts.isEmpty() ? Optional.empty() : Optional.of(activeContracts.get(0));
    }

    public List<HopDong> findActiveContracts() {
        return hopDongRepository.findByTrangThai(TrangThaiHopDong.DANG_HIEU_LUC);
    }

    @Transactional
    public HopDong create(HopDong hopDong, Long phongId, Long khachThueId) {
        PhongTro phongTro = phongTroRepository.findById(phongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng trọ"));

        if (phongTro.getTrangThai() != TrangThaiPhong.TRONG) {
            throw new RuntimeException("Phòng đã được thuê hoặc đang bảo trì");
        }

        NguoiDung khachThue = nguoiDungRepository.findById(khachThueId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));

        if (hopDongRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiHopDong.DANG_HIEU_LUC)) {
            throw new RuntimeException("Khách thuê đã có hợp đồng đang hiệu lực");
        }

        // Luôn lấy giá thuê từ PhongTro (không cho chủ tự nhập)
        hopDong.setGiaThue(phongTro.getGiaThue());

        // Validate thời hạn hợp đồng tối thiểu 6 tháng
        if (hopDong.getNgayBatDau() != null && hopDong.getNgayKetThuc() != null) {
            LocalDate minEndDate = hopDong.getNgayBatDau().plusMonths(6);
            if (hopDong.getNgayKetThuc().isBefore(minEndDate)) {
                throw new RuntimeException("Thời hạn hợp đồng phải ít nhất 6 tháng. Ngày kết thúc tối thiểu: " + minEndDate);
            }
        }

        hopDong.setPhongTro(phongTro);
        hopDong.setKhachThue(khachThue);
        hopDong.setTrangThai(TrangThaiHopDong.DANG_HIEU_LUC);

        // Set room to rented
        phongTro.setTrangThai(TrangThaiPhong.DA_THUE);
        phongTroRepository.save(phongTro);

        HopDong saved = hopDongRepository.save(hopDong);

        // Ghi nhận tiền cọc (theo Activity Diagram UC07)
        BigDecimal tienCoc = hopDong.getTienCoc();
        if (tienCoc != null && tienCoc.compareTo(BigDecimal.ZERO) > 0) {
            GiaoDich giaoDichCoc = GiaoDich.builder()
                    .hoaDon(null)
                    .soTien(tienCoc)
                    .phuongThuc(PhuongThucThanhToan.TIEN_MAT)
                    .ngayGiaoDich(LocalDateTime.now())
                    .ghiChu("Tiền cọc hợp đồng phòng " + phongTro.getSoPhong()
                            + " - Khách: " + khachThue.getHoTen())
                    .build();
            giaoDichRepository.save(giaoDichCoc);
        }

        return saved;
    }

    @Transactional
    public HopDong extend(Long hopDongId, java.time.LocalDate ngayKetThucMoi) {
        HopDong hopDong = getById(hopDongId);

        // Validate theo Sequence Diagram: ngayKTMoi > ngayKTCu && ngayKTMoi > ngayHienTai
        if (ngayKetThucMoi.isBefore(hopDong.getNgayKetThuc()) || ngayKetThucMoi.isEqual(hopDong.getNgayKetThuc())) {
            throw new RuntimeException("Ngày kết thúc mới phải sau ngày kết thúc hiện tại ("
                    + hopDong.getNgayKetThuc() + ")");
        }
        if (ngayKetThucMoi.isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Ngày kết thúc mới phải sau ngày hiện tại");
        }

        hopDong.setNgayKetThuc(ngayKetThucMoi);
        hopDong.setTrangThai(TrangThaiHopDong.DANG_HIEU_LUC);
        return hopDongRepository.save(hopDong);
    }

    @Transactional
    public void terminate(Long hopDongId) {
        HopDong hopDong = getById(hopDongId);
        hopDong.setTrangThai(TrangThaiHopDong.DA_CHAM_DUT);
        hopDongRepository.save(hopDong);

        PhongTro phongTro = hopDong.getPhongTro();
        phongTro.setTrangThai(TrangThaiPhong.TRONG);
        phongTroRepository.save(phongTro);
    }

    public boolean existsActiveContractForRoom(Long phongId) {
        return hopDongRepository.existsByPhongTro_PhongIdAndTrangThai(phongId, TrangThaiHopDong.DANG_HIEU_LUC);
    }

    public boolean existsActiveContractForTenant(Long khachThueId) {
        return hopDongRepository.existsByKhachThue_UserIdAndTrangThai(khachThueId, TrangThaiHopDong.DANG_HIEU_LUC);
    }
}
