package com.nhatro.quanlynhatro.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void migrate() {
        try {
            // Cho phép hoa_don_id nullable trong giao_dich (cho giao dịch tiền cọc / hoàn cọc)
            jdbcTemplate.execute("ALTER TABLE giao_dich MODIFY COLUMN hoa_don_id BIGINT NULL");
        } catch (Exception e) {
            // Bỏ qua nếu table chưa tồn tại hoặc đã nullable
        }

        try {
            // Fix dữ liệu cũ: phụ lục đã duyệt có ngayHieuLuc sai (= ngày duyệt thay vì ngày KT cũ).
            // Cập nhật ngayHieuLuc = ngayKetThucMoi - 1 tháng (xấp xỉ) cho các record đã duyệt
            // mà ngayHieuLuc nằm trước ngayKetThucMoi quá xa (tức là bị set = ngày duyệt).
            // Logic: nếu ngayHieuLuc < ngayKetThucMoi - 60 ngày → set lại = ngayKetThucMoi - 30 ngày
            jdbcTemplate.execute(
                "UPDATE phu_luc_hop_dong SET ngay_hieu_luc = DATE_SUB(ngay_ket_thuc_moi, INTERVAL 1 MONTH) " +
                "WHERE trang_thai = 'DA_PHE_DUYET' " +
                "AND ngay_hieu_luc IS NOT NULL " +
                "AND DATEDIFF(ngay_ket_thuc_moi, ngay_hieu_luc) > 60"
            );
        } catch (Exception e) {
            // Bỏ qua nếu table chưa tồn tại
        }
    }
}
