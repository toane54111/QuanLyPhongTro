package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.entity.YeuCauSuCo;
import com.nhatro.quanlynhatro.enums.MucDoUuTien;
import com.nhatro.quanlynhatro.enums.TrangThaiSuCo;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import com.nhatro.quanlynhatro.service.ThongBaoService;
import com.nhatro.quanlynhatro.service.YeuCauSuCoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tenant/su-co")
@RequiredArgsConstructor
public class TenantSuCoController {

    private final NguoiDungService nguoiDungService;
    private final YeuCauSuCoService yeuCauSuCoService;
    private final HopDongService hopDongService;
    private final ThongBaoService thongBaoService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            List<YeuCauSuCo> suCos = yeuCauSuCoService.findByKhachThueId(currentUser.getUserId());
            model.addAttribute("suCos", suCos);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/su-co/list";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();

            // Kiểm tra khách thuê có hợp đồng đang hiệu lực không
            HopDong activeContract = hopDongService.findActiveByKhachThueId(currentUser.getUserId())
                    .orElse(null);
            if (activeContract == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có hợp đồng đang hiệu lực để báo sự cố");
                return "redirect:/tenant/su-co";
            }

            model.addAttribute("nguoiDung", currentUser);
            model.addAttribute("hopDong", activeContract);
            model.addAttribute("mucDoUuTiens", MucDoUuTien.values());
            return "tenant/su-co/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/su-co";
        }
    }

    @PostMapping("/create")
    public String create(@RequestParam String loaiSuCo,
                         @RequestParam String moTa,
                         @RequestParam(value = "fileImages", required = false) java.util.List<org.springframework.web.multipart.MultipartFile> fileImages,
                         @RequestParam(required = false) String mucDoUuTien,
                         RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();

            // Tự động lấy phòng từ hợp đồng đang hiệu lực
            HopDong activeContract = hopDongService.findActiveByKhachThueId(currentUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("Bạn không có hợp đồng đang hiệu lực"));

            MucDoUuTien mucDo = MucDoUuTien.TRUNG_BINH;
            if (mucDoUuTien != null && !mucDoUuTien.isEmpty()) {
                try {
                    mucDo = MucDoUuTien.valueOf(mucDoUuTien);
                } catch (IllegalArgumentException ignored) {
                }
            }

            java.util.List<String> hinhAnhPaths = new java.util.ArrayList<>();
            if (fileImages != null && !fileImages.isEmpty()) {
                for (org.springframework.web.multipart.MultipartFile fileImage : fileImages) {
                    if (fileImage != null && !fileImage.isEmpty()) {
                        String fileName = org.springframework.util.StringUtils.cleanPath(fileImage.getOriginalFilename());
                        fileName = System.currentTimeMillis() + "_" + fileName;
                        String uploadDir = "./uploads";
                        try {
                            com.nhatro.quanlynhatro.utils.FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
                            hinhAnhPaths.add("/uploads/" + fileName);
                        } catch (java.io.IOException e) {
                            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh lên: " + e.getMessage());
                            return "redirect:/tenant/su-co/create";
                        }
                    }
                }
            }
            
            String finalHinhAnhPath = hinhAnhPaths.isEmpty() ? null : String.join(",", hinhAnhPaths);

            YeuCauSuCo suCo = YeuCauSuCo.builder()
                    .phongTro(activeContract.getPhongTro())
                    .khachThue(currentUser)
                    .loaiSuCo(loaiSuCo)
                    .moTa(moTa)
                    .hinhAnh(finalHinhAnhPath)
                    .mucDoUuTien(mucDo)
                    .trangThai(TrangThaiSuCo.MOI)
                    .ngayTao(LocalDateTime.now())
                    .build();
            YeuCauSuCo saved = yeuCauSuCoService.save(suCo);

            // UC14/UC23: Gửi thông báo cho chủ trọ
            thongBaoService.notifyAllLandlords(
                    "Sự cố mới - Phòng " + activeContract.getPhongTro().getSoPhong(),
                    "Khách thuê " + currentUser.getHoTen() + " báo sự cố: " + loaiSuCo
                            + " (Mức độ: " + mucDo.name() + ")",
                    "/landlord/su-co/detail/" + saved.getTicketId());

            redirectAttributes.addFlashAttribute("successMessage", "Gửi báo sự cố thành công!");
            return "redirect:/tenant/su-co";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi báo sự cố thất bại: " + e.getMessage());
            return "redirect:/tenant/su-co/create";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            YeuCauSuCo suCo = yeuCauSuCoService.findById(id);

            // Kiểm tra quyền sở hữu
            if (!suCo.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem yêu cầu sự cố này");
                return "redirect:/tenant/su-co";
            }

            model.addAttribute("suCo", suCo);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/su-co/detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/su-co";
        }
    }
}
