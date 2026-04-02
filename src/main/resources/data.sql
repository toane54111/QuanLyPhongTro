-- ============================================================
-- QUẢN LÝ NHÀ TRỌ - DỮ LIỆU MẪU (SEED DATA)
-- Chỉ insert khi bảng rỗng (sử dụng INSERT IGNORE)
-- ============================================================

-- ============================================================
-- 1. TÀI KHOẢN MẪU
-- Mật khẩu mã hóa BCrypt cho "123456"
-- ============================================================
INSERT IGNORE INTO nguoi_dung (user_id, ho_ten, email, mat_khau, sdt, cccd, vai_tro, trang_thai)
VALUES
    (1, 'Nguyễn Văn Chủ', 'chutro@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0901234567', '012345678901', 'CHU_TRO', 'HOAT_DONG'),
    (2, 'Trần Thị Khách', 't@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0912345678', '012345678902', 'KHACH_THUE', 'HOAT_DONG');

-- ============================================================
-- 2. KHU TRỌ MẪU
-- ============================================================
INSERT IGNORE INTO khu_tro (khu_tro_id, ten_khu, dia_chi, so_tang, mo_ta)
VALUES
    (1, 'Khu A', '123 Đường Nguyễn Văn Cừ, Quận 5, TP.HCM', 3, 'Khu trọ chính, gần trường đại học');

-- ============================================================
-- 3. PHÒNG TRỌ MẪU
-- ============================================================
INSERT IGNORE INTO phong_tro (phong_id, khu_tro_id, so_phong, tang, dien_tich, gia_thue, trang_thai, mo_ta)
VALUES
    (1, 1, '101', 1, 20.0, 3000000, 'DA_THUE', 'Phòng đơn, có ban công'),
    (2, 1, '102', 1, 25.0, 3500000, 'TRONG', 'Phòng đôi, có toilet riêng'),
    (3, 1, '201', 2, 20.0, 3000000, 'TRONG', 'Phòng đơn tầng 2'),
    (4, 1, '202', 2, 30.0, 4000000, 'TRONG', 'Phòng rộng, có bếp riêng');

-- ============================================================
-- 4. DỊCH VỤ MẪU
-- ============================================================
INSERT IGNORE INTO dich_vu (dich_vu_id, ten_dv, don_gia, don_vi_tinh)
VALUES
    (1, 'Điện', 3500.00, 'kWh'),
    (2, 'Nước', 15000.00, 'm3'),
    (3, 'Internet', 100000.00, 'tháng'),
    (4, 'Rác', 20000.00, 'tháng');
