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
    }
}
