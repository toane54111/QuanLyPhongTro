package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.DichVu;
import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.repository.ChiSoDienNuocRepository;
import com.nhatro.quanlynhatro.repository.DichVuRepository;
import com.nhatro.quanlynhatro.repository.GiaoDichRepository;
import com.nhatro.quanlynhatro.entity.PhuLucHopDong;
import com.nhatro.quanlynhatro.repository.HoaDonRepository;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.PhuLucHopDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HopDongRepository hopDongRepository;
    private final ChiSoDienNuocRepository chiSoDienNuocRepository;
    private final DichVuRepository dichVuRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final PhuLucHopDongRepository phuLucHopDongRepository;

    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }

    public List<HoaDon> findAll(TrangThaiHoaDon trangThai, String kyThanhToan) {
        List<HoaDon> result = hoaDonRepository.findAll();
        if (trangThai != null) {
            result = result.stream().filter(hd -> hd.getTrangThai() == trangThai).toList();
        }
        if (kyThanhToan != null && !kyThanhToan.isEmpty()) {
            result = result.stream().filter(hd -> kyThanhToan.equals(hd.getKyThanhToan())).toList();
        }
        return result;
    }

    // Returns HoaDon directly (throws if not found) - for landlord controllers
    public HoaDon findById(Long id) {
        return hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));
    }

    // Returns Optional - for tenant controllers that call .orElseThrow() themselves
    public Optional<HoaDon> findOptionalById(Long id) {
        return hoaDonRepository.findById(id);
    }

    public List<HoaDon> findByHopDongId(Long hopDongId) {
        return hoaDonRepository.findByHopDong_HopDongId(hopDongId);
    }

    public List<HoaDon> findByKhachThueId(Long khachThueId) {
        return hoaDonRepository.findByHopDong_KhachThue_UserId(khachThueId);
    }

    public List<HoaDon> findByTrangThai(TrangThaiHoaDon trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    public List<HoaDon> findUnpaidByKhachThueId(Long khachThueId) {
        List<HoaDon> allInvoices = hoaDonRepository.findByHopDong_KhachThue_UserId(khachThueId);
        return allInvoices.stream()
                .filter(hd -> hd.getTrangThai() != TrangThaiHoaDon.DA_THANH_TOAN)
                .toList();
    }

    public List<GiaoDich> findGiaoDichByHoaDonId(Long hoaDonId) {
        return giaoDichRepository.findByHoaDon_HoaDonId(hoaDonId);
    }

    @Transactional
    public HoaDon create(HoaDon hoaDon, Long hopDongId, String kyThanhToan) {
        if (hoaDonRepository.existsByHopDong_HopDongIdAndKyThanhToan(hopDongId, kyThanhToan)) {
            throw new RuntimeException("Hóa đơn đã tồn tại cho hợp đồng này trong kỳ " + kyThanhToan);
        }

        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + hopDongId));

        Long phongId = hopDong.getPhongTro().getPhongId();

        // Tính giá thuê hiệu lực: kiểm tra phụ lục hợp đồng đã duyệt
        BigDecimal tienPhong = getGiaThueHieuLuc(hopDong, kyThanhToan);

        BigDecimal tienDien = BigDecimal.ZERO;
        BigDecimal tienNuoc = BigDecimal.ZERO;

        Optional<ChiSoDienNuoc> chiSoOpt = chiSoDienNuocRepository.findByPhongTro_PhongIdAndKyGhi(phongId, kyThanhToan);
        if (chiSoOpt.isPresent()) {
            ChiSoDienNuoc chiSo = chiSoOpt.get();
            Optional<DichVu> dienDV = dichVuRepository.findByTenDV("Điện");
            if (dienDV.isPresent()) {
                tienDien = dienDV.get().getDonGia().multiply(BigDecimal.valueOf(chiSo.getDienTieuThu()));
            }
            Optional<DichVu> nuocDV = dichVuRepository.findByTenDV("Nước");
            if (nuocDV.isPresent()) {
                tienNuoc = nuocDV.get().getDonGia().multiply(BigDecimal.valueOf(chiSo.getNuocTieuThu()));
            }
        }

        BigDecimal phiDichVu = BigDecimal.ZERO;
        List<DichVu> allServices = dichVuRepository.findAll();
        for (DichVu dv : allServices) {
            if (!"Điện".equals(dv.getTenDV()) && !"Nước".equals(dv.getTenDV())) {
                phiDichVu = phiDichVu.add(dv.getDonGia());
            }
        }

        BigDecimal tongTien = tienPhong.add(tienDien).add(tienNuoc).add(phiDichVu);

        HoaDon newHoaDon = new HoaDon();
        newHoaDon.setHopDong(hopDong);
        newHoaDon.setKyThanhToan(kyThanhToan);
        newHoaDon.setTienPhong(tienPhong);
        newHoaDon.setTienDien(tienDien);
        newHoaDon.setTienNuoc(tienNuoc);
        newHoaDon.setPhiDichVu(phiDichVu);
        newHoaDon.setTongTien(tongTien);
        newHoaDon.setHanThanhToan(hoaDon.getHanThanhToan() != null ? hoaDon.getHanThanhToan() : LocalDate.now().plusDays(15));
        newHoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);
        newHoaDon.setNgayTao(LocalDateTime.now());

        return hoaDonRepository.save(newHoaDon);
    }

    @Transactional
    public void recordPayment(Long hoaDonId, GiaoDich giaoDich) {
        HoaDon hoaDon = findById(hoaDonId);

        if (hoaDon.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán đầy đủ");
        }

        giaoDich.setHoaDon(hoaDon);
        if (giaoDich.getNgayGiaoDich() == null) {
            giaoDich.setNgayGiaoDich(LocalDateTime.now());
        }
        giaoDichRepository.save(giaoDich);

        BigDecimal totalPaid = giaoDichRepository.sumSoTienByHoaDonId(hoaDonId);
        if (totalPaid.compareTo(hoaDon.getTongTien()) >= 0) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        } else {
            hoaDon.setTrangThai(TrangThaiHoaDon.THANH_TOAN_MOT_PHAN);
        }
        hoaDonRepository.save(hoaDon);
    }

    @Transactional
    public void thanhToan(HoaDon hoaDon, PhuongThucThanhToan phuongThuc) {
        BigDecimal conNo = hoaDon.getConNo();
        if (conNo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Hóa đơn đã được thanh toán đầy đủ");
        }

        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setHoaDon(hoaDon);
        giaoDich.setSoTien(conNo);
        giaoDich.setPhuongThuc(phuongThuc);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setGhiChu("Thanh toán qua " + phuongThuc.name());
        giaoDichRepository.save(giaoDich);

        hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonRepository.save(hoaDon);
    }

    public boolean existsByHopDongIdAndKyThanhToan(Long hopDongId, String kyThanhToan) {
        return hoaDonRepository.existsByHopDong_HopDongIdAndKyThanhToan(hopDongId, kyThanhToan);
    }

    /**
     * Xem trước (preview) hóa đơn hàng loạt cho tất cả hợp đồng đang hiệu lực
     * Trả về danh sách Map chứa thông tin dự kiến cho từng phòng
     */
    public List<Map<String, Object>> previewBatchInvoices(String kyThanhToan) {
        List<HopDong> activeContracts = hopDongRepository.findByTrangThai(
                com.nhatro.quanlynhatro.enums.TrangThaiHopDong.DANG_HIEU_LUC);
        List<Map<String, Object>> previews = new ArrayList<>();

        for (HopDong hopDong : activeContracts) {
            // Bỏ qua nếu đã có hóa đơn cho kỳ này
            if (hoaDonRepository.existsByHopDong_HopDongIdAndKyThanhToan(hopDong.getHopDongId(), kyThanhToan)) {
                continue;
            }

            Long phongId = hopDong.getPhongTro().getPhongId();
            BigDecimal tienPhong = getGiaThueHieuLuc(hopDong, kyThanhToan);
            BigDecimal tienDien = BigDecimal.ZERO;
            BigDecimal tienNuoc = BigDecimal.ZERO;
            boolean coChiSo = false;

            Optional<ChiSoDienNuoc> chiSoOpt = chiSoDienNuocRepository.findByPhongTro_PhongIdAndKyGhi(phongId, kyThanhToan);
            if (chiSoOpt.isPresent()) {
                coChiSo = true;
                ChiSoDienNuoc chiSo = chiSoOpt.get();
                Optional<DichVu> dienDV = dichVuRepository.findByTenDV("Điện");
                if (dienDV.isPresent()) {
                    tienDien = dienDV.get().getDonGia().multiply(BigDecimal.valueOf(chiSo.getDienTieuThu()));
                }
                Optional<DichVu> nuocDV = dichVuRepository.findByTenDV("Nước");
                if (nuocDV.isPresent()) {
                    tienNuoc = nuocDV.get().getDonGia().multiply(BigDecimal.valueOf(chiSo.getNuocTieuThu()));
                }
            }

            BigDecimal phiDichVu = BigDecimal.ZERO;
            List<DichVu> allServices = dichVuRepository.findAll();
            for (DichVu dv : allServices) {
                if (!"Điện".equals(dv.getTenDV()) && !"Nước".equals(dv.getTenDV())) {
                    phiDichVu = phiDichVu.add(dv.getDonGia());
                }
            }

            BigDecimal tongTien = tienPhong.add(tienDien).add(tienNuoc).add(phiDichVu);

            Map<String, Object> preview = new HashMap<>();
            preview.put("hopDongId", hopDong.getHopDongId());
            preview.put("soPhong", hopDong.getPhongTro().getSoPhong());
            preview.put("khachThue", hopDong.getKhachThue().getHoTen());
            preview.put("tienPhong", tienPhong);
            preview.put("tienDien", tienDien);
            preview.put("tienNuoc", tienNuoc);
            preview.put("phiDichVu", phiDichVu);
            preview.put("tongTien", tongTien);
            preview.put("coChiSo", coChiSo);
            previews.add(preview);
        }
        return previews;
    }

    /**
     * Tạo hóa đơn hàng loạt cho tất cả hợp đồng đang hiệu lực trong kỳ
     */
    @Transactional
    public int createBatchInvoices(String kyThanhToan, LocalDate hanThanhToan) {
        List<HopDong> activeContracts = hopDongRepository.findByTrangThai(
                com.nhatro.quanlynhatro.enums.TrangThaiHopDong.DANG_HIEU_LUC);
        int count = 0;

        for (HopDong hopDong : activeContracts) {
            if (hoaDonRepository.existsByHopDong_HopDongIdAndKyThanhToan(hopDong.getHopDongId(), kyThanhToan)) {
                continue;
            }
            try {
                HoaDon temp = new HoaDon();
                temp.setHanThanhToan(hanThanhToan);
                create(temp, hopDong.getHopDongId(), kyThanhToan);
                count++;
            } catch (Exception e) {
                // Bỏ qua phòng lỗi, tiếp tục phòng khác
            }
        }
        return count;
    }

    /**
     * Lấy giá thuê hiệu lực cho một kỳ thanh toán dựa trên phụ lục hợp đồng.
     * Nếu có phụ lục đã duyệt với ngayHieuLuc trong hoặc trước kỳ thanh toán → dùng giá mới.
     * Ngược lại → dùng giá gốc hợp đồng.
     * Đảm bảo giá mới chỉ áp dụng từ tháng phụ lục có hiệu lực trở đi.
     */
    private BigDecimal getGiaThueHieuLuc(HopDong hopDong, String kyThanhToan) {
        YearMonth kyTT = YearMonth.parse(kyThanhToan);

        List<PhuLucHopDong> approvedList = phuLucHopDongRepository
                .findApprovedByHopDongOrderByNgayHieuLucDesc(hopDong.getHopDongId());

        for (PhuLucHopDong phuLuc : approvedList) {
            if (phuLuc.getNgayHieuLuc() != null) {
                YearMonth thangHieuLuc = YearMonth.from(phuLuc.getNgayHieuLuc());
                // Kỳ thanh toán >= tháng phụ lục có hiệu lực → áp dụng giá mới
                if (!kyTT.isBefore(thangHieuLuc)) {
                    return phuLuc.getGiaThueMMoi();
                }
            }
        }

        return hopDong.getGiaThue();
    }
}
