package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.service.DichVuService;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/tenant/hoa-don")
@RequiredArgsConstructor
public class TenantHoaDonController {

    private final NguoiDungService nguoiDungService;
    private final HoaDonService hoaDonService;
    private final DichVuService dichVuService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            Long userId = currentUser.getUserId();
            String kyHienTai = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            List<HoaDon> allHoaDons = hoaDonService.findByKhachThueId(userId);
            List<HoaDon> hoaDonChuaTT = hoaDonService.findUnpaidByKhachThueId2(userId);
            List<HoaDon> hoaDonDaTT = hoaDonService.findPaidByKhachThueId(userId);
            List<HoaDon> hoaDonThangNay = hoaDonService.findByKhachThueAndKyThanhToan(userId, kyHienTai);

            BigDecimal tongNo = hoaDonService.getTongNoByKhachThue(userId);
            BigDecimal tongDaTT = hoaDonService.getTongDaThanhToanByKhachThue(userId);

            BigDecimal tongThangNay = hoaDonThangNay.stream()
                    .map(HoaDon::getTongTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long soQuaHan = hoaDonChuaTT.stream()
                    .filter(hd -> hd.getHanThanhToan() != null && hd.getHanThanhToan().isBefore(java.time.LocalDate.now()))
                    .count();

            List<GiaoDich> lichSuGD = hoaDonService.getLichSuGiaoDichByKhachThue(userId);
            List<GiaoDich> lichSuGDGanDay = lichSuGD.size() > 10 ? lichSuGD.subList(0, 10) : lichSuGD;

            model.addAttribute("danhSachDichVu", dichVuService.findAll());
            model.addAttribute("nguoiDung", currentUser);
            model.addAttribute("hoaDons", allHoaDons);
            model.addAttribute("hoaDonChuaTT", hoaDonChuaTT);
            model.addAttribute("hoaDonDaTT", hoaDonDaTT);
            model.addAttribute("hoaDonThangNay", hoaDonThangNay);
            model.addAttribute("tongNo", tongNo);
            model.addAttribute("tongDaTT", tongDaTT);
            model.addAttribute("tongThangNay", tongThangNay);
            model.addAttribute("soQuaHan", soQuaHan);
            model.addAttribute("soHoaDonChuaTT", hoaDonChuaTT.size());
            model.addAttribute("lichSuGD", lichSuGDGanDay);
            model.addAttribute("kyHienTai", kyHienTai);

            return "tenant/hoa-don/list";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            List<GiaoDich> giaoDichs = hoaDonService.findGiaoDichByHoaDonId(id);

            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("giaoDichs", giaoDichs);
            model.addAttribute("nguoiDung", currentUser);
            model.addAttribute("danhSachDichVu", dichVuService.findAll());
            return "tenant/hoa-don/detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hoa-don";
        }
    }

    /**
     * Khách chọn thanh toán chuyển khoản → tạo giao dịch CHỜ XÁC NHẬN
     */
    @PostMapping("/chuyen-khoan/{id}")
    public String chuyenKhoan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thanh toán hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            hoaDonService.taoBankTransferPending(hoaDon);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã ghi nhận chuyển khoản! Vui lòng chờ chủ trọ xác nhận.");
            return "redirect:/tenant/hoa-don/detail/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hoa-don/detail/" + id;
        }
    }

    /**
     * Thanh toán ví điện tử → chuyển đến cổng thanh toán
     */
    @PostMapping("/thanh-toan-online/{id}")
    public String thanhToanOnline(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thanh toán hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            return "redirect:/tenant/hoa-don/payment-gateway/" + id + "?phuongThuc=ONLINE";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hoa-don/detail/" + id;
        }
    }

    /**
     * Cổng thanh toán ví điện tử (giả lập)
     */
    @GetMapping("/payment-gateway/{id}")
    public String showPaymentGateway(@PathVariable Long id,
                                     @RequestParam String phuongThuc,
                                     Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thanh toán hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            String maDon = "HD" + hoaDon.getHoaDonId() + "-" + System.currentTimeMillis();
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("phuongThuc", phuongThuc);
            model.addAttribute("maDon", maDon);
            return "tenant/hoa-don/payment-gateway";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hoa-don";
        }
    }

    /**
     * Callback từ cổng thanh toán ví điện tử → tự động gạch nợ
     */
    @PostMapping("/payment-callback/{id}")
    public String paymentCallback(@PathVariable Long id,
                                  @RequestParam String ketQua,
                                  @RequestParam String phuongThuc,
                                  RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền");
                return "redirect:/tenant/hoa-don";
            }

            switch (ketQua) {
                case "THANH_CONG":
                    hoaDonService.thanhToan(hoaDon, PhuongThucThanhToan.ONLINE);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Thanh toán online thành công! Hóa đơn đã được gạch nợ tự động.");
                    break;
                case "THAT_BAI":
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Thanh toán thất bại. Vui lòng thử lại sau.");
                    break;
                case "HUY":
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Thanh toán đã bị hủy.");
                    break;
                default:
                    redirectAttributes.addFlashAttribute("errorMessage", "Kết quả không hợp lệ");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý thanh toán: " + e.getMessage());
        }
        return "redirect:/tenant/hoa-don/detail/" + id;
    }
}
