package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.KhachThueService;
import com.nhatro.quanlynhatro.service.PhongTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/landlord/hop-dong")
@RequiredArgsConstructor
public class HopDongController {

    private final HopDongService hopDongService;
    private final PhongTroService phongTroService;
    private final KhachThueService khachThueService;

    @GetMapping
    public String list(@RequestParam(required = false) TrangThaiHopDong trangThai,
                       Model model) {
        try {
            model.addAttribute("danhSachHopDong", hopDongService.findAll(trangThai));
            model.addAttribute("trangThaiHopDongs", TrangThaiHopDong.values());
            model.addAttribute("selectedTrangThai", trangThai);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách hợp đồng: " + e.getMessage());
        }
        return "landlord/hop-dong/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            model.addAttribute("hopDong", new HopDong());
            model.addAttribute("danhSachPhongTrong", phongTroService.findEmptyRooms());
            model.addAttribute("danhSachKhachThue", khachThueService.findTenantsWithoutActiveContract());
            model.addAttribute("isEdit", false);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải form tạo hợp đồng: " + e.getMessage());
        }
        return "landlord/hop-dong/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute HopDong hopDong,
                         @RequestParam Long phongId,
                         @RequestParam(required = false) Long khachThueId,
                         @RequestParam(required = false) String newKhachHoTen,
                         @RequestParam(required = false) String newKhachEmail,
                         @RequestParam(required = false) String newKhachSdt,
                         @RequestParam(required = false) String newKhachCccd,
                         @RequestParam(defaultValue = "false") boolean taoKhachMoi,
                         RedirectAttributes redirectAttributes) {
        try {
            // Nếu chọn tạo khách thuê mới (theo Activity Diagram UC07)
            if (taoKhachMoi) {
                NguoiDung khachMoi = new NguoiDung();
                khachMoi.setHoTen(newKhachHoTen);
                khachMoi.setEmail(newKhachEmail);
                khachMoi.setSdt(newKhachSdt);
                khachMoi.setCccd(newKhachCccd);
                NguoiDung saved = khachThueService.create(khachMoi);
                khachThueId = saved.getUserId();
            }

            if (khachThueId == null) {
                throw new RuntimeException("Vui lòng chọn khách thuê hoặc tạo khách thuê mới");
            }

            hopDongService.create(hopDong, phongId, khachThueId);
            redirectAttributes.addFlashAttribute("success", "Tạo hợp đồng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hợp đồng: " + e.getMessage());
        }
        return "redirect:/landlord/hop-dong";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HopDong hopDong = hopDongService.getById(id);
            model.addAttribute("hopDong", hopDong);
            return "landlord/hop-dong/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hợp đồng: " + e.getMessage());
            return "redirect:/landlord/hop-dong";
        }
    }

    @GetMapping("/extend/{id}")
    public String showExtendForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HopDong hopDong = hopDongService.getById(id);
            model.addAttribute("hopDong", hopDong);
            return "landlord/hop-dong/extend";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hợp đồng: " + e.getMessage());
            return "redirect:/landlord/hop-dong";
        }
    }

    @PostMapping("/extend/{id}")
    public String extend(@PathVariable Long id,
                         @RequestParam String ngayKetThucMoi,
                         RedirectAttributes redirectAttributes) {
        try {
            java.time.LocalDate ngayMoi = java.time.LocalDate.parse(ngayKetThucMoi);
            hopDongService.extend(id, ngayMoi);
            redirectAttributes.addFlashAttribute("success", "Gia hạn hợp đồng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gia hạn hợp đồng: " + e.getMessage());
        }
        return "redirect:/landlord/hop-dong";
    }

    @PostMapping("/terminate/{id}")
    public String terminate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            hopDongService.terminate(id);
            redirectAttributes.addFlashAttribute("success", "Chấm dứt hợp đồng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi chấm dứt hợp đồng: " + e.getMessage());
        }
        return "redirect:/landlord/hop-dong";
    }
}
