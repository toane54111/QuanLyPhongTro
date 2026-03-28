package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.PhongTro;
import com.nhatro.quanlynhatro.enums.TrangThaiPhong;
import com.nhatro.quanlynhatro.service.KhuTroService;
import com.nhatro.quanlynhatro.service.PhongTroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/landlord/phong-tro")
@RequiredArgsConstructor
public class PhongTroController {

    private final PhongTroService phongTroService;
    private final KhuTroService khuTroService;
    private final com.nhatro.quanlynhatro.repository.DichVuRepository dichVuRepository;
    private final com.nhatro.quanlynhatro.repository.ThanhVienPhongTroRepository thanhVienPhongTroRepository;

    @GetMapping
    public String list(@RequestParam(required = false) Long khuTroId,
                       @RequestParam(required = false) TrangThaiPhong trangThai,
                       Model model) {
        try {
            List<PhongTro> danhSachPhongTro = phongTroService.findAll(khuTroId, trangThai);
            Map<Integer, List<PhongTro>> phongTroByTang = danhSachPhongTro.stream()
                    .collect(Collectors.groupingBy(
                            phong -> phong.getTang() != null ? phong.getTang() : 0,
                            TreeMap::new,
                            Collectors.toList()
                    ));

            model.addAttribute("danhSachPhongTro", danhSachPhongTro); // keeps compatibility with empty state check
            model.addAttribute("phongTroByTang", phongTroByTang);
            model.addAttribute("danhSachKhuTro", khuTroService.findAll());
            model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
            model.addAttribute("selectedKhuTroId", khuTroId);
            model.addAttribute("selectedTrangThai", trangThai);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách phòng trọ: " + e.getMessage());
        }
        return "landlord/phong-tro/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PhongTro phongTro = phongTroService.findById(id);
            
            // Calculate remaining time
            int soNgayConLai = 0;
            int phanTramThoiGian = 0;
            if (phongTro.getHopDongHienTai() != null) {
                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate start = phongTro.getHopDongHienTai().getNgayBatDau();
                java.time.LocalDate end = phongTro.getHopDongHienTai().getNgayKetThuc();
                
                if (end != null && !now.isAfter(end)) {
                    soNgayConLai = (int) java.time.temporal.ChronoUnit.DAYS.between(now, end);
                }
                
                if (start != null && end != null) {
                    long totalDays = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    long passedDays = java.time.temporal.ChronoUnit.DAYS.between(start, now);
                    if (totalDays > 0) {
                        phanTramThoiGian = (int) ((passedDays * 100) / totalDays);
                        if (phanTramThoiGian < 0) phanTramThoiGian = 0;
                        if (phanTramThoiGian > 100) phanTramThoiGian = 100;
                    }
                }
            }

            // Provide mock assets if empty for UI demonstration
            List<com.nhatro.quanlynhatro.entity.TaiSanPhongTro> taiSanList = phongTro.getTaiSanPhongTros();
            if (taiSanList == null || taiSanList.isEmpty()) {
                taiSanList = List.of(
                    com.nhatro.quanlynhatro.entity.TaiSanPhongTro.builder().tenTaiSan("Máy lạnh Panasonic").tinhTrang("Tốt").build(),
                    com.nhatro.quanlynhatro.entity.TaiSanPhongTro.builder().tenTaiSan("Giường gỗ xoan đào").tinhTrang("Tốt").build(),
                    com.nhatro.quanlynhatro.entity.TaiSanPhongTro.builder().tenTaiSan("Tủ lạnh Aqua").tinhTrang("Khá").build()
                );
            }
            
            model.addAttribute("soNgayConLai", soNgayConLai);
            model.addAttribute("phanTramThoiGian", phanTramThoiGian);
            model.addAttribute("taiSanList", taiSanList);
            model.addAttribute("phongTro", phongTro);
            model.addAttribute("dichVus", dichVuRepository.findAll());
            return "landlord/phong-tro/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng trọ: " + e.getMessage());
            return "redirect:/landlord/phong-tro";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("phongTro", new PhongTro());
        model.addAttribute("danhSachKhuTro", khuTroService.findAll());
        model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
        model.addAttribute("isEdit", false);
        return "landlord/phong-tro/form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute PhongTro phongTro,
                         @RequestParam Long khuTroId,
                         RedirectAttributes redirectAttributes) {
        try {
            phongTroService.save(phongTro, khuTroId);
            redirectAttributes.addFlashAttribute("success", "Thêm phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PhongTro phongTro = phongTroService.findById(id);
            model.addAttribute("phongTro", phongTro);
            model.addAttribute("danhSachKhuTro", khuTroService.findAll());
            model.addAttribute("trangThaiPhongs", TrangThaiPhong.values());
            model.addAttribute("isEdit", true);
            return "landlord/phong-tro/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng trọ: " + e.getMessage());
            return "redirect:/landlord/phong-tro";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute PhongTro phongTro,
                         @RequestParam Long khuTroId,
                         RedirectAttributes redirectAttributes) {
        try {
            phongTro.setPhongId(id);
            phongTroService.update(phongTro, khuTroId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            phongTroService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa phòng trọ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa phòng trọ: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro";
    }

    @PostMapping("/{id}/add-member")
    public String addMember(@PathVariable Long id, 
                            @RequestParam String hoTen, 
                            @RequestParam String sdt, 
                            @RequestParam String cccd, 
                            RedirectAttributes redirectAttributes) {
        try {
            PhongTro phongTro = phongTroService.findById(id);
            com.nhatro.quanlynhatro.entity.ThanhVienPhongTro thanhVien = new com.nhatro.quanlynhatro.entity.ThanhVienPhongTro();
            thanhVien.setPhongTro(phongTro);
            thanhVien.setHoTen(hoTen);
            thanhVien.setSdt(sdt);
            thanhVien.setCccd(cccd);
            thanhVien.setNgayBatDau(java.time.LocalDate.now());
            thanhVien.setTrangThai("Đang ở");
            
            thanhVienPhongTroRepository.save(thanhVien);
            
            redirectAttributes.addFlashAttribute("success", "Đã thêm thành viên ở ghép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm thành viên: " + e.getMessage());
        }
        return "redirect:/landlord/phong-tro/" + id;
    }
}
