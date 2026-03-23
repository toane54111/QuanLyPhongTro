package com.nhatro.quanlynhatro.service;

import com.nhatro.quanlynhatro.entity.NguoiDung;
import com.nhatro.quanlynhatro.entity.ThongBao;
import com.nhatro.quanlynhatro.enums.VaiTro;
import com.nhatro.quanlynhatro.repository.NguoiDungRepository;
import com.nhatro.quanlynhatro.repository.ThongBaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public List<ThongBao> findByUserId(Long userId) {
        return thongBaoRepository.findByNguoiNhan_UserIdOrderByNgayTaoDesc(userId);
    }

    public List<ThongBao> findUnreadByUserId(Long userId) {
        return thongBaoRepository.findByNguoiNhan_UserIdAndDaDocFalseOrderByNgayTaoDesc(userId);
    }

    public long countUnread(Long userId) {
        return thongBaoRepository.countByNguoiNhan_UserIdAndDaDocFalse(userId);
    }

    @Transactional
    public void markAsRead(Long thongBaoId) {
        thongBaoRepository.findById(thongBaoId).ifPresent(tb -> {
            tb.setDaDoc(true);
            thongBaoRepository.save(tb);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<ThongBao> unreads = thongBaoRepository.findByNguoiNhan_UserIdAndDaDocFalseOrderByNgayTaoDesc(userId);
        unreads.forEach(tb -> tb.setDaDoc(true));
        thongBaoRepository.saveAll(unreads);
    }

    /**
     * Gửi thông báo cho tất cả chủ trọ (UC14 - extend từ UC13, UC23)
     */
    @Transactional
    public void notifyAllLandlords(String tieuDe, String noiDung, String link) {
        List<NguoiDung> landlords = nguoiDungRepository.findByVaiTro(VaiTro.CHU_TRO);
        for (NguoiDung landlord : landlords) {
            ThongBao tb = ThongBao.builder()
                    .nguoiNhan(landlord)
                    .tieuDe(tieuDe)
                    .noiDung(noiDung)
                    .link(link)
                    .daDoc(false)
                    .ngayTao(LocalDateTime.now())
                    .build();
            thongBaoRepository.save(tb);
        }
    }

    /**
     * Gửi thông báo cho một người dùng cụ thể
     */
    @Transactional
    public void notifyUser(Long userId, String tieuDe, String noiDung, String link) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        ThongBao tb = ThongBao.builder()
                .nguoiNhan(user)
                .tieuDe(tieuDe)
                .noiDung(noiDung)
                .link(link)
                .daDoc(false)
                .ngayTao(LocalDateTime.now())
                .build();
        thongBaoRepository.save(tb);
    }
}
