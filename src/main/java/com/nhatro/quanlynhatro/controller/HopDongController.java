package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.*;
import com.nhatro.quanlynhatro.enums.TrangThaiHopDong;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/landlord/hop-dong")
@RequiredArgsConstructor
public class HopDongController {

    private final HopDongService hopDongService;
    private final PhongTroService phongTroService;
    private final KhachThueService khachThueService;
    private final PhuLucHopDongService phuLucHopDongService;
    private final ThongBaoService thongBaoService;
    private final DichVuService dichVuService;
    private final NguoiDungRepository nguoiDungRepository;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return nguoiDungRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping
    public String list(@RequestParam(required = false) TrangThaiHopDong trangThai,
                       Model model) {
        try {
            List<HopDong> danhSach = hopDongService.findAll(trangThai);
            model.addAttribute("danhSachHopDong", danhSach);
            model.addAttribute("trangThaiHopDongs", TrangThaiHopDong.values());
            model.addAttribute("selectedTrangThai", trangThai);

            // Map hopDongId → thông tin giá hiện tại
            Map<Long, Map<String, Object>> giaThueMap = new HashMap<>();
            for (HopDong hd : danhSach) {
                giaThueMap.put(hd.getHopDongId(), phuLucHopDongService.getThongTinGiaThue(hd.getHopDongId()));
            }
            model.addAttribute("giaThueMap", giaThueMap);
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

    /**
     * Bước 1: Từ form → hiển thị trang xác nhận trước khi tạo hợp đồng
     */
    @PostMapping("/confirm")
    public String showConfirm(@ModelAttribute HopDong hopDong,
                              @RequestParam Long phongId,
                              @RequestParam(required = false) Long khachThueId,
                              @RequestParam(required = false) String newKhachHoTen,
                              @RequestParam(required = false) String newKhachEmail,
                              @RequestParam(required = false) String newKhachSdt,
                              @RequestParam(required = false) String newKhachCccd,
                              @RequestParam(defaultValue = "false") boolean taoKhachMoi,
                              Model model, RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin phòng
            PhongTro phongTro = phongTroService.findById(phongId);
            // Gán giá thuê từ phòng
            hopDong.setGiaThue(phongTro.getGiaThue());

            // Lấy thông tin khách thuê (hoặc chuẩn bị khách mới)
            NguoiDung khachThue = null;
            if (taoKhachMoi) {
                // Tạo object tạm để hiển thị, chưa lưu DB
                khachThue = new NguoiDung();
                khachThue.setHoTen(newKhachHoTen);
                khachThue.setEmail(newKhachEmail);
                khachThue.setSdt(newKhachSdt);
                khachThue.setCccd(newKhachCccd);
            } else {
                if (khachThueId == null) {
                    throw new RuntimeException("Vui lòng chọn khách thuê hoặc tạo khách thuê mới");
                }
                khachThue = nguoiDungRepository.findById(khachThueId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách thuê"));
            }

            // Lấy thông tin chủ trọ (user đang đăng nhập)
            NguoiDung chuTro = getCurrentUser();

            // Lấy danh sách dịch vụ
            List<DichVu> danhSachDichVu = dichVuService.findAll();

            model.addAttribute("hopDong", hopDong);
            model.addAttribute("phongTro", phongTro);
            model.addAttribute("khachThue", khachThue);
            model.addAttribute("chuTro", chuTro);
            model.addAttribute("danhSachDichVu", danhSachDichVu);
            model.addAttribute("phongId", phongId);
            model.addAttribute("khachThueId", khachThueId);
            model.addAttribute("taoKhachMoi", taoKhachMoi);
            model.addAttribute("newKhachHoTen", newKhachHoTen);
            model.addAttribute("newKhachEmail", newKhachEmail);
            model.addAttribute("newKhachSdt", newKhachSdt);
            model.addAttribute("newKhachCccd", newKhachCccd);

            return "landlord/hop-dong/confirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/landlord/hop-dong/create";
        }
    }

    /**
     * Bước 2: Xác nhận → thực sự tạo hợp đồng
     */
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
            model.addAttribute("phuLucList", phuLucHopDongService.findByHopDongId(id));
            model.addAttribute("giaThueInfo", phuLucHopDongService.getThongTinGiaThue(id));
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
            model.addAttribute("hasPendingPhuLuc",
                    phuLucHopDongService.hasPendingPhuLuc(hopDong.getHopDongId()));
            model.addAttribute("giaThueInfo", phuLucHopDongService.getThongTinGiaThue(id));
            return "landlord/hop-dong/extend";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hợp đồng: " + e.getMessage());
            return "redirect:/landlord/hop-dong";
        }
    }

    /**
     * Gia hạn nhanh (giữ giá cũ) — cập nhật ngay, gửi thông báo cho khách
     */
    @PostMapping("/extend/{id}")
    public String extend(@PathVariable Long id,
                         @RequestParam String ngayKetThucMoi,
                         RedirectAttributes redirectAttributes) {
        try {
            java.time.LocalDate ngayMoi = java.time.LocalDate.parse(ngayKetThucMoi);
            HopDong hopDong = hopDongService.extend(id, ngayMoi);

            // Gửi thông báo cho khách thuê
            String soPhong = hopDong.getPhongTro().getSoPhong();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            thongBaoService.notifyUser(hopDong.getKhachThue().getUserId(),
                    "Hợp đồng phòng " + soPhong + " đã được gia hạn",
                    "Hợp đồng phòng " + soPhong + " đã được gia hạn thành công đến ngày "
                            + ngayMoi.format(fmt) + " với giá thuê giữ nguyên."
                            + " Nếu không đồng ý, vui lòng thông báo cho chủ trọ trong vòng 15 ngày.",
                    "/tenant/hop-dong/detail/" + hopDong.getHopDongId());

            redirectAttributes.addFlashAttribute("success", "Gia hạn hợp đồng thành công (giá cũ)!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gia hạn hợp đồng: " + e.getMessage());
        }
        return "redirect:/landlord/hop-dong";
    }

    /**
     * Gia hạn kèm phụ lục (giá mới) — gửi cho khách duyệt
     */
    @PostMapping("/extend-phu-luc/{id}")
    public String extendWithPhuLuc(@PathVariable Long id,
                                    @RequestParam String ngayKetThucMoi,
                                    @RequestParam BigDecimal giaThueMoi,
                                    @RequestParam(required = false) String ghiChu,
                                    RedirectAttributes redirectAttributes) {
        try {
            java.time.LocalDate ngayMoi = java.time.LocalDate.parse(ngayKetThucMoi);
            phuLucHopDongService.create(id, giaThueMoi, ngayMoi, ghiChu);
            redirectAttributes.addFlashAttribute("success",
                    "Đã gửi đề nghị gia hạn kèm phụ lục cho khách thuê. Vui lòng chờ khách phản hồi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/landlord/hop-dong/detail/" + id;
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
