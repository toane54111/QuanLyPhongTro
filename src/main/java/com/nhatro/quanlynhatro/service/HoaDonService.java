package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.DichVu;
import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiGiaoDich;
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

    public List<HoaDon> findUnpaidByKhachThueId2(Long khachThueId) {
        return hoaDonRepository.findUnpaidByKhachThue(khachThueId);
    }

    public List<HoaDon> findPaidByKhachThueId(Long khachThueId) {
        return hoaDonRepository.findPaidByKhachThue(khachThueId);
    }

    public List<HoaDon> findByKhachThueAndKyThanhToan(Long khachThueId, String kyThanhToan) {
        return hoaDonRepository.findByKhachThueAndKyThanhToan(khachThueId, kyThanhToan);
    }

    public BigDecimal getTongNoByKhachThue(Long khachThueId) {
        List<HoaDon> unpaid = hoaDonRepository.findUnpaidByKhachThue(khachThueId);
        return unpaid.stream()
                .map(HoaDon::getConNo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTongDaThanhToanByKhachThue(Long khachThueId) {
        return giaoDichRepository.sumTotalPaidByKhachThue(khachThueId);
    }

    public List<GiaoDich> getLichSuGiaoDichByKhachThue(Long khachThueId) {
        return giaoDichRepository.findByKhachThueOrderByNgayDesc(khachThueId);
    }

    /**
     * Tạo hóa đơn kèm ghi chỉ số điện nước (gộp 2 bước thành 1).
     * 1. Validate + lưu chi_so_dien_nuoc
     * 2. Tạo hóa đơn dựa trên chi_so vừa lưu
     */
    @Transactional
    public HoaDon createWithChiSo(HoaDon hoaDon, Long hopDongId, String kyThanhToan,
                                   int dienCu, int dienMoi, int nuocCu, int nuocMoi) {
        YearMonth kyTT = YearMonth.parse(kyThanhToan);
        if (kyTT.isAfter(YearMonth.now())) {
            throw new RuntimeException("Không thể lập hóa đơn cho kỳ thanh toán trong tương lai");
        }

        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + hopDongId));

        Long phongId = hopDong.getPhongTro().getPhongId();

        // Validate chỉ số
        if (dienMoi < dienCu) {
            throw new RuntimeException("Chỉ số điện mới phải lớn hơn hoặc bằng chỉ số điện cũ");
        }
        if (nuocMoi < nuocCu) {
            throw new RuntimeException("Chỉ số nước mới phải lớn hơn hoặc bằng chỉ số nước cũ");
        }

        // Tạo hoặc cập nhật chi_so_dien_nuoc
        Optional<ChiSoDienNuoc> existingChiSo = chiSoDienNuocRepository
                .findByPhongTro_PhongIdAndKyGhi(phongId, kyThanhToan);

        ChiSoDienNuoc chiSo;
        if (existingChiSo.isPresent()) {
            chiSo = existingChiSo.get();
        } else {
            chiSo = new ChiSoDienNuoc();
            chiSo.setPhongTro(hopDong.getPhongTro());
            chiSo.setKyGhi(kyThanhToan);
        }
        chiSo.setDienCu(dienCu);
        chiSo.setDienMoi(dienMoi);
        chiSo.setNuocCu(nuocCu);
        chiSo.setNuocMoi(nuocMoi);
        chiSo.setNgayGhi(LocalDate.now());
        chiSoDienNuocRepository.save(chiSo);

        // Tạo hóa đơn (sẽ tự đọc chi_so vừa lưu để tính tiền)
        return create(hoaDon, hopDongId, kyThanhToan);
    }

    /**
     * Lấy dữ liệu xem trước hóa đơn (cho ajax frontend)
     */
    public Map<String, Object> previewSingleInvoice(Long hopDongId, String kyThanhToan, int dienCu, int dienMoi, int nuocCu, int nuocMoi) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        BigDecimal tienPhong = getGiaThueHieuLuc(hopDong, kyThanhToan);

        int tieuThuDien = Math.max(0, dienMoi - dienCu);
        int tieuThuNuoc = Math.max(0, nuocMoi - nuocCu);

        BigDecimal tienDien = BigDecimal.ZERO;
        BigDecimal tienNuoc = BigDecimal.ZERO;
        BigDecimal donGiaDien = BigDecimal.ZERO;
        BigDecimal donGiaNuoc = BigDecimal.ZERO;

        Optional<DichVu> dienDV = dichVuRepository.findByTenDV("Điện");
        if (dienDV.isPresent()) {
            donGiaDien = dienDV.get().getDonGia();
            tienDien = donGiaDien.multiply(BigDecimal.valueOf(tieuThuDien));
        }

        Optional<DichVu> nuocDV = dichVuRepository.findByTenDV("Nước");
        if (nuocDV.isPresent()) {
            donGiaNuoc = nuocDV.get().getDonGia();
            tienNuoc = donGiaNuoc.multiply(BigDecimal.valueOf(tieuThuNuoc));
        }

        BigDecimal phiDichVu = BigDecimal.ZERO;
        List<Map<String, Object>> cacDichVuKhac = new ArrayList<>();

        List<DichVu> allServices = dichVuRepository.findAll();
        for (DichVu dv : allServices) {
            if (!"Điện".equals(dv.getTenDV()) && !"Nước".equals(dv.getTenDV())) {
                phiDichVu = phiDichVu.add(dv.getDonGia());
                Map<String, Object> dvMap = new HashMap<>();
                dvMap.put("tenDV", dv.getTenDV());
                dvMap.put("donGia", dv.getDonGia());
                cacDichVuKhac.add(dvMap);
            }
        }

        BigDecimal tongTien = tienPhong.add(tienDien).add(tienNuoc).add(phiDichVu);

        Map<String, Object> result = new HashMap<>();
        result.put("tienPhong", tienPhong);
        result.put("tienDien", tienDien);
        result.put("tienNuoc", tienNuoc);
        result.put("phiDichVu", phiDichVu);
        result.put("tongTien", tongTien);
        result.put("donGiaDien", donGiaDien);
        result.put("donGiaNuoc", donGiaNuoc);
        result.put("tieuThuDien", tieuThuDien);
        result.put("tieuThuNuoc", tieuThuNuoc);
        result.put("cacDichVuKhac", cacDichVuKhac);

        return result;
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
        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.DA_XAC_NHAN);
        if (giaoDich.getNgayGiaoDich() == null) {
            giaoDich.setNgayGiaoDich(LocalDateTime.now());
        }
        giaoDichRepository.save(giaoDich);

        capNhatTrangThaiHoaDon(hoaDon);
    }

    /**
     * Thanh toán trực tiếp (ví điện tử - tự động xác nhận)
     */
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
        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.DA_XAC_NHAN);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setGhiChu("Thanh toán qua " + phuongThuc.name());
        giaoDichRepository.save(giaoDich);

        hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonRepository.save(hoaDon);
    }

    /**
     * Khách tạo yêu cầu chuyển khoản → trạng thái CHỜ XÁC NHẬN
     */
    @Transactional
    public GiaoDich taoBankTransferPending(HoaDon hoaDon) {
        BigDecimal conNo = hoaDon.getConNo();
        if (conNo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Hóa đơn đã được thanh toán đầy đủ");
        }

        // Kiểm tra đã có giao dịch chờ xác nhận chưa
        List<GiaoDich> pending = giaoDichRepository.findByHoaDon_HoaDonIdAndTrangThaiGD(
                hoaDon.getHoaDonId(), TrangThaiGiaoDich.CHO_XAC_NHAN);
        if (!pending.isEmpty()) {
            throw new RuntimeException("Đã có giao dịch chuyển khoản đang chờ xác nhận cho hóa đơn này");
        }

        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setHoaDon(hoaDon);
        giaoDich.setSoTien(conNo);
        giaoDich.setPhuongThuc(PhuongThucThanhToan.CHUYEN_KHOAN);
        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.CHO_XAC_NHAN);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setGhiChu("Chuyển khoản ngân hàng - chờ xác nhận");
        return giaoDichRepository.save(giaoDich);
    }

    /**
     * Chủ trọ xác nhận đã nhận chuyển khoản
     */
    @Transactional
    public void xacNhanGiaoDich(Long giaoDichId) {
        GiaoDich giaoDich = giaoDichRepository.findById(giaoDichId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if (giaoDich.getTrangThaiGD() != TrangThaiGiaoDich.CHO_XAC_NHAN) {
            throw new RuntimeException("Giao dịch này không ở trạng thái chờ xác nhận");
        }

        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.DA_XAC_NHAN);
        giaoDich.setGhiChu("Chuyển khoản ngân hàng - đã xác nhận");
        giaoDichRepository.save(giaoDich);

        // Cập nhật trạng thái hóa đơn
        HoaDon hoaDon = giaoDich.getHoaDon();
        capNhatTrangThaiHoaDon(hoaDon);
    }

    /**
     * Chủ trọ từ chối giao dịch chuyển khoản
     */
    @Transactional
    public void tuChoiGiaoDich(Long giaoDichId) {
        GiaoDich giaoDich = giaoDichRepository.findById(giaoDichId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch"));

        if (giaoDich.getTrangThaiGD() != TrangThaiGiaoDich.CHO_XAC_NHAN) {
            throw new RuntimeException("Giao dịch này không ở trạng thái chờ xác nhận");
        }

        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.DA_HUY);
        giaoDich.setGhiChu("Chuyển khoản bị từ chối - chưa nhận được tiền");
        giaoDichRepository.save(giaoDich);
    }

    /**
     * Chủ trọ ghi nhận thu tiền mặt
     */
    @Transactional
    public GiaoDich thuTienMat(Long hoaDonId, BigDecimal soTien, String ghiChu) {
        HoaDon hoaDon = findById(hoaDonId);
        if (hoaDon.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán đầy đủ");
        }
        if (soTien.compareTo(hoaDon.getConNo()) > 0) {
            throw new RuntimeException("Số tiền thu vượt quá số nợ còn lại");
        }

        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setHoaDon(hoaDon);
        giaoDich.setSoTien(soTien);
        giaoDich.setPhuongThuc(PhuongThucThanhToan.TIEN_MAT);
        giaoDich.setTrangThaiGD(TrangThaiGiaoDich.DA_XAC_NHAN);
        giaoDich.setNgayGiaoDich(LocalDateTime.now());
        giaoDich.setGhiChu(ghiChu != null && !ghiChu.isBlank() ? ghiChu : "Thu tiền mặt");
        giaoDichRepository.save(giaoDich);

        capNhatTrangThaiHoaDon(hoaDon);
        return giaoDich;
    }

    /**
     * Cập nhật trạng thái hóa đơn dựa trên tổng đã thanh toán (đã xác nhận)
     */
    private void capNhatTrangThaiHoaDon(HoaDon hoaDon) {
        BigDecimal totalPaid = giaoDichRepository.sumSoTienByHoaDonId(hoaDon.getHoaDonId());
        if (totalPaid.compareTo(hoaDon.getTongTien()) >= 0) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            hoaDon.setTrangThai(TrangThaiHoaDon.THANH_TOAN_MOT_PHAN);
        } else {
            hoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);
        }
        hoaDonRepository.save(hoaDon);
    }

    /**
     * Danh sách giao dịch chờ xác nhận (cho chủ trọ)
     */
    public List<GiaoDich> findPendingTransactions() {
        return giaoDichRepository.findAllPending();
    }

    public boolean existsByHopDongIdAndKyThanhToan(Long hopDongId, String kyThanhToan) {
        return hoaDonRepository.existsByHopDong_HopDongIdAndKyThanhToan(hopDongId, kyThanhToan);
    }

    /**
     * Xem trước (preview) hóa đơn hàng loạt cho tất cả hợp đồng đang hiệu lực
     * Trả về danh sách Map chứa thông tin dự kiến cho từng phòng
     */
    public List<Map<String, Object>> previewBatchInvoices(String kyThanhToan) {
        YearMonth kyTT = YearMonth.parse(kyThanhToan);
        if (kyTT.isAfter(YearMonth.now())) {
            throw new RuntimeException("Không thể lập hóa đơn cho kỳ thanh toán trong tương lai");
        }

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
        YearMonth kyTT = YearMonth.parse(kyThanhToan);
        if (kyTT.isAfter(YearMonth.now())) {
            throw new RuntimeException("Không thể lập hóa đơn cho kỳ thanh toán trong tương lai");
        }

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
