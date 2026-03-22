package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.GiaoDich;
import com.nhatro.quanlynhatro.entity.HoaDon;
import com.nhatro.quanlynhatro.enums.PhuongThucThanhToan;
import com.nhatro.quanlynhatro.enums.TrangThaiHoaDon;
import com.nhatro.quanlynhatro.service.HoaDonService;
import com.nhatro.quanlynhatro.service.HopDongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService hoaDonService;
    private final HopDongService hopDongService;

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
