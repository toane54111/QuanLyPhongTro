package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
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
                         @RequestParam Long khachThueId,
                         RedirectAttributes redirectAttributes) {
        try {
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
                         @RequestParam Integer soThangGiaHan,
                         RedirectAttributes redirectAttributes) {
        try {
            hopDongService.extend(id, soThangGiaHan);
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
