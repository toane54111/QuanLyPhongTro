package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.HopDong;
import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.entity.PhuLucHopDong;
import com.nhatro.quanlynhatro.entity.YeuCauChamDut;
import com.nhatro.quanlynhatro.entity.YeuCauGiaHan;
import com.nhatro.quanlynhatro.enums.TrangThaiYeuCau;
import com.nhatro.quanlynhatro.service.HopDongService;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import com.nhatro.quanlynhatro.service.PhuLucHopDongService;
import com.nhatro.quanlynhatro.service.YeuCauChamDutService;
import com.nhatro.quanlynhatro.service.YeuCauGiaHanService;
import com.nhatro.quanlynhatro.service.DichVuService;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.enums.VaiTro;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tenant/hop-dong")
@RequiredArgsConstructor
public class TenantHopDongController {

    private final NguoiDungService nguoiDungService;
    private final HopDongService hopDongService;
    private final YeuCauGiaHanService yeuCauGiaHanService;
    private final YeuCauChamDutService yeuCauChamDutService;
    private final PhuLucHopDongService phuLucHopDongService;
    private final DichVuService dichVuService;
    private final NguoiDungRepository nguoiDungRepository;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public String list(Model model) {
        try {
            NguoiDung currentUser = getCurrentUser();
            List<HopDong> hopDongs = hopDongService.findByKhachThueId(currentUser.getUserId());
            model.addAttribute("hopDongs", hopDongs);
            model.addAttribute("nguoiDung", currentUser);

            // Map hopDongId → thông tin giá hiện tại
            Map<Long, Map<String, Object>> giaThueMap = new HashMap<>();
            for (HopDong hd : hopDongs) {
                giaThueMap.put(hd.getHopDongId(), phuLucHopDongService.getThongTinGiaThue(hd.getHopDongId()));
            }
            model.addAttribute("giaThueMap", giaThueMap);

            return "tenant/hop-dong/list";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            // Kiểm tra quyền sở hữu
            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem hợp đồng này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);

            long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), hopDong.getNgayKetThuc());
            model.addAttribute("remainingDays", remainingDays);

            // Fetch landlord (assume first one) and all services for the printable contract
            model.addAttribute("danhSachDichVu", dichVuService.findAll());
            List<NguoiDung> chuTros = nguoiDungRepository.findByVaiTro(VaiTro.CHU_TRO);
            if (!chuTros.isEmpty()) {
                model.addAttribute("chuTro", chuTros.get(0));
            }
            model.addAttribute("khachThue", currentUser);

            // Thông tin giá thuê hiện tại (có tính phụ lục)
            model.addAttribute("giaThueInfo", phuLucHopDongService.getThongTinGiaThue(hopDong.getHopDongId()));

            // Kiểm tra phụ lục đang chờ duyệt
            phuLucHopDongService.findByHopDongId(hopDong.getHopDongId()).stream()
                    .filter(pl -> pl.getTrangThai() == TrangThaiYeuCau.CHO_PHE_DUYET)
                    .findFirst()
                    .ifPresent(pl -> model.addAttribute("pendingPhuLuc", pl));

            return "tenant/hop-dong/detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @GetMapping("/yeu-cau-gia-han/{hopDongId}")
    public String showGiaHanForm(@PathVariable Long hopDongId, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/yeu-cau-gia-han";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/yeu-cau-gia-han/{hopDongId}")
    public String submitGiaHan(@PathVariable Long hopDongId,
                               @RequestParam Integer thoiGianGiaHan,
                               @RequestParam(required = false) String ghiChu,
                               RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            YeuCauGiaHan yeuCau = YeuCauGiaHan.builder()
                    .khachThue(currentUser)
                    .hopDong(hopDong)
                    .thoiGianGiaHan(thoiGianGiaHan)
                    .ghiChu(ghiChu)
                    .trangThai(TrangThaiYeuCau.CHO_PHE_DUYET)
                    .ngayTao(LocalDateTime.now())
                    .build();
            yeuCauGiaHanService.save(yeuCau);

            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu gia hạn thành công. Vui lòng chờ phê duyệt.");
            return "redirect:/tenant/hop-dong/detail/" + hopDongId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi yêu cầu thất bại: " + e.getMessage());
            return "redirect:/tenant/hop-dong/yeu-cau-gia-han/" + hopDongId;
        }
    }

    @GetMapping("/yeu-cau-cham-dut/{hopDongId}")
    public String showChamDutForm(@PathVariable Long hopDongId, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/yeu-cau-cham-dut";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/yeu-cau-cham-dut/{hopDongId}")
    public String submitChamDut(@PathVariable Long hopDongId,
                                @RequestParam String ngayDuKienTra,
                                @RequestParam(required = false) String lyDo,
                                RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            HopDong hopDong = hopDongService.findById(hopDongId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện yêu cầu này");
                return "redirect:/tenant/hop-dong";
            }

            YeuCauChamDut yeuCau = YeuCauChamDut.builder()
                    .khachThue(currentUser)
                    .hopDong(hopDong)
                    .ngayDuKienTra(LocalDate.parse(ngayDuKienTra))
                    .lyDo(lyDo)
                    .trangThai(TrangThaiYeuCau.CHO_PHE_DUYET)
                    .ngayTao(LocalDateTime.now())
                    .build();
            yeuCauChamDutService.save(yeuCau);

            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu chấm dứt hợp đồng thành công. Vui lòng chờ phê duyệt.");
            return "redirect:/tenant/hop-dong/detail/" + hopDongId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gửi yêu cầu thất bại: " + e.getMessage());
            return "redirect:/tenant/hop-dong/yeu-cau-cham-dut/" + hopDongId;
        }
    }

    // ==================== Phụ lục hợp đồng (gia hạn kèm giá mới) ====================

    @GetMapping("/phu-luc/{phuLucId}")
    public String showPhuLuc(@PathVariable Long phuLucId, Model model, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            PhuLucHopDong phuLuc = phuLucHopDongService.findById(phuLucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ lục hợp đồng"));

            HopDong hopDong = phuLuc.getHopDong();
            // Kiểm tra quyền: phụ lục phải thuộc hợp đồng của khách này
            if (!hopDong.getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xem phụ lục này");
                return "redirect:/tenant/hop-dong";
            }

            model.addAttribute("phuLuc", phuLuc);
            model.addAttribute("hopDong", hopDong);
            model.addAttribute("nguoiDung", currentUser);
            return "tenant/hop-dong/phu-luc-detail";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/phu-luc/approve/{phuLucId}")
    public String approvePhuLuc(@PathVariable Long phuLucId, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            PhuLucHopDong phuLuc = phuLucHopDongService.findById(phuLucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ lục hợp đồng"));

            if (!phuLuc.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này");
                return "redirect:/tenant/hop-dong";
            }

            phuLucHopDongService.approve(phuLucId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bạn đã đồng ý gia hạn hợp đồng với giá mới. Hợp đồng đã được cập nhật!");
            return "redirect:/tenant/hop-dong/detail/" + phuLuc.getHopDong().getHopDongId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }

    @PostMapping("/phu-luc/reject/{phuLucId}")
    public String rejectPhuLuc(@PathVariable Long phuLucId,
                                @RequestParam(required = false) String lyDo,
                                RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = getCurrentUser();
            PhuLucHopDong phuLuc = phuLucHopDongService.findById(phuLucId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ lục hợp đồng"));

            if (!phuLuc.getHopDong().getKhachThue().getUserId().equals(currentUser.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác này");
                return "redirect:/tenant/hop-dong";
            }

            phuLucHopDongService.reject(phuLucId, lyDo);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bạn đã từ chối đề nghị gia hạn. Hợp đồng hiện tại vẫn giữ nguyên.");
            return "redirect:/tenant/hop-dong/detail/" + phuLuc.getHopDong().getHopDongId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/tenant/hop-dong";
        }
    }
}
