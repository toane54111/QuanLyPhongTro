package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.ChiSoDienNuoc;
import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.service.ChiSoDienNuocService;
import com.nhatro.quanlynhatro.service.PhongTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/landlord/chi-so")
@RequiredArgsConstructor
public class ChiSoDienNuocController {

    private final ChiSoDienNuocService chiSoDienNuocService;
    private final PhongTroService phongTroService;

    @GetMapping
    public String list(@RequestParam(required = false) String kyGhi, Model model) {
        try {
            model.addAttribute("danhSachChiSo", chiSoDienNuocService.findAll(kyGhi));
            model.addAttribute("selectedKyGhi", kyGhi);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách chỉ số: " + e.getMessage());
        }
        return "landlord/chi-so/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        try {
            ChiSoDienNuoc chiSoDienNuoc = new ChiSoDienNuoc();
            // Auto-fill kỳ ghi = tháng hiện tại, ngày ghi = hôm nay
            chiSoDienNuoc.setKyGhi(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
            chiSoDienNuoc.setNgayGhi(java.time.LocalDate.now());

            model.addAttribute("chiSoDienNuoc", chiSoDienNuoc);

            List<PhongTro> danhSachPhongTro = phongTroService.findOccupiedRooms();
            model.addAttribute("danhSachPhongTro", danhSachPhongTro);
            model.addAttribute("isEdit", false);

            // Build map phongId → {dienMoi, nuocMoi} từ chỉ số gần nhất
            Map<Long, Map<String, Integer>> latestReadings = new HashMap<>();
            for (PhongTro phong : danhSachPhongTro) {
                Optional<ChiSoDienNuoc> latest = chiSoDienNuocService.getLatestByPhongId(phong.getPhongId());
                if (latest.isPresent()) {
                    Map<String, Integer> data = new HashMap<>();
                    data.put("dienMoi", latest.get().getDienMoi());
                    data.put("nuocMoi", latest.get().getNuocMoi());
                    latestReadings.put(phong.getPhongId(), data);
                }
            }
            model.addAttribute("latestReadings", latestReadings);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải form ghi chỉ số: " + e.getMessage());
        }
        return "landlord/chi-so/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ChiSoDienNuoc chiSoDienNuoc,
                         @RequestParam Long phongId,
                         RedirectAttributes redirectAttributes) {
        try {
            chiSoDienNuocService.save(chiSoDienNuoc, phongId);
            redirectAttributes.addFlashAttribute("success", "Ghi chỉ số điện nước thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi ghi chỉ số: " + e.getMessage());
        }
        return "redirect:/landlord/chi-so";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ChiSoDienNuoc chiSoDienNuoc = chiSoDienNuocService.findById(id);
            model.addAttribute("chiSoDienNuoc", chiSoDienNuoc);
            model.addAttribute("danhSachPhongTro", phongTroService.findOccupiedRooms());
            model.addAttribute("isEdit", true);
            return "landlord/chi-so/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chỉ số: " + e.getMessage());
            return "redirect:/landlord/chi-so";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute ChiSoDienNuoc chiSoDienNuoc,
                         @RequestParam Long phongId,
                         RedirectAttributes redirectAttributes) {
        try {
            chiSoDienNuoc.setChiSoId(id);
            chiSoDienNuocService.update(chiSoDienNuoc, phongId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật chỉ số điện nước thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật chỉ số: " + e.getMessage());
        }
        return "redirect:/landlord/chi-so";
    }
}
