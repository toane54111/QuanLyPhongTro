package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiGiaoDich;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.service.ChiSoDienNuocService;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Controller
@RequestMapping("/landlord/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final HopDongService hopDongService;
    private final ThongBaoService thongBaoService;
    private final ChiSoDienNuocService chiSoDienNuocService;

    @GetMapping
    public String list(@RequestParam(required = false) TrangThaiHoaDon trangThai,
                       @RequestParam(required = false) String kyThanhToan,
                       Model model) {
        try {
            model.addAttribute("danhSachHoaDon", hoaDonService.findAll(trangThai, kyThanhToan));
            model.addAttribute("trangThaiHoaDons", TrangThaiHoaDon.values());
            model.addAttribute("selectedTrangThai", trangThai);
            model.addAttribute("selectedKyThanhToan", kyThanhToan);
            // Đếm giao dịch chờ xác nhận
            model.addAttribute("pendingCount", hoaDonService.findPendingTransactions().size());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
        }
        return "landlord/hoa-don/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("hoaDon", new HoaDon());
            List<HopDong> danhSachHopDong = hopDongService.findActiveContracts();
            model.addAttribute("danhSachHopDong", danhSachHopDong);
            model.addAttribute("currentMonth", YearMonth.now().toString());

            // Build map hopDongId → phongId để JS map từ hợp đồng sang phòng
            Map<Long, Long> hopDongPhongMap = new HashMap<>();
            // Build map phongId → { dienMoi, nuocMoi } từ chỉ số gần nhất
            Map<Long, Map<String, Integer>> latestReadings = new HashMap<>();

            for (HopDong hd : danhSachHopDong) {
                Long phongId = hd.getPhongTro().getPhongId();
                hopDongPhongMap.put(hd.getHopDongId(), phongId);

                if (!latestReadings.containsKey(phongId)) {
                    Optional<ChiSoDienNuoc> latest = chiSoDienNuocService.getLatestByPhongId(phongId);
                    if (latest.isPresent()) {
                        Map<String, Integer> data = new HashMap<>();
                        data.put("dienMoi", latest.get().getDienMoi());
                        data.put("nuocMoi", latest.get().getNuocMoi());
                        latestReadings.put(phongId, data);
                    }
                }
            }
            model.addAttribute("hopDongPhongMap", hopDongPhongMap);
            model.addAttribute("latestReadings", latestReadings);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải form tạo hóa đơn: " + e.getMessage());
        }
        return "landlord/hoa-don/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute HoaDon hoaDon,
                         @RequestParam Long hopDongId,
                         @RequestParam String kyThanhToan,
                         @RequestParam int dienCu,
                         @RequestParam int dienMoi,
                         @RequestParam int nuocCu,
                         @RequestParam int nuocMoi,
                         RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.createWithChiSo(hoaDon, hopDongId, kyThanhToan, dienCu, dienMoi, nuocCu, nuocMoi);
            redirectAttributes.addFlashAttribute("success", "Tạo hóa đơn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hóa đơn: " + e.getMessage());
        }
        return "redirect:/landlord/hoa-don";
    }

    @GetMapping("/api/preview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> previewSingle(
            @RequestParam Long hopDongId,
            @RequestParam String kyThanhToan,
            @RequestParam int dienCu,
            @RequestParam int dienMoi,
            @RequestParam int nuocCu,
            @RequestParam int nuocMoi) {
        try {
            Map<String, Object> result = hoaDonService.previewSingleInvoice(hopDongId, kyThanhToan, dienCu, dienMoi, nuocCu, nuocMoi);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // === LẬP HÓA ĐƠN HÀNG LOẠT (UC13 theo Activity Diagram) ===

    @GetMapping("/batch")
    public String showBatchForm(Model model) {
        model.addAttribute("currentMonth", YearMonth.now().toString());
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

            chiSoDienNuocService.findByPhongIdAndKyGhi(hoaDon.getHopDong().getPhongTro().getPhongId(), hoaDon.getKyThanhToan())
                    .ifPresent(cs -> model.addAttribute("chiSo", cs));

            return "landlord/hoa-don/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn: " + e.getMessage());
            return "redirect:/landlord/hoa-don";
        }
    }

    /**
     * Trang thu tiền mặt / xác nhận chuyển khoản
     */
    @GetMapping("/payment/{id}")
    public String showPaymentForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = hoaDonService.findById(id);
            List<GiaoDich> pendingGD = hoaDonService.findGiaoDichByHoaDonId(id).stream()
                    .filter(gd -> gd.getTrangThaiGD() == TrangThaiGiaoDich.CHO_XAC_NHAN)
                    .toList();
            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("pendingGD", pendingGD);
            return "landlord/hoa-don/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn: " + e.getMessage());
            return "redirect:/landlord/hoa-don";
        }
    }

    /**
     * Chủ trọ ghi nhận thu tiền mặt
     */
    @PostMapping("/thu-tien-mat/{id}")
    public String thuTienMat(@PathVariable Long id,
                             @RequestParam BigDecimal soTien,
                             @RequestParam(required = false) String ghiChu,
                             RedirectAttributes redirectAttributes) {
        try {
            GiaoDich gd = hoaDonService.thuTienMat(id, soTien, ghiChu);
            redirectAttributes.addFlashAttribute("success", "Thu tiền mặt thành công!");
            redirectAttributes.addFlashAttribute("bienLaiGD", gd.getGiaoDichId());
            return "redirect:/landlord/hoa-don/payment/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/landlord/hoa-don/payment/" + id;
        }
    }

    /**
     * Chủ trọ xác nhận đã nhận chuyển khoản
     */
    @PostMapping("/xac-nhan-gd/{giaoDichId}")
    public String xacNhanGiaoDich(@PathVariable Long giaoDichId,
                                  @RequestParam Long hoaDonId,
                                  RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.xacNhanGiaoDich(giaoDichId);
            redirectAttributes.addFlashAttribute("success", "Xác nhận chuyển khoản thành công!");
            return "redirect:/landlord/hoa-don/payment/" + hoaDonId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/landlord/hoa-don/payment/" + hoaDonId;
        }
    }

    /**
     * Chủ trọ từ chối giao dịch chuyển khoản
     */
    @PostMapping("/tu-choi-gd/{giaoDichId}")
    public String tuChoiGiaoDich(@PathVariable Long giaoDichId,
                                 @RequestParam Long hoaDonId,
                                 RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.tuChoiGiaoDich(giaoDichId);
            redirectAttributes.addFlashAttribute("success", "Đã từ chối giao dịch chuyển khoản.");
            return "redirect:/landlord/hoa-don/payment/" + hoaDonId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/landlord/hoa-don/payment/" + hoaDonId;
        }
    }

    /**
     * In biên lai thanh toán
     */
    @GetMapping("/bien-lai/{giaoDichId}")
    public String inBienLai(@PathVariable Long giaoDichId, Model model, RedirectAttributes redirectAttributes) {
        try {
            GiaoDich giaoDich = hoaDonService.findGiaoDichByHoaDonId(0L).stream().findFirst().orElse(null);
            // Tìm giao dịch từ repository thông qua service
            model.addAttribute("giaoDichId", giaoDichId);
            return "landlord/hoa-don/payment"; // Biên lai được in bằng JS trong payment page
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/landlord/hoa-don";
        }
    }
}
