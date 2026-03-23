package com.nhatro.quanlynhatro.config;

import com.nhatro.quanlynhatro.entity.ThongBao;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return nguoiDungRepository.findByEmail(auth.getName())
                    .map(u -> u.getUserId())
                    .orElse(null);
        }
        return null;
    }

    @ModelAttribute("soThongBaoChuaDoc")
    public long soThongBaoChuaDoc() {
        try {
            Long userId = getCurrentUserId();
            return userId != null ? thongBaoService.countUnread(userId) : 0;
        } catch (Exception ignored) {
            return 0;
        }
    }

    @ModelAttribute("danhSachThongBao")
    public List<ThongBao> danhSachThongBao() {
        try {
            Long userId = getCurrentUserId();
            return userId != null ? thongBaoService.findUnreadByUserId(userId) : Collections.emptyList();
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}
