package com.nhatro.quanlynhatro.controller;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.service.NguoiDungService;
import com.nhatro.quanlynhatro.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungService nguoiDungService;

    private NguoiDung getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return nguoiDungService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/mark-read/{id}")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        thongBaoService.markAsRead(id);
        return "redirect:/";
    }

    @PostMapping("/mark-all-read")
    public String markAllAsRead(RedirectAttributes redirectAttributes) {
        NguoiDung user = getCurrentUser();
        thongBaoService.markAllAsRead(user.getUserId());
        return "redirect:/";
    }
}
