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

    /**
     * Trang tổng quan thanh toán - dashboard hóa đơn khách thuê
     */
    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            Long userId = currentUser.getUserId();
            String kyHienTai = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            // Tất cả hóa đơn
            List<HoaDon> allHoaDons = hoaDonService.findByKhachThueId(userId);

            // Hóa đơn chưa thanh toán (ưu tiên hiển thị)
            List<HoaDon> hoaDonChuaTT = hoaDonService.findUnpaidByKhachThueId2(userId);

            // Hóa đơn đã thanh toán (lịch sử)
            List<HoaDon> hoaDonDaTT = hoaDonService.findPaidByKhachThueId(userId);

            // Hóa đơn tháng hiện tại
            List<HoaDon> hoaDonThangNay = hoaDonService.findByKhachThueAndKyThanhToan(userId, kyHienTai);

            // Thống kê tổng hợp
            BigDecimal tongNo = hoaDonService.getTongNoByKhachThue(userId);
            BigDecimal tongDaTT = hoaDonService.getTongDaThanhToanByKhachThue(userId);

            // Tổng tiền hóa đơn tháng hiện tại
            BigDecimal tongThangNay = hoaDonThangNay.stream()
                    .map(HoaDon::getTongTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Số hóa đơn quá hạn
            long soQuaHan = hoaDonChuaTT.stream()
                    .filter(hd -> hd.getHanThanhToan() != null && hd.getHanThanhToan().isBefore(java.time.LocalDate.now()))
                    .count();

            // Lịch sử giao dịch gần đây (top 10)
            List<GiaoDich> lichSuGD = hoaDonService.getLichSuGiaoDichByKhachThue(userId);
            List<GiaoDich> lichSuGDGanDay = lichSuGD.size() > 10 ? lichSuGD.subList(0, 10) : lichSuGD;

            // Danh sách dịch vụ (để hiển thị bảng giá)
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

            // Kiểm tra quyền sở hữu
            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            // Lịch sử giao dịch của hóa đơn này
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

    @PostMapping("/thanh-toan/{id}")
    public String thanhToan(@PathVariable Long id,
                            @RequestParam String phuongThuc,
                            RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HoaDon hoaDon = hoaDonService.findById(id);

            if (!hoaDon.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thanh toán hóa đơn này");
                return "redirect:/tenant/hoa-don";
            }

            PhuongThucThanhToan pt = PhuongThucThanhToan.valueOf(phuongThuc);

            // Nếu thanh toán ONLINE → chuyển hướng đến trang giả lập cổng thanh toán (UC22)
            if (pt == PhuongThucThanhToan.ONLINE) {
                return "redirect:/tenant/hoa-don/payment-gateway/" + id + "?phuongThuc=" + phuongThuc;
            }

            // Thanh toán tiền mặt/chuyển khoản → ghi nhận trực tiếp
            hoaDonService.thanhToan(hoaDon, pt);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán hóa đơn thành công!");
            return "redirect:/tenant/hoa-don/detail/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phương thức thanh toán không hợp lệ");
            return "redirect:/tenant/hoa-don/detail/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán thất bại: " + e.getMessage());
            return "redirect:/tenant/hoa-don/detail/" + id;
        }
    }

    /**
     * UC22: Hiển thị trang giả lập cổng thanh toán (VNPay/MoMo)
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
     * UC22: Callback xử lý kết quả từ cổng thanh toán
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
                            "Thanh toán online thành công! Hóa đơn đã được cập nhật.");
                    break;
                case "THAT_BAI":
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Thanh toán thất bại. Vui lòng thử lại sau.");
                    break;
                case "HUY":
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "Thanh toán đã bị hủy. Hóa đơn vẫn ở trạng thái chưa thanh toán.");
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
