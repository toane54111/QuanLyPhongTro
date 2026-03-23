package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/landlord/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final HopDongService hopDongService;
    private final ThongBaoService thongBaoService;

    @GetMapping
    public String list(@RequestParam(required = false) TrangThaiHoaDon trangThai,
                       @RequestParam(required = false) String kyThanhToan,
                       Model model) {
        try {
            model.addAttribute("danhSachHoaDon", hoaDonService.findAll(trangThai, kyThanhToan));
            model.addAttribute("trangThaiHoaDons", TrangThaiHoaDon.values());
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("selectedKyThanhToan", kyThanhToan);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
        }
        return "landlord/hoa-don/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("hoaDon", new HoaDon());
            model.addAttribute("danhSachHopDong", hopDongService.findActiveContracts());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải form tạo hóa đơn: " + e.getMessage());
        }
        return "landlord/hoa-don/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute HoaDon hoaDon,
                         @RequestParam Long hopDongId,
                         @RequestParam String kyThanhToan,
                         RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.create(hoaDon, hopDongId, kyThanhToan);
            redirectAttributes.addFlashAttribute("success", "Tạo hóa đơn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hóa đơn: " + e.getMessage());
        }
        return "redirect:/landlord/hoa-don";
    }

    // === LẬP HÓA ĐƠN HÀNG LOẠT (UC13 theo Activity Diagram) ===

    @GetMapping("/batch")
    public String showBatchForm(Model model) {
        return "landlord/hoa-don/batch-form";
    }

    @PostMapping("/batch/preview")
    public String previewBatch(@RequestParam String kyThanhToan,
                               @RequestParam(required = false) String hanThanhToan,
                               Model model) {
        try {
            List<Map<String, Object>> previews = hoaDonService.previewBatchInvoices(kyThanhToan);
            model.addAttribute("previews", previews);
            model.addAttribute("kyThanhToan", kyThanhToan);
            model.addAttribute("hanThanhToan", hanThanhToan != null && !hanThanhToan.isEmpty()
                    ? hanThanhToan : LocalDate.now().plusDays(15).toString());

            if (previews.isEmpty()) {
                model.addAttribute("warning", "Không có phòng nào cần lập hóa đơn cho kỳ " + kyThanhToan
                        + " (đã lập hết hoặc không có hợp đồng hiệu lực).");
            }

            long phongThieuChiSo = previews.stream().filter(p -> !(Boolean) p.get("coChiSo")).count();
            if (phongThieuChiSo > 0) {
                model.addAttribute("chiSoWarning",
                        "Có " + phongThieuChiSo + " phòng chưa ghi chỉ số điện nước cho kỳ này.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xem trước hóa đơn: " + e.getMessage());
        }
        return "landlord/hoa-don/batch-preview";
    }

    @PostMapping("/batch/create")
    public String createBatch(@RequestParam String kyThanhToan,
                              @RequestParam(required = false) String hanThanhToan,
                              RedirectAttributes redirectAttributes) {
        try {
            LocalDate han = (hanThanhToan != null && !hanThanhToan.isEmpty())
                    ? LocalDate.parse(hanThanhToan) : LocalDate.now().plusDays(15);
            int count = hoaDonService.createBatchInvoices(kyThanhToan, han);

            // UC14 (extend): Gửi thông báo cho khách thuê
            List<HopDong> activeContracts = hopDongService.findActiveContracts();
            for (HopDong hd : activeContracts) {
                if (hoaDonService.existsByHopDongIdAndKyThanhToan(hd.getHopDongId(), kyThanhToan)) {
                    thongBaoService.notifyUser(
                            hd.getKhachThue().getUserId(),
                            "Hóa đơn mới - Kỳ " + kyThanhToan,
                            "Hóa đơn phòng " + hd.getPhongTro().getSoPhong() + " đã được lập cho kỳ " + kyThanhToan
                                    + ". Vui lòng thanh toán trước hạn.",
                            "/tenant/hoa-don");
                }
            }

            redirectAttributes.addFlashAttribute("success",
                    "Tạo hóa đơn hàng loạt thành công! Đã tạo " + count + " hóa đơn cho kỳ " + kyThanhToan);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hóa đơn hàng loạt: " + e.getMessage());
        }
        return "redirect:/landlord/hoa-don";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = hoaDonService.findById(id);
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("danhSachGiaoDich", hoaDonService.findGiaoDichByHoaDonId(id));
            return "landlord/hoa-don/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn: " + e.getMessage());
            return "redirect:/landlord/hoa-don";
        }
    }

    @GetMapping("/payment/{id}")
    public String showPaymentForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = hoaDonService.findById(id);
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("giaoDich", new GiaoDich());
            model.addAttribute("phuongThucThanhToans", PhuongThucThanhToan.values());
            return "landlord/hoa-don/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn: " + e.getMessage());
            return "redirect:/landlord/hoa-don";
        }
    }

    @PostMapping("/payment/{id}")
    public String recordPayment(@PathVariable Long id,
                                @ModelAttribute GiaoDich giaoDich,
                                RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.recordPayment(id, giaoDich);
            redirectAttributes.addFlashAttribute("success", "Ghi nhận thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi ghi nhận thanh toán: " + e.getMessage());
        }
        return "redirect:/landlord/hoa-don/detail/" + id;
    }
}
