package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.PhuLucHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.repository.HopDongRepository;
import com.nhatro.quanlynhatro.repository.PhuLucHopDongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhuLucHopDongService {

    private final PhuLucHopDongRepository phuLucHopDongRepository;
    private final HopDongRepository hopDongRepository;
    private final ThongBaoService thongBaoService;

    public Optional<PhuLucHopDong> findById(Long id) {
        return phuLucHopDongRepository.findById(id);
    }

    public List<PhuLucHopDong> findByHopDongId(Long hopDongId) {
        return phuLucHopDongRepository.findByHopDong_HopDongId(hopDongId);
    }

    public boolean hasPendingPhuLuc(Long hopDongId) {
        return phuLucHopDongRepository.existsByHopDong_HopDongIdAndTrangThai(
                hopDongId, TrangThaiYeuCau.CHO_PHE_DUYET);
    }

    /**
     * Chủ trọ tạo phụ lục gia hạn kèm giá mới → gửi thông báo cho khách duyệt
     */
    @Transactional
    public PhuLucHopDong create(Long hopDongId, BigDecimal giaThueMoi, LocalDate ngayKetThucMoi, String ghiChu) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (hopDong.getTrangThai() == TrangThaiHopDong.DA_CHAM_DUT) {
            throw new RuntimeException("Hợp đồng đã chấm dứt, không thể tạo phụ lục");
        }

        // Kiểm tra đã có phụ lục đang chờ duyệt chưa
        if (phuLucHopDongRepository.existsByHopDong_HopDongIdAndTrangThai(
                hopDongId, TrangThaiYeuCau.CHO_PHE_DUYET)) {
            throw new RuntimeException("Hợp đồng đang có phụ lục chờ khách duyệt. Vui lòng chờ khách phản hồi.");
        }

        // Validate ngày kết thúc mới
        if (ngayKetThucMoi.isBefore(hopDong.getNgayKetThuc()) || ngayKetThucMoi.isEqual(hopDong.getNgayKetThuc())) {
            throw new RuntimeException("Ngày kết thúc mới phải sau ngày kết thúc hiện tại ("
                    + hopDong.getNgayKetThuc() + ")");
        }

        // ngayHieuLuc = ngày kết thúc hiện tại của HĐ
        // Vì giá mới chỉ áp dụng cho giai đoạn gia hạn (từ ngày KT cũ trở đi),
        // KHÔNG áp dụng hồi tố cho giai đoạn đang thuê hiện tại.
        LocalDate ngayGiaMoiApDung = hopDong.getNgayKetThuc();

        PhuLucHopDong phuLuc = PhuLucHopDong.builder()
                .hopDong(hopDong)
                .giaThueMMoi(giaThueMoi)
                .ngayKetThucMoi(ngayKetThucMoi)
                .ngayHieuLuc(ngayGiaMoiApDung) // Lưu sẵn ngày giá mới bắt đầu
                .trangThai(TrangThaiYeuCau.CHO_PHE_DUYET)
                .ghiChu(ghiChu)
                .ngayTao(LocalDateTime.now())
                .build();

        PhuLucHopDong saved = phuLucHopDongRepository.save(phuLuc);

        // Gửi thông báo cho khách thuê
        Long khachThueId = hopDong.getKhachThue().getUserId();
        String soPhong = hopDong.getPhongTro().getSoPhong();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        thongBaoService.notifyUser(khachThueId,
                "Đề nghị gia hạn hợp đồng phòng " + soPhong,
                "Chủ trọ đề nghị gia hạn hợp đồng phòng " + soPhong
                        + " với giá thuê mới: " + String.format("%,.0f", giaThueMoi) + " VND/tháng"
                        + ", áp dụng từ " + ngayGiaMoiApDung.format(fmt)
                        + " đến " + ngayKetThucMoi.format(fmt) + "."
                        + " Vui lòng xem chi tiết và phản hồi.",
                "/tenant/hop-dong/phu-luc/" + saved.getPhuLucId());

        return saved;
    }

    /**
     * Khách thuê đồng ý phụ lục → cập nhật hợp đồng ngay
     */
    @Transactional
    public PhuLucHopDong approve(Long phuLucId) {
        PhuLucHopDong phuLuc = phuLucHopDongRepository.findById(phuLucId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ lục hợp đồng"));

        if (phuLuc.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Phụ lục này đã được xử lý trước đó");
        }

        // Duyệt phụ lục — giữ nguyên ngayHieuLuc đã set từ lúc tạo
        // (= ngày kết thúc cũ của HĐ = ngày giá mới bắt đầu áp dụng)
        phuLuc.setTrangThai(TrangThaiYeuCau.DA_PHE_DUYET);
        phuLucHopDongRepository.save(phuLuc);

        // Cập nhật hợp đồng: gia hạn ngày kết thúc
        HopDong hopDong = phuLuc.getHopDong();
        hopDong.setNgayKetThuc(phuLuc.getNgayKetThucMoi());
        hopDong.setTrangThai(TrangThaiHopDong.DANG_HIEU_LUC);
        hopDongRepository.save(hopDong);

        // Gửi thông báo cho chủ trọ
        String soPhong = hopDong.getPhongTro().getSoPhong();
        String tenKhach = hopDong.getKhachThue().getHoTen();
        thongBaoService.notifyAllLandlords(
                "Khách đồng ý phụ lục hợp đồng phòng " + soPhong,
                "Khách " + tenKhach + " đã đồng ý gia hạn hợp đồng phòng " + soPhong
                        + " với giá mới: " + String.format("%,.0f", phuLuc.getGiaThueMMoi()) + " VND/tháng."
                        + " Hợp đồng đã được cập nhật.",
                "/landlord/hop-dong/detail/" + hopDong.getHopDongId());

        return phuLuc;
    }

    /**
     * Khách thuê từ chối phụ lục → thông báo hợp đồng sẽ hết hạn
     */
    @Transactional
    public PhuLucHopDong reject(Long phuLucId, String lyDo) {
        PhuLucHopDong phuLuc = phuLucHopDongRepository.findById(phuLucId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ lục hợp đồng"));

        if (phuLuc.getTrangThai() != TrangThaiYeuCau.CHO_PHE_DUYET) {
            throw new RuntimeException("Phụ lục này đã được xử lý trước đó");
        }

        phuLuc.setTrangThai(TrangThaiYeuCau.DA_TU_CHOI);
        phuLuc.setGhiChu(phuLuc.getGhiChu() != null
                ? phuLuc.getGhiChu() + " | Lý do từ chối: " + lyDo
                : "Lý do từ chối: " + lyDo);
        phuLucHopDongRepository.save(phuLuc);

        HopDong hopDong = phuLuc.getHopDong();
        String soPhong = hopDong.getPhongTro().getSoPhong();
        String tenKhach = hopDong.getKhachThue().getHoTen();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Thông báo cho chủ trọ: khách từ chối
        thongBaoService.notifyAllLandlords(
                "Khách từ chối phụ lục hợp đồng phòng " + soPhong,
                "Khách " + tenKhach + " đã từ chối gia hạn hợp đồng phòng " + soPhong
                        + " với giá mới. Lý do: " + (lyDo != null ? lyDo : "Không có")
                        + ". Hợp đồng hiện tại sẽ hết hạn vào ngày "
                        + hopDong.getNgayKetThuc().format(fmt) + ".",
                "/landlord/hop-dong/detail/" + hopDong.getHopDongId());

        // Thông báo cho khách: hợp đồng sẽ hết hạn
        thongBaoService.notifyUser(hopDong.getKhachThue().getUserId(),
                "Hợp đồng phòng " + soPhong + " sẽ hết hạn",
                "Bạn đã từ chối đề nghị gia hạn hợp đồng phòng " + soPhong + " với giá mới."
                        + " Hợp đồng hiện tại sẽ hết hạn vào ngày "
                        + hopDong.getNgayKetThuc().format(fmt) + "."
                        + " Nếu muốn tiếp tục thuê, vui lòng liên hệ chủ trọ.",
                "/tenant/hop-dong/detail/" + hopDong.getHopDongId());

        return phuLuc;
    }

    /**
     * Lấy giá thuê hiệu lực cho một kỳ thanh toán cụ thể.
     * Nếu có phụ lục đã duyệt mà ngayHieuLuc nằm trong hoặc trước kỳ → dùng giá mới
     * Ngược lại → dùng giá gốc của hợp đồng
     */
    public BigDecimal getGiaThueHieuLuc(Long hopDongId, String kyThanhToan) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        // Parse kỳ thanh toán (format: "yyyy-MM") thành YearMonth
        YearMonth kyTT = YearMonth.parse(kyThanhToan);

        // Tìm tất cả phụ lục đã duyệt, sắp xếp theo ngayHieuLuc giảm dần
        List<PhuLucHopDong> approvedList = phuLucHopDongRepository
                .findApprovedByHopDongOrderByNgayHieuLucDesc(hopDongId);

        for (PhuLucHopDong phuLuc : approvedList) {
            if (phuLuc.getNgayHieuLuc() != null) {
                YearMonth thangHieuLuc = YearMonth.from(phuLuc.getNgayHieuLuc());
                // Nếu kỳ thanh toán >= tháng phụ lục có hiệu lực → áp dụng giá mới
                if (!kyTT.isBefore(thangHieuLuc)) {
                    return phuLuc.getGiaThueMMoi();
                }
            }
        }

        // Không có phụ lục nào áp dụng → dùng giá gốc hợp đồng
        return hopDong.getGiaThue();
    }

    /**
     * Lấy thông tin giá thuê để hiển thị trên trang chi tiết hợp đồng.
     *
     * Trả về Map gồm:
     *   - giaHienTai (BigDecimal): giá đang áp dụng tháng hiện tại
     *   - giaGoc (BigDecimal): giá gốc trên hợp đồng
     *   - coLichSuGia (boolean): có phụ lục đã duyệt hay không
     *   - giaSapToi (BigDecimal | null): giá sẽ áp dụng trong tương lai (phụ lục đã duyệt nhưng chưa tới tháng hiệu lực)
     *   - thangApDungGiaMoi (String | null): tháng/năm giá mới bắt đầu áp dụng (format "MM/yyyy")
     *   - pendingGiaMoi (BigDecimal | null): giá trong phụ lục đang chờ khách duyệt
     *   - pendingPhuLucId (Long | null): id phụ lục đang chờ duyệt
     */
    public Map<String, Object> getThongTinGiaThue(Long hopDongId) {
        HopDong hopDong = hopDongRepository.findById(hopDongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        Map<String, Object> info = new HashMap<>();
        YearMonth thangHienTai = YearMonth.now();
        DateTimeFormatter fmtMonth = DateTimeFormatter.ofPattern("MM/yyyy");

        // Lấy tất cả phụ lục đã duyệt, sắp xếp ngayHieuLuc giảm dần
        List<PhuLucHopDong> approvedList = phuLucHopDongRepository
                .findApprovedByHopDongOrderByNgayHieuLucDesc(hopDongId);

        BigDecimal giaHienTai = hopDong.getGiaThue(); // mặc định = giá gốc
        BigDecimal giaSapToi = null;
        String thangApDungGiaMoi = null;

        // Duyệt danh sách (đã sắp xếp giảm dần theo ngayHieuLuc)
        for (PhuLucHopDong phuLuc : approvedList) {
            if (phuLuc.getNgayHieuLuc() == null) continue;

            YearMonth thangHL = YearMonth.from(phuLuc.getNgayHieuLuc());

            if (!thangHienTai.isBefore(thangHL)) {
                // Tháng hiện tại >= tháng hiệu lực → đây là giá đang áp dụng
                giaHienTai = phuLuc.getGiaThueMMoi();
                break; // vì list giảm dần, gặp cái đầu tiên thỏa là mới nhất
            } else {
                // Tháng hiện tại < tháng hiệu lực → giá này chưa áp dụng, là "sắp tới"
                giaSapToi = phuLuc.getGiaThueMMoi();
                thangApDungGiaMoi = thangHL.format(fmtMonth);
                // tiếp tục duyệt để tìm giá đang áp dụng hiện tại (nếu có)
            }
        }

        info.put("giaHienTai", giaHienTai);
        info.put("giaGoc", hopDong.getGiaThue());
        info.put("coLichSuGia", !approvedList.isEmpty());
        info.put("giaSapToi", giaSapToi);
        info.put("thangApDungGiaMoi", thangApDungGiaMoi);

        // Kiểm tra phụ lục đang chờ duyệt
        List<PhuLucHopDong> pendingList = phuLucHopDongRepository
                .findByHopDong_HopDongIdAndTrangThai(hopDongId, TrangThaiYeuCau.CHO_PHE_DUYET);
        if (!pendingList.isEmpty()) {
            PhuLucHopDong pending = pendingList.get(0);
            info.put("pendingGiaMoi", pending.getGiaThueMMoi());
            info.put("pendingPhuLucId", pending.getPhuLucId());
        } else {
            info.put("pendingGiaMoi", null);
            info.put("pendingPhuLucId", null);
        }

        return info;
    }
}
