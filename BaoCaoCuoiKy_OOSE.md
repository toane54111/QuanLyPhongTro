# BÁO CÁO CUỐI KỲ

# ĐỒ ÁN MÔN: PHÂN TÍCH VÀ THIẾT KẾ HƯỚNG ĐỐI TƯỢNG

---

# ĐỀ TÀI: HỆ THỐNG WEBSITE QUẢN LÝ PHÒNG TRỌ

---

**Nhóm thực hiện:** Nhóm 3 thành viên

**Thành viên:**
- Trần Quang Toản
- Trần Văn Tưởng
- Ô Duy Hoàng Thiện

**Giảng viên hướng dẫn:** [Tên Giảng viên]

**Thời gian thực hiện:** 18/03/2026 - 28/03/2026 (11 ngày)

---

# LỜI MỞ ĐẦU

## 1. Lý do chọn đề tài

Trong bối cảnh cuộc sống ngày càng phát triển, đặc biệt tại các thành phố lớn như Hồ Chí Minh, Hà Nội, Đà Nẵng..., nhu cầu thuê trọ ngày càng tăng cao. Theo thống kê, có hàng triệu người lao động và sinh viên đang sinh sống tại các khu trọ, chung cư mini trên cả nước. Điều này tạo ra một nhu cầu cấp thiết về việc quản lý các khu trọ một cách hiệu quả và chuyên nghiệp.

Tuy nhiên, thực trạng quản lý phòng trọ tại Việt Nam hiện nay vẫn còn nhiều bất cập. Đa số các chủ trọ vẫn sử dụng phương pháp quản lý thủ công bằng sổ sách, bảng tính Excel hoặc ghi chép rời rạc. Phương pháp này không chỉ tốn nhiều thời gian và công sức mà còn dễ dẫn đến sai sót, nhầm lẫn trong việc tính toán tiền điện, tiền nước, quản lý hợp đồng và công nợ.

Trước những thách thức đó, việc số hóa quy trình quản lý phòng trọ trở thành một xu hướng tất yếu. Một hệ thống quản lý hiện đại sẽ giúp:

- **Đối với chủ trọ:** Tiết kiệm thời gian trong việc quản lý, tự động hóa việc tính toán chi phí, theo dõi công nợ một cách chính xác, và nâng cao chất lượng dịch vụ cho thuê.
- **Đối với người thuê trọ:** Được cung cấp thông tin minh bạch về chi phí, có thể theo dõi hóa đơn mọi lúc mọi nơi, và dễ dàng gửi yêu cầu hỗ trợ khi cần thiết.
- **Đối với thị trường:** Góp phần chuẩn hóa quy trình quản lý, tạo nền tảng cho sự phát triển bền vững của ngành cho thuê bất động sản.

## 2. Mục tiêu của đề tài

Đồ án này được thực hiện với mục tiêu xây dựng một **Hệ thống Website Quản lý Phòng Trọ** hoàn chỉnh, đáp ứng các yêu cầu cơ bản trong việc quản lý khu trọ, từ quản lý phòng ốc, hợp đồng thuê, tính toán chi phí điện nước đến thu chi tài chính và xử lý sự cố.

## 3. Phạm vi nghiên cứu

- **Đối tượng phục vụ:** Chủ trọ (Landlord) và Người thuê (Tenant) tại các khu trọ, chung cư mini.
- **Mô hình hoạt động:** Single-tenant - một chủ trọ quản lý một hoặc nhiều khu trọ.
- **Ngôn ngữ lập trình:** Java (Spring Boot) với giao diện Thymeleaf.
- **Cơ sở dữ liệu:** MySQL.

---

# CHƯƠNG 1: KHẢO SÁT VÀ PHÂN TÍCH YÊU CẦU

## 1.1 Khảo sát thực trạng quản lý phòng trọ

### 1.1.1 Thực trạng quản lý thủ công hiện nay

Qua quá trình khảo sát thực tế tại nhiều khu trọ trên địa bàn thành phố, nhóm đã nhận diện được những vấn đề chính sau:

#### Nỗi khổ của Chủ trọ

| Vấn đề | Mô tả chi tiết | Hệ quả |
|---------|----------------|--------|
| **Quản lý bằng sổ sách/Excel** | Ghi chép thông tin khách thuê, số điện/nước, tiền phòng bằng sổ tay hoặc bảng tính Excel | Sổ sách dễ rách, thất lạc; Excel dễ nhầm lẫn dòng/cột, format |
| **Bài toán điện/nước** | Mỗi tháng phải đi từng phòng ghi số điện/nước, trừ số cũ, nhân đơn giá (có thể tính giá bậc thang) | Tốn hàng giờ đồng hồ, rất dễ tính sai, dẫn đến tranh chấp với khách |
| **Quản lý hợp đồng** | Theo dõi ngày bắt đầu/kết thúc, tiền cọc, thông tin CCCD | Khó nhớ phòng nào sắp hết hạn, khách nào chưa đóng tiền |
| **Lưu trữ tài liệu** | Lưu trữ hình ảnh CCCD, hợp đồng giấy | Rất rườm rà, chiếm nhiều không gian, khó tìm kiếm |
| **Thông tin liên lạc** | Nhắn tin Zalo/SMS cho từng khách | Tin nhắn dễ bị trôi, chủ trọ dễ quên không gọi thợ sửa chữa |

#### Nỗi khổ của Người thuê trọ

| Vấn đề | Mô tả chi tiết | Hệ quả |
|---------|----------------|--------|
| **Thiếu minh bạch** | Nhận giấy báo tiền phòng viết tay, mập mờ | Đôi khi nghi ngờ bị tính sai số điện/nước nhưng không có dữ liệu cũ để đối chứng |
| **Báo cáo sự cố** | Nhắn tin Zalo cho chủ trọ khi có hỏng hóc | Tin nhắn hay bị trôi hoặc chủ trọ quên mất |
| **Theo dõi thanh toán** | Không biết mình đã đóng đủ chưa, còn nợ bao nhiêu | Gây bức xúc, mất lòng tin |

### 1.1.2 Sơ đồ bài toán tổng quan

```
┌─────────────────────────────────────────────────────────────────────┐
│                    HỆ THỐNG QUẢN LÝ PHÒNG TRỌ                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐                              ┌─────────────┐     │
│  │  CHỦ TRỌ    │                              │ NGƯỜI THUÊ  │     │
│  │ (Landlord)  │                              │  (Tenant)   │     │
│  └──────┬──────┘                              └──────┬──────┘     │
│         │                                            │             │
│         ▼                                            ▼             │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    CÁC CHỨC NĂNG CHÍNH                      │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │ • Quản lý khu trọ & phòng trọ                             │   │
│  │ • Quản lý khách thuê & hợp đồng                            │   │
│  │ • Ghi chỉ số điện/nước hàng tháng                          │   │
│  │ • Tự động tính tiền & lập hóa đơn                          │   │
│  │ • Quản lý thu chi & công nợ                                │   │
│  │ • Xử lý yêu cầu sự cố                                      │   │
│  │ • Thanh toán online                                        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              ▼                                     │
│                    ┌─────────────────┐                             │
│                    │     MySQL        │                             │
│                    │  Cơ sở dữ liệu  │                             │
│                    └─────────────────┘                             │
└─────────────────────────────────────────────────────────────────────┘
```

## 1.2 Xác định tác nhân hệ thống

Dựa trên thực trạng khảo sát, hệ thống xác định **2 tác nhân chính** và **2 tác nhân bên ngoài**:

### 1.2.1 Tác nhân chính

| STT | Tên tác nhân | Vai trò | Mô tả |
|-----|--------------|---------|-------|
| 1 | **Chủ trọ (Landlord)** | Quản lý | Người tạo lập khu trọ, quản lý phòng, thêm khách, chốt điện nước và thu tiền. Có toàn quyền quản lý hệ thống. |
| 2 | **Khách thuê (Tenant)** | Sử dụng | Người đăng nhập để xem thông tin hợp đồng, xem hóa đơn hàng tháng, thanh toán và báo cáo sự cố. |

### 1.2.2 Tác nhân bên ngoài (External Actors)

| STT | Tên tác nhân | Vai trò | Mô tả |
|-----|--------------|---------|-------|
| 1 | **Cổng thanh toán** | Xử lý thanh toán | Tương tác với hệ thống để xử lý thanh toán online (VNPay, MoMo, chuyển khoản ngân hàng). |
| 2 | **Dịch vụ thông báo** | Gửi thông báo | Gửi email/SMS thông báo hóa đơn cho khách thuê. |

## 1.3 Yêu cầu chức năng (Functional Requirements)

### 1.3.1 Nhóm yêu cầu dùng chung

| STT | Mã UC | Tên Use Case | Mô tả |
|-----|-------|--------------|-------|
| 1 | UC01 | Đăng nhập | Xác thực người dùng vào hệ thống bằng tài khoản và mật khẩu |
| 2 | UC02 | Quản lý tài khoản | Xem, cập nhật thông tin cá nhân và đổi mật khẩu |

### 1.3.2 Nhóm yêu cầu của Chủ trọ

| STT | Mã UC | Tên Use Case | Mô tả |
|-----|-------|--------------|-------|
| 3 | UC03 | Quản lý khu trọ | Thêm, sửa, xóa thông tin cấu hình chung của khu trọ |
| 4 | UC04 | Quản lý phòng trọ | Thêm, sửa, xóa thông tin phòng, cập nhật trạng thái phòng |
| 5 | UC05 | Cấu hình dịch vụ & đơn giá | Cài đặt giá điện, giá nước, wifi, phí rác... |
| 6 | UC06 | Quản lý khách thuê | Lưu trữ, xem, cập nhật thông tin cá nhân và CCCD của khách thuê |
| 7 | UC07 | Lập hợp đồng thuê phòng | Tạo hợp đồng mới cho khách, lưu tiền cọc |
| 8 | UC08 | Kiểm tra phòng trống | (UC nội bộ) Kiểm tra trạng thái phòng trước khi lập hợp đồng |
| 9 | UC09 | Gia hạn hợp đồng | Kéo dài thời hạn hợp đồng |
| 10 | UC10 | Phê duyệt chấm dứt hợp đồng | Tiếp nhận và phê duyệt yêu cầu trả phòng |
| 11 | UC11 | Thực hiện chấm dứt hợp đồng | (UC nội bộ) Tính tiền cọc hoàn trả, chuyển phòng về "Trống" |
| 12 | UC12 | Ghi chỉ số điện/nước | Nhập số điện, nước mới của từng phòng vào cuối tháng |
| 13 | UC13 | Lập hóa đơn tính tiền | Tự động tính toán tổng tiền dựa trên chỉ số và đơn giá |
| 14 | UC14 | Gửi thông báo (Email/SMS) | Gửi thông báo hóa đơn cho khách thuê |
| 15 | UC15 | Xác nhận thanh toán | Chủ trọ xác nhận khi khách đóng tiền mặt hoặc chuyển khoản |
| 16 | UC16 | Xem thống kê doanh thu | Xem báo cáo thu/chi, công nợ theo tháng/năm |
| 17 | UC17 | Quản lý yêu cầu sự cố | Xem và cập nhật tiến độ xử lý sự cố |

### 1.3.3 Nhóm yêu cầu của Khách thuê

| STT | Mã UC | Tên Use Case | Mô tả |
|-----|-------|--------------|-------|
| 18 | UC18 | Xem thông tin hợp đồng | Xem lại thời hạn, tiền cọc và các điều khoản phòng đang ở |
| 19 | UC19 | Đề nghị gia hạn hợp đồng | Gửi yêu cầu xin ở thêm khi sắp hết hạn |
| 20 | UC20 | Yêu cầu chấm dứt hợp đồng | Báo trước cho chủ trọ ngày sẽ dọn đi |
| 21 | UC21 | Xem hóa đơn hàng tháng | Xem chi tiết tiền phòng, điện, nước |
| 22 | UC22 | Thanh toán hóa đơn online | Thanh toán trực tiếp qua cổng thanh toán |
| 23 | UC23 | Báo cáo sự cố | Tạo ticket báo cáo hỏng hóc kèm hình ảnh |

## 1.4 Yêu cầu phi chức năng (Non-Functional Requirements)

### 1.4.1 Yêu cầu về tính khả dụng (Usability)

| STT | Yêu cầu | Mô tả chi tiết |
|-----|---------|----------------|
| 1 | **Giao diện thân thiện** | Website phải dễ sử dụng, trực quan, phù hợp với người dùng không có chuyên môn về công nghệ |
| 2 | **Responsive Design** | Website phải hiển thị tốt trên mọi thiết bị (desktop, tablet, mobile) |
| 3 | **Mobile-first** | Đặc biệt tối ưu cho điện thoại di động vì chủ trọ thường cầm điện thoại đi ghi số điện |
| 4 | **Hỗ trợ đa ngôn ngữ** | Hỗ trợ tiếng Việt là ngôn ngữ chính |

### 1.4.2 Yêu cầu về tính bảo mật (Security)

| STT | Yêu cầu | Mô tả chi tiết |
|-----|---------|----------------|
| 1 | **Mã hóa dữ liệu** | Dữ liệu CCCD và hợp đồng của khách thuê phải được bảo mật |
| 2 | **HTTPS** | Mã hóa đường truyền bằng SSL/TLS |
| 3 | **Xác thực & Phân quyền** | Phân biệt rõ vai trò Landlord và Tenant |
| 4 | **Bảo vệ dữ liệu** | Backup định kỳ, ngăn chặn truy cập trái phép |

### 1.4.3 Yêu cầu về tính chính xác (Accuracy)

| STT | Yêu cầu | Mô tả chi tiết |
|-----|---------|----------------|
| 1 | **Tính tiền chính xác** | Thuật toán tính tiền phòng và điện nước phải đảm bảo chính xác 100% |
| 2 | **Làm tròn số** | Áp dụng đúng quy tắc làm tròn để không xảy ra tranh chấp |
| 3 | **Validation dữ liệu** | Kiểm tra tính hợp lệ của dữ liệu đầu vào |

### 1.4.4 Yêu cầu về hiệu năng (Performance)

| STT | Yêu cầu | Mô tả chi tiết |
|-----|---------|----------------|
| 1 | **Tốc độ phản hồi** | Thời gian phản hồi dưới 2 giây cho các thao tác thông thường |
| 2 | **Đồng thời** | Hỗ trợ nhiều người dùng truy cập cùng lúc |
| 3 | **Tải trang** | Trang web phải load nhanh, không giật lag |

## 1.5 Mô tả bài toán tổng quan

**Bài toán:** Xây dựng hệ thống website quản lý nhà trọ nhằm số hóa quy trình vận hành của các khu trọ, chung cư mini.

**Giải pháp đề xuất:** Hệ thống Website Quản lý Nhà Trọ được xây dựng nhằm số hóa quy trình vận hành của các khu trọ, chung cư mini. Hệ thống cung cấp giải pháp toàn diện giúp Chủ trọ quản lý thông tin khách hàng, tự động hóa quy trình tính toán chi phí (điện, nước, dịch vụ) và theo dõi tình trạng thanh toán một cách chính xác. Đồng thời, hệ thống cung cấp cổng thông tin minh bạch cho Người thuê trọ để theo dõi hóa đơn và tương tác trực tiếp với quản lý khi có sự cố phát sinh, thay thế hoàn toàn phương thức quản lý bằng sổ sách truyền thống.

---

# CHƯƠNG 2: MÔ HÌNH HÓA HỆ THỐNG (MỨC PHÂN TÍCH)

## 2.1 Biểu đồ Use Case tổng quát

### 2.1.1 Sơ đồ Use Case Diagram

```
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
    BackgroundColor LightCyan
    BorderColor DarkCyan
    ArrowColor DimGray
}
skinparam actor {
    BackgroundColor Moccasin
    BorderColor SaddleBrown
}
skinparam package {
    BackgroundColor WhiteSmoke
    BorderColor Silver
}

actor "Chủ trọ\n(Landlord)" as landlord
actor "Khách thuê\n(Tenant)" as tenant
actor "Cổng thanh toán\n(VNPay/MoMo)" as payment_gateway
actor "Dịch vụ thông báo\n(Email/SMS)" as notify_service

rectangle "Hệ thống Quản lý Nhà Trọ" {

  package "Dùng chung" {
    usecase "Đăng nhập" as UC_Login
    usecase "Quản lý tài khoản" as UC_Account
  }

  package "Phân hệ Chủ trọ" {

    package "Quản lý cơ sở vật chất" {
      usecase "Quản lý khu trọ" as UC_ManageAreas
      usecase "Quản lý phòng trọ" as UC_ManageRooms
      usecase "Cấu hình dịch vụ\n& đơn giá" as UC_ConfigServices
    }

    package "Quản lý Hợp đồng & Khách thuê" {
      usecase "Quản lý khách thuê" as UC_ManageTenants
      usecase "Lập hợp đồng\nthuê phòng" as UC_CreateContract
      usecase "Kiểm tra phòng trống" as UC_CheckRoom
      usecase "Gia hạn hợp đồng" as UC_ExtendContract
      usecase "Phê duyệt chấm dứt\nhợp đồng" as UC_ApproveEndContract
      usecase "Thực hiện chấm dứt\nhợp đồng" as UC_EndContract
    }

    package "Quản lý Tài chính" {
      usecase "Ghi chỉ số\nđiện nước" as UC_RecordMetrics
      usecase "Lập hóa đơn\ntính tiền" as UC_CreateInvoice
      usecase "Gửi thông báo\n(Email/SMS)" as UC_Notify
      usecase "Xác nhận thanh toán" as UC_RecordPayment
      usecase "Xem thống kê\ndoanh thu" as UC_ViewStats
    }

    package "Quản lý Vận hành" {
      usecase "Quản lý yêu cầu\nsự cố" as UC_ManageTickets
    }
  }

  package "Phân hệ Khách thuê" {

    package "Hợp đồng (Self-service)" {
      usecase "Xem thông tin\nhợp đồng" as UC_ViewContract
      usecase "Đề nghị gia hạn\nhợp đồng" as UC_ProposeExtend
      usecase "Yêu cầu chấm dứt\nhợp đồng" as UC_RequestEnd
    }

    package "Tài chính & Vận hành" {
      usecase "Xem hóa đơn\nhàng tháng" as UC_ViewInvoice
      usecase "Thanh toán hóa đơn" as UC_PayInvoice
      usecase "Báo cáo sự cố" as UC_ReportTicket
    }
  }
}

UC_CreateContract ..> UC_CheckRoom : <<include>>
UC_ApproveEndContract ..> UC_EndContract : <<include>>
UC_CreateInvoice ..> UC_Notify : <<extend>>

landlord -- UC_Login
landlord -- UC_Account
landlord -- UC_ManageAreas
landlord -- UC_ManageRooms
landlord -- UC_ConfigServices
landlord -- UC_ManageTenants
landlord -- UC_CreateContract
landlord -- UC_ExtendContract
landlord -- UC_ApproveEndContract
landlord -- UC_RecordMetrics
landlord -- UC_CreateInvoice
landlord -- UC_RecordPayment
landlord -- UC_ViewStats
landlord -- UC_ManageTickets

tenant -- UC_Login
tenant -- UC_Account
tenant -- UC_ViewContract
tenant -- UC_ProposeExtend
tenant -- UC_RequestEnd
tenant -- UC_ViewInvoice
tenant -- UC_PayInvoice
tenant -- UC_ReportTicket

UC_PayInvoice -- payment_gateway
UC_Notify -- notify_service
@enduml
```

### 2.1.2 Bảng tổng hợp 23 Use Case

| STT | Mã UC | Tên Use Case | Tác nhân | Package |
|-----|-------|--------------|----------|---------|
| 1 | UC01 | Đăng nhập | Landlord, Tenant | Dùng chung |
| 2 | UC02 | Quản lý tài khoản | Landlord, Tenant | Dùng chung |
| 3 | UC03 | Quản lý khu trọ | Landlord | Quản lý CSVC |
| 4 | UC04 | Quản lý phòng trọ | Landlord | Quản lý CSVC |
| 5 | UC05 | Cấu hình dịch vụ & đơn giá | Landlord | Quản lý CSVC |
| 6 | UC06 | Quản lý khách thuê | Landlord | Hợp đồng & KT |
| 7 | UC07 | Lập hợp đồng thuê phòng | Landlord | Hợp đồng & KT |
| 8 | UC08 | Kiểm tra phòng trống | (Nội bộ) | Hợp đồng & KT |
| 9 | UC09 | Gia hạn hợp đồng | Landlord | Hợp đồng & KT |
| 10 | UC10 | Phê duyệt chấm dứt HĐ | Landlord | Hợp đồng & KT |
| 11 | UC11 | Thực hiện chấm dứt HĐ | (Nội bộ) | Hợp đồng & KT |
| 12 | UC12 | Ghi chỉ số điện/nước | Landlord | Tài chính |
| 13 | UC13 | Lập hóa đơn tính tiền | Landlord | Tài chính |
| 14 | UC14 | Gửi thông báo (Email/SMS) | External | Tài chính |
| 15 | UC15 | Xác nhận thanh toán | Landlord | Tài chính |
| 16 | UC16 | Xem thống kê doanh thu | Landlord | Tài chính |
| 17 | UC17 | Quản lý yêu cầu sự cố | Landlord | Vận hành |
| 18 | UC18 | Xem thông tin hợp đồng | Tenant | Hợp đồng |
| 19 | UC19 | Đề nghị gia hạn HĐ | Tenant | Hợp đồng |
| 20 | UC20 | Yêu cầu chấm dứt HĐ | Tenant | Hợp đồng |
| 21 | UC21 | Xem hóa đơn hàng tháng | Tenant | Tài chính |
| 22 | UC22 | Thanh toán hóa đơn online | Tenant | Tài chính |
| 23 | UC23 | Báo cáo sự cố | Tenant | Vận hành |

### 2.1.3 Mối quan hệ giữa các Use Case

| Loại quan hệ | Use Case 1 | Use Case 2 | Ý nghĩa |
|--------------|------------|------------|---------|
| <<include>> | UC07 - Lập hợp đồng | UC08 - Kiểm tra phòng trống | Bắt buộc kiểm tra phòng trống trước khi lập HĐ |
| <<include>> | UC10 - Phê duyệt chấm dứt | UC11 - Thực hiện chấm dứt | Tự động thực hiện tính cọc khi duyệt chấm dứt |
| <<extend>> | UC13 - Lập hóa đơn | UC14 - Gửi thông báo | Tùy chọn gửi thông báo sau khi tạo hóa đơn |

## 2.2 Đặc tả chi tiết Use Case cốt lõi

### 2.2.1 UC07: Lập hợp đồng thuê phòng

| Thuộc tính | Nội dung |
|------------|----------|
| **Use Case ID** | UC07 |
| **Use Case Name** | Lập hợp đồng thuê phòng |
| **Actor(s)** | Chủ trọ (Landlord) |
| **Mô tả** | Tạo hợp đồng thuê mới cho khách, bao gồm thông tin phòng, thời hạn, giá thuê, tiền cọc. Bắt buộc kiểm tra phòng trống trước khi tạo. |
| **Priority** | Cao |
| **Ghi chú** | Quan hệ <<include>> với UC08 (Kiểm tra phòng trống) là bắt buộc |

#### Tiền điều kiện (Pre-conditions)
- Chủ trọ đã đăng nhập thành công vào hệ thống.
- Đã có thông tin khách thuê trong hệ thống (hoặc cần thêm mới).
- Có ít nhất một phòng trống trong hệ thống.

#### Hậu điều kiện (Post-conditions)
- Hợp đồng mới được tạo với trạng thái "Đang hiệu lực".
- Phòng được chuyển trạng thái từ "Trống" sang "Đã thuê".
- Tiền cọc được ghi nhận trong hệ thống.

#### Luồng chính (Basic Flow)

| Bước | Hành động | Hệ thống |
|------|-----------|----------|
| 1 | Chủ trọ chọn "Lập hợp đồng mới" | Hiển thị form yêu cầu chọn phòng |
| 2 | Chủ trọ chọn phòng cần cho thuê | - |
| 3 | Hệ thống thực hiện kiểm tra phòng trống (UC08) | Kiểm tra trạng thái phòng |
| 4 | Phòng hợp lệ (trống) | Hiển thị form hợp đồng |
| 5 | Chủ trọ chọn khách thuê (từ danh sách hoặc thêm mới) | Hiển thị danh sách khách thuê |
| 6 | Chủ trọ nhập thông tin hợp đồng (ngày bắt đầu, ngày kết thúc, giá thuê, tiền cọc, điều khoản) | - |
| 7 | Chủ trọ nhấn "Tạo hợp đồng" | Xác nhận dữ liệu |
| 8 | Hệ thống lưu hợp đồng, cập nhật trạng thái phòng thành "Đã thuê" | Tạo bản ghi HĐ |
| 9 | Hệ thống hiển thị thông báo "Tạo hợp đồng thành công" | - |

#### Luồng thay thế (Alternative Flow)

| Bước | Điều kiện | Hành động |
|------|-----------|-----------|
| 5a | Khách thuê chưa có trong hệ thống | Chủ trọ chọn "Thêm khách mới", nhập thông tin khách. Hệ thống lưu và quay lại bước 6. |

#### Luồng ngoại lệ (Exception Flow)

| Bước | Điều kiện | Hành động |
|------|-----------|-----------|
| 3a | Phòng không trống (đã thuê hoặc đang bảo trì) | Hệ thống thông báo "Phòng không khả dụng". Quay lại bước 2 để chọn phòng khác. |
| 7a | Ngày kết thúc trước ngày bắt đầu | Hệ thống báo lỗi validate. Quay lại bước 6. |

#### Biểu đồ Sequence cho UC07

```
@startuml
participant "Chủ trọ" as landlord
participant "Giao diện\nLập HĐ" as ui
participant "Control:\nLập HĐ" as control
participant "Entity:\nPhòng trọ" as room
participant "Entity:\nKhách thuê" as tenant
participant "Entity:\nHợp đồng" as contract

landlord -> ui : 1. Chọn "Lập HĐ mới"
activate ui
ui -> control : 2. Yêu cầu form
activate control
control -> room : 3. Lấy danh sách phòng trống
activate room
room --> control : 4. Danh sách phòng
deactivate room
control --> ui : 5. Hiển thị form
deactivate control
deactivate ui

landlord -> ui : 6. Chọn phòng
ui -> control : 7. Gửi phòng đã chọn
activate control
control -> room : 8. Kiểm tra trạng thái phòng
activate room
alt Phòng trống
    room --> control : 9. Trạng thái: TRỐNG
    control -> ui : 10. Hiển thị form HĐ
    deactivate control
    deactivate ui
    
    landlord -> ui : 11. Chọn khách thuê, nhập thông tin
    ui -> control : 12. Submit form HĐ
    activate control
    control -> tenant : 13. Kiểm tra khách thuê
    activate tenant
    tenant --> control : 14. Thông tin khách
    deactivate tenant
    control -> contract : 15. Tạo hợp đồng mới
    activate contract
    contract --> control : 16. HĐ đã tạo
    deactivate contract
    control -> room : 17. Cập nhật phòng: ĐÃ THUÊ
    activate room
    room --> control : 18. OK
    deactivate room
    control --> ui : 19. Thông báo thành công
    deactivate control
    ui --> landlord : 20. "Tạo HĐ thành công"
else Phòng không trống
    room --> control : Phòng không trống
    control --> ui : Thông báo lỗi
    ui --> landlord : "Phòng không khả dụng"
end
@enduml
```

---

### 2.2.2 UC13: Lập hóa đơn tính tiền

| Thuộc tính | Nội dung |
|------------|----------|
| **Use Case ID** | UC13 |
| **Use Case Name** | Lập hóa đơn tính tiền |
| **Actor(s)** | Chủ trọ (Landlord) |
| **Mô tả** | Hệ thống tự động tính toán tổng tiền hóa đơn hàng tháng dựa trên chỉ số điện/nước và đơn giá dịch vụ |
| **Priority** | **Cao (Core Feature)** |
| **Ghi chú** | Có thể kích hoạt UC14 (Gửi thông báo) qua quan hệ <<extend>> |

#### Tiền điều kiện (Pre-conditions)
- Chủ trọ đã đăng nhập thành công.
- Đã ghi chỉ số điện/nước cho kỳ hiện tại (UC12).
- Đã cấu hình đơn giá dịch vụ (UC05).
- Có ít nhất một hợp đồng đang hiệu lực.

#### Hậu điều kiện (Post-conditions)
- Hóa đơn được tạo với trạng thái "Chưa thanh toán" cho từng phòng.
- Hệ thống ghi nhận ngày tạo và hạn thanh toán.

#### Công thức tính tiền

```
┌─────────────────────────────────────────────────────────────────┐
│                    CÔNG THỨC TÍNH HÓA ĐƠN                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Tổng tiền = Tiền phòng + Tiền điện + Tiền nước + Phí dịch vụ   │
│                                                                 │
│  Trong đó:                                                      │
│  ┌──────────────┬──────────────────────────────────────────┐   │
│  │ Tiền phòng   │ = Giá thuê tháng (theo HĐ)               │   │
│  ├──────────────┼──────────────────────────────────────────┤   │
│  │ Tiền điện   │ = (Điện mới - Điện cũ) × Đơn giá điện    │   │
│  ├──────────────┼──────────────────────────────────────────┤   │
│  │ Tiền nước   │ = (Nước mới - Nước cũ) × Đơn giá nước    │   │
│  ├──────────────┼──────────────────────────────────────────┤   │
│  │ Phí dịch vụ │ = Tổng (Giá wifi + Giá rác + ...)        │   │
│  └──────────────┴──────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### Luồng chính (Basic Flow)

| Bước | Hành động | Hệ thống |
|------|-----------|----------|
| 1 | Chủ trọ chọn "Lập hóa đơn" và chọn kỳ thanh toán (tháng/năm) | Hiển thị form |
| 2 | Hệ thống hiển thị danh sách phòng đang thuê với chỉ số tiêu thụ | Tổng hợp dữ liệu |
| 3 | Hệ thống tự động tính toán cho từng phòng | Áp dụng công thức |
| 4 | Hệ thống hiển thị bảng hóa đơn dự kiến để chủ trọ xem trước | Preview |
| 5 | Chủ trọ xác nhận và nhấn "Tạo hóa đơn" | - |
| 6 | Hệ thống tạo hóa đơn cho từng phòng với trạng thái "Chưa thanh toán" | Lưu vào DB |
| 7 | Hệ thống thông báo "Tạo hóa đơn thành công" | - |

#### Luồng thay thế (Alternative Flow)

| Bước | Điều kiện | Hành động |
|------|-----------|-----------|
| 5a | Chủ trọ chọn "Gửi thông báo" | Kích hoạt UC14 - Gửi thông báo Email/SMS |
| 4a | Chủ trọ muốn điều chỉnh | Nhập khoản phụ thu/giảm trừ cho phòng cụ thể |

#### Luồng ngoại lệ (Exception Flow)

| Bước | Điều kiện | Hành động |
|------|-----------|-----------|
| 2a | Chưa ghi chỉ số cho một số phòng | Hệ thống cảnh báo danh sách phòng chưa có chỉ số. Chủ trọ có thể bỏ qua (tạo hóa đơn không có điện/nước) hoặc quay lại ghi chỉ số. |

#### Biểu đồ Sequence cho UC13

```
@startuml
participant "Chủ trọ" as landlord
participant "Giao diện\nLập HĐ" as ui
participant "Control:\nTính tiền" as billing
participant "Entity:\nChỉ số Đ/N" as meter
participant "Entity:\nDịch vụ" as service
participant "Entity:\nHóa đơn" as invoice

landlord -> ui : 1. Chọn "Lập hóa đơn", kỳ 2026-03
activate ui
ui -> billing : 2. Yêu cầu lập HĐ kỳ 2026-03
activate billing

billing -> billing : 3. Lấy danh sách HĐ đang hiệu lực
loop Mỗi hợp đồng
    billing -> meter : 4. Lấy chỉ số Đ/N kỳ 2026-03
    activate meter
    alt Có chỉ số
        meter --> billing : Chỉ số điện/nước
        billing -> billing : Tính tiền điện = (Mới-Cũ) × Đơn giá
        billing -> billing : Tính tiền nước = (Mới-Cũ) × Đơn giá
    else Không có chỉ số
        meter --> billing : Không có dữ liệu
    end
    deactivate meter
    
    billing -> service : 5. Lấy đơn giá dịch vụ
    activate service
    service --> billing : Danh sách dịch vụ (wifi, rác...)
    deactivate service
    
    billing -> billing : 6. Tính phí dịch vụ
    billing -> billing : 7. Tính tổng = Phòng + Điện + Nước + DV
end

billing --> ui : 8. Bảng preview hóa đơn
deactivate billing
ui --> landlord : 9. Hiển thị preview
deactivate ui

landlord -> ui : 10. Xác nhận "Tạo hóa đơn"
ui -> billing : 11. Submit
activate billing

loop Mỗi preview
    billing -> invoice : 12. Tạo hóa đơn
    activate invoice
    invoice --> billing : 13. HĐ đã lưu
    deactivate invoice
end

billing --> ui : 14. Thông báo thành công
deactivate billing
ui --> landlord : 15. "Tạo hóa đơn thành công"
deactivate ui
@enduml
```

---

### 2.2.3 UC22: Thanh toán hóa đơn Online

| Thuộc tính | Nội dung |
|------------|----------|
| **Use Case ID** | UC22 |
| **Use Case Name** | Thanh toán hóa đơn Online |
| **Actor(s)** | Khách thuê (Tenant), Cổng thanh toán (VNPay/MoMo) |
| **Mô tả** | Khách thuê thanh toán hóa đơn trực tiếp qua cổng thanh toán tích hợp (VNPay, MoMo, chuyển khoản ngân hàng) |
| **Priority** | Cao |
| **Ghi chú** | Tương tác với external actor "Cổng thanh toán" qua đường Association |

#### Tiền điều kiện (Pre-conditions)
- Khách thuê đã đăng nhập thành công vào hệ thống.
- Có hóa đơn ở trạng thái "Chưa thanh toán" cho khách thuê.
- Cổng thanh toán đang hoạt động bình thường.

#### Hậu điều kiện (Post-conditions)
- Hóa đơn chuyển trạng thái thành "Đã thanh toán" (nếu thanh toán đầy đủ) hoặc "Thanh toán một phần" (nếu chưa đủ).
- Giao dịch được ghi nhận trong lịch sử thanh toán.
- Khách thuê nhận được thông báo xác nhận thanh toán.

#### Luồng chính (Basic Flow)

| Bước | Hành động | Hệ thống / Actor |
|------|-----------|------------------|
| 1 | Khách thuê chọn "Hóa đơn của tôi" | Hiển thị danh sách hóa đơn |
| 2 | Khách thuê chọn hóa đơn chưa thanh toán | Hiển thị chi tiết |
| 3 | Khách thuê nhấn "Thanh toán online" | Hiển thị phương thức |
| 4 | Hệ thống hiển thị các phương thức thanh toán (VNPay, MoMo, chuyển khoản) | - |
| 5 | Khách thuê chọn phương thức và nhấn "Thanh toán" | - |
| 6 | Hệ thống chuyển hướng đến trang thanh toán của cổng | - |
| 7 | Khách thuê hoàn tất thanh toán trên cổng | - |
| 8 | Cổng thanh toán gửi kết quả về hệ thống (callback) | Xử lý callback |
| 9 | Hệ thống cập nhật hóa đơn thành "Đã thanh toán" | Lưu trạng thái |
| 10 | Hệ thống ghi nhận giao dịch | Lưu vào lịch sử |
| 11 | Hệ thống hiển thị "Thanh toán thành công" kèm biên lai | - |

#### Luồng ngoại lệ (Exception Flow)

| Bước | Điều kiện | Hành động |
|------|-----------|-----------|
| 7a | Khách thuê hủy thanh toán trên cổng | Hệ thống nhận callback "Đã hủy". Hóa đơn giữ nguyên trạng thái "Chưa thanh toán". Hiển thị thông báo "Thanh toán đã bị hủy". |
| 8a | Cổng thanh toán trả về lỗi | Hệ thống hiển thị "Thanh toán thất bại", ghi log lỗi, gợi ý thử lại. |
| 8b | Timeout - không nhận được phản hồi | Hệ thống đánh dấu giao dịch "Đang xử lý", kiểm tra lại sau với cổng thanh toán. |

#### Biểu đồ Sequence cho UC22

```
@startuml
participant "Khách thuê" as tenant
participant "Giao diện\nThanh toán" as ui
participant "Control:\nThanh toán" as payment
participant "Entity:\nHóa đơn" as invoice
participant "Entity:\nGiao dịch" as transaction
participant "Cổng thanh toán\n(VNPay/MoMo)" as gateway

tenant -> ui : 1. Chọn hóa đơn, nhấn "Thanh toán online"
activate ui
ui -> payment : 2. Yêu cầu thanh toán
activate payment

payment -> invoice : 3. Lấy thông tin hóa đơn
activate invoice
invoice --> payment : 4. Thông tin: Phòng 101, Tổng: 2.500.000đ
deactivate invoice

payment --> ui : 5. Hiển thị phương thức thanh toán
deactivate payment
ui --> tenant : 6. Chọn phương thức (VNPay)
deactivate ui

tenant -> ui : 7. Xác nhận thanh toán
ui -> payment : 8. Gửi yêu cầu thanh toán
activate payment

payment -> gateway : 9. Redirect đến VNPay với thông tin thanh toán
activate gateway
note right: Tạo URL thanh toán\nvới order info,\namount, return URL
deactivate gateway

gateway -> tenant : 10. Hiển thị trang thanh toán VNPay
ui --> tenant : (Trình duyệt chuyển hướng)
deactivate ui

tenant -> gateway : 11. Hoàn tất thanh toán thành công
gateway -> payment : 12. Callback với kết quả thanh toán
activate payment

alt Thanh toán thành công
    payment -> invoice : 13. Cập nhật trạng thái: ĐÃ THANH TOÁN
    activate invoice
    invoice --> payment : 14. OK
    deactivate invoice
    
    payment -> transaction : 15. Ghi nhận giao dịch
    activate transaction
    transaction --> payment : 16. Giao dịch đã lưu
    deactivate transaction
    
    payment --> ui : 17. Thanh toán thành công
    deactivate payment
    ui --> tenant : 18. "Thanh toán thành công" + Biên lai
    
else Thanh toán thất bại/Hủy
    payment --> ui : Thanh toán thất bại/Hủy
    deactivate payment
    ui --> tenant : Thông báo lỗi/Hủy
end
@enduml
```

---

# CHƯƠNG 3: PHÂN TÍCH HƯỚNG ĐỐI TƯỢNG

## 3.1 Phân tích các lớp theo mô hình BCE (Boundary-Control-Entity)

### 3.1.1 Giới thiệu mô hình BCE

Mô hình BCE là một phương pháp phân tích hướng đối tượng giúp xác định ba loại lớp chính:

| Loại lớp | Ký hiệu | Mô tả | Ví dụ |
|----------|---------|-------|-------|
| **Boundary** | Lớp biên | Giao diện giữa hệ thống và tác nhân | Form nhập liệu, màn hình hiển thị |
| **Control** | Lớp điều khiển | Xử lý logic nghiệp vụ, điều phối | Service, Controller |
| **Entity** | Lớp thực thể | Lưu trữ dữ liệu, đại diện cho đối tượng | Bảng trong database |

### 3.1.2 Phân tích tính năng tính tiền điện nước (UC12 + UC13)

Đây là tính năng cốt lõi nhất của hệ thống, bao gồm:

- **UC12**: Ghi chỉ số điện/nước
- **UC13**: Lập hóa đơn tính tiền

#### Biểu đồ phân tích BCE cho tính năng tính tiền

```
┌─────────────────────────────────────────────────────────────────────┐
│                    TÍNH NĂNG TÍNH TIỀN ĐIỆN NƯỚC                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    BOUNDARY CLASSES                          │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │                                                              │   │
│   │   ┌─────────────────┐      ┌─────────────────┐              │   │
│   │   │ ChiSoView       │      │ HoaDonView      │              │   │
│   │   │ - Form ghi số   │      │ - Form tạo HĐ   │              │   │
│   │   │ - Danh sách CS  │      │ - Preview HĐ    │              │   │
│   │   │ - Bảng số liệu  │      │ - Chi tiết HĐ   │              │   │
│   │   └────────┬────────┘      └────────┬────────┘              │   │
│   │            │                         │                      │   │
│   └────────────┼─────────────────────────┼──────────────────────┘   │
│                │                         │                          │
│                ▼                         ▼                          │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    CONTROL CLASSES                          │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │                                                              │   │
│   │   ┌─────────────────────────────────────────────────────┐   │   │
│   │   │ ChiSoController / ChiSoService                       │   │   │
│   │   │ - saveChiSo(phongId, dienMoi, nuocMoi, kyGhi)       │   │   │
│   │   │ - calculateTieuThu(dienCu, dienMoi, nuocCu, nuocMoi)│   │   │
│   │   │ - validateChiSo(chiSoMoi, chiSoCu)                  │   │   │
│   │   └─────────────────────────────────────────────────────┘   │   │
│   │                           │                                 │   │
│   │                           ▼                                 │   │
│   │   ┌─────────────────────────────────────────────────────┐   │   │
│   │   │ HoaDonController / HoaDonService                     │   │   │
│   │   │ - previewInvoices(kyThanhToan)                      │   │   │
│   │   │ - createInvoice(hopDongId, kyThanhToan)              │   │   │
│   │   │ - calculateTongTien(tienPhong, tienDien, tienNuoc)   │   │   │
│   │   │ - getDonGia(dichVu)                                 │   │   │
│   │   └─────────────────────────────────────────────────────┘   │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                               │                                     │
│                               ▼                                     │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                    ENTITY CLASSES                           │   │
│   ├─────────────────────────────────────────────────────────────┤   │
│   │                                                              │   │
│   │   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │   │
│   │   │  ChiSo       │ │   DichVu     │ │   HoaDon     │        │   │
│   │   │  DienNuoc    │ │              │ │              │        │   │
│   │   ├──────────────┤ ├──────────────┤ ├──────────────┤        │   │
│   │   │ phongId     │ │ tenDV        │ │ hopDong      │        │   │
│   │   │ kyGhi       │ │ donGia       │ │ kyThanhToan  │        │   │
│   │   │ dienCu      │ │ donViTinh    │ │ tienPhong    │        │   │
│   │   │ dienMoi     │ │ loaiDV       │ │ tienDien     │        │   │
│   │   │ nuocCu      │ │              │ │ tienNuoc     │        │   │
│   │   │ nuocMoi     │ │              │ │ phiDichVu    │        │   │
│   │   │ ngayGhi     │ │              │ │ tongTien     │        │   │
│   │   └──────────────┘ └──────────────┘ │ trangThai   │        │   │
│   │                                      └──────────────┘        │   │
│   │                                                              │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.1.3 Chi tiết các lớp cho tính năng tính tiền

#### 3.1.3.1 Boundary Classes (Lớp Biên)

| Tên lớp | Thuộc tính | Phương thức | Mô tả |
|---------|------------|-------------|-------|
| **ChiSoDienNuocForm** | phongId, kyGhi, dienMoi, nuocMoi | validate(), submit() | Form nhập chỉ số điện/nước |
| **ChiSoDienNuocList** | danhSachChiSo, phongSelected | filter(), search() | Bảng hiển thị danh sách chỉ số |
| **HoaDonPreview** | danhSachPreview, kyThanhToan | confirm(), cancel() | Màn hình xem trước hóa đơn |
| **HoaDonDetail** | hoaDon, chiTietDienNuoc | viewHistory(), pay() | Chi tiết một hóa đơn |

#### 3.1.3.2 Control Classes (Lớp Điều khiển)

| Tên lớp | Thuộc tính | Phương thức | Mô tả |
|---------|------------|-------------|-------|
| **ChiSoService** | chiSoRepository, phongService | saveChiSo(), getChiSoByKy(), validateTieuThu() | Xử lý logic ghi chỉ số |
| **TinhToanService** | dichVuRepository | calculateTienDien(), calculateTienNuoc(), calculateTongTien() | Logic tính tiền |
| **HoaDonService** | hoaDonRepository, hopDongRepository, chiSoService | createInvoice(), previewBatchInvoices(), recordPayment() | Tạo và quản lý hóa đơn |

#### 3.1.3.3 Entity Classes (Lớp Thực thể)

**ChiSoDienNuoc Entity:**

```java
@Entity
@Table(name = "chi_so_dien_nuoc", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"phong_id", "ky_ghi"})
})
public class ChiSoDienNuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chiSoId;
    
    @ManyToOne
    @JoinColumn(name = "phong_id", nullable = false)
    private PhongTro phongTro;
    
    @Column(nullable = false, length = 7)  // Format: "YYYY-MM"
    private String kyGhi;
    
    @Column(nullable = false)
    private Integer dienCu = 0;
    
    @Column(nullable = false)
    private Integer dienMoi = 0;
    
    @Column(nullable = false)
    private Integer nuocCu = 0;
    
    @Column(nullable = false)
    private Integer nuocMoi = 0;
    
    private LocalDate ngayGhi;
    
    // Computed methods
    @Transient
    public int getDienTieuThu() {
        return dienMoi - dienCu;
    }
    
    @Transient
    public int getNuocTieuThu() {
        return nuocMoi - nuocCu;
    }
}
```

**DichVu Entity:**

```java
@Entity
@Table(name = "dich_vu")
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dichVuId;
    
    @Column(nullable = false, length = 50)
    private String tenDV;  // "Điện", "Nước", "Wifi", "Rác"
    
    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal donGia;
    
    @Column(length = 20)
    private String donViTinh;  // "kWh", "m³", "tháng"
    
    private Boolean hoatDong = true;
}
```

**HoaDon Entity:**

```java
@Entity
@Table(name = "hoa_don", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"hop_dong_id", "ky_thanh_toan"})
})
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hoaDonId;
    
    @ManyToOne
    @JoinColumn(name = "hop_dong_id", nullable = false)
    private HopDong hopDong;
    
    @Column(nullable = false, length = 7)  // Format: "YYYY-MM"
    private String kyThanhToan;
    
    @Column(precision = 12, scale = 0)
    private BigDecimal tienPhong;
    
    @Column(precision = 12, scale = 0)
    private BigDecimal tienDien;
    
    @Column(precision = 12, scale = 0)
    private BigDecimal tienNuoc;
    
    @Column(precision = 12, scale = 0)
    private BigDecimal phiDichVu;
    
    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal tongTien;
    
    private LocalDate hanThanhToan;
    
    @Enumerated(EnumType.STRING)
    private TrangThaiHoaDon trangThai;
    
    private LocalDateTime ngayTao;
}
```

### 3.1.4 Biểu đồ Class Diagram (Mức Phân tích)

```
@startuml
skinparam classAttributeIconSize 0

' Entity Classes
class ChiSoDienNuoc {
    - chiSoId: Long
    - kyGhi: String
    - dienCu: Integer
    - dienMoi: Integer
    - nuocCu: Integer
    - nuocMoi: Integer
    - ngayGhi: LocalDate
    + getDienTieuThu(): int
    + getNuocTieuThu(): int
}

class DichVu {
    - dichVuId: Long
    - tenDV: String
    - donGia: BigDecimal
    - donViTinh: String
    - hoatDong: Boolean
}

class HoaDon {
    - hoaDonId: Long
    - kyThanhToan: String
    - tienPhong: BigDecimal
    - tienDien: BigDecimal
    - tienNuoc: BigDecimal
    - phiDichVu: BigDecimal
    - tongTien: BigDecimal
    - hanThanhToan: LocalDate
    - trangThai: TrangThaiHoaDon
    - ngayTao: LocalDateTime
    + getDaThanhToan(): BigDecimal
    + getConNo(): BigDecimal
}

class PhongTro {
    - phongId: Long
    - soPhong: String
    - tang: Integer
    - dienTich: Float
    - giaThue: BigDecimal
    - trangThai: TrangThaiPhong
}

class HopDong {
    - hopDongId: Long
    - ngayBatDau: LocalDate
    - ngayKetThuc: LocalDate
    - giaThue: BigDecimal
    - tienCoc: BigDecimal
    - trangThai: TrangThaiHopDong
}

' Control Classes
class ChiSoService {
    - chiSoRepository
    - phongService
    + saveChiSo(phongId, kyGhi, dienMoi, nuocMoi)
    + getChiSoByPhong(phongId, kyGhi)
    + validateTieuThu(chiSo)
}

class TinhToanService {
    - dichVuRepository
    + calculateTienDien(tieuThu, donGia)
    + calculateTienNuoc(tieuThu, donGia)
    + calculatePhiDichVu()
    + calculateTongTien()
}

class HoaDonService {
    - hoaDonRepository
    - hopDongRepository
    - chiSoService
    + previewBatchInvoices(kyThanhToan)
    + createInvoice(hopDongId, kyThanhToan)
    + recordPayment(hoaDonId, soTien)
}

' Boundary Classes  
class ChiSoDienNuocForm {
    - phongId: Long
    - kyGhi: String
    - dienMoi: Integer
    - nuocMoi: Integer
    + validate()
    + submit()
}

class HoaDonPreview {
    - danhSachPreview
    - kyThanhToan: String
    + confirm()
    + cancel()
}

' Relationships
ChiSoDienNuoc "1" --> "1" PhongTro : thuộc về >
ChiSoDienNuoc "0..*" --> "0..1" DichVu : sử dụng đơn giá >
HoaDon "1" --> "1" HopDong : cho >
HopDong "1" --> "1" PhongTro : tại >

PhongTro "1" --> "0..*" HopDong : có nhiều >

ChiSoService ..> ChiSoDienNuoc : sử dụng >
TinhToanService ..> DichVu : sử dụng >
HoaDonService ..> HoaDon : quản lý >
HoaDonService ..> ChiSoDienNuoc : đọc >

ChiSoDienNuocForm ..> ChiSoService : gửi yêu cầu >
HoaDonPreview ..> HoaDonService : gửi yêu cầu >

note top of ChiSoDienNuoc
  Lưu trữ chỉ số điện/nước
  theo từng phòng mỗi kỳ
end note

note top of HoaDon
  Tính tổng tiền:
  Phòng + Điện + Nước + DV
end note
@enduml
```

## 3.2 Biểu đồ hoạt động (Activity Diagram)

### 3.2.1 Activity Diagram cho quy trình tính tiền điện nước

```
@startuml
|Admin/Landlord|
start
:Navigate to "Ghi chỉ số điện nước";
:Select kỳ ghi (tháng/năm);

|Business Logic|
note right
  Hệ thống hiển thị:
  - Danh sách phòng đang thuê
  - Chỉ số cũ của kỳ trước
end note

|Business Logic|
:Load list of active rooms;

while (For each room?) is (Yes)
  :Display room info with old meter reading;
  
  |Admin/Landlord|
  :Input new electricity reading;
  :Input new water reading;
  
  |Business Logic|
  :Validate readings;
  
  if (New reading < Old reading?) then (Yes)
    :Show warning error;
    note right
      "Chỉ số mới không thể 
       nhỏ hơn chỉ số cũ"
    end note
  else (No)
    :Calculate consumption;
    :Save meter reading;
  endif
  
  :Next room;
endwhile (No)

|Admin/Landlord|
:Click "Xác nhận hoàn tất";

|Business Logic|
:Generate consumption report;
:Save all readings;

stop

note right of "Generate consumption report"
  Bảng tổng hợp:
  - Số phòng đã ghi
  - Tổng điện tiêu thụ
  - Tổng nước tiêu thụ
end note
@enduml
```

### 3.2.2 Activity Diagram cho quy trình lập hóa đơn

```
@startuml
|Admin/Landlord|
start
:Navigate to "Lập hóa đơn";
:Sele ct kỳ thanh toán (tháng/năm);

|Business Logic|
:Load active contracts;
:Check meter readings for each room;

if (Any room missing meter readings?) then (Yes)
  :Show warning list;
  note right
    "Một số phòng chưa có 
     chỉ số điện/nước"
  end note
  |Admin/Landlord|
  :Choose action;
  if (Skip rooms?) then (Yes)
    :Continue without these rooms;
  else (No)
    :Go back to record meter readings;
    stop
  endif
else (No)
  :Proceed with all rooms;
endif

|Business Logic|
:Calculate preview for all rooms;

|Admin/Landlord|
:Review invoice preview;
if (Need adjustment?) then (Yes)
  :Add surcharge/discount;
  :Enter reason;
else (No)
  :Proceed;
endif

|Admin/Landlord|
:Click "Tạo hóa đơn";

|Business Logic|
repeat loop
  :Create invoice for contract;
  :Calculate total = Room + Electric + Water + Services;
  :Set status = "CHUA_THANH_TOAN";
  :Save invoice;
repeat while (More contracts?)

|Admin/Landlord|
:View success message;
:Option to send notifications;

if (Send notifications?) then (Yes)
  |Business Logic|
  :Generate notification for each tenant;
  :Send via Email/SMS;
  note right
    <<extend>> UC14
    Gửi thông báo cho khách thuê
  end note
else (No)
  :Complete;
endif

stop
@enduml
```

## 3.3 Biểu đồ Sequence cho tính năng tính tiền

### 3.3.1 Sequence Diagram: Ghi chỉ số điện nước

```
@startuml
actor "Chủ trọ" as landlord
participant "ChiSoView" as view
participant "ChiSoController" as controller
participant "ChiSoService" as service
participant "PhongTroService" as roomService
participant "ChiSoDienNuoc Entity" as chiSoEntity
database "Database" as db

-> view : 1. Load form ghi chỉ số
activate view

view -> controller : 2. getDanhSachPhongDangThue()
activate controller

controller -> roomService : 3. findPhongDangThue()
activate roomService

roomService -> db : 4. SELECT phong WHERE status = 'DANG_THUE'
activate db
db --> roomService : 5. Danh sách phòng
deactivate db

roomService --> controller : 6. List<PhongTro>
deactivate roomService

loop Mỗi phòng
    controller -> service : 7. getChiSoCu(phongId, kyGhi)
    activate service
    service -> db : 8. SELECT chi_so WHERE phong_id = ? AND ky_ghi = ?
    activate db
    db --> service : 9. Chi số cũ
    deactivate db
    service --> controller : 10. Thông tin chỉ số cũ
    deactivate service
end

controller --> view : 11. Form với dữ liệu
deactivate controller
view --> landlord : 12. Hiển thị form nhập
deactivate view

landlord -> view : 13. Nhập chỉ số mới, submit

view -> controller : 14. saveChiSo(dto)
activate controller

controller -> service : 15. validateChiSo(dto)
activate service

alt Validation OK
    service -> service : 16. Tính tiêu thụ = Mới - Cũ
    
    service -> chiSoEntity : 17. Create new ChiSoDienNuoc
    activate chiSoEntity
    chiSoEntity --> service : 18. Entity created
    deactivate chiSoEntity
    
    service -> db : 19. INSERT/UPDATE chi_so
    activate db
    db --> service : 20. Success
    deactivate db
    
    service --> controller : 21. Success
    deactivate service
    
    controller --> view : 22. Thông báo thành công
    deactivate controller
    view --> landlord : 23. "Đã lưu chỉ số"
    
else Validation FAILED
    service --> controller : Validation Error
    deactivate service
    controller --> view : Error message
    deactivate controller
    view --> landlord : "Lỗi: chỉ số mới < chỉ số cũ"
end
@enduml
```

### 3.3.2 Sequence Diagram: Tạo hóa đơn hàng loạt

```
@startuml
actor "Chủ trọ" as landlord
participant "HoaDonView" as view
participant "HoaDonController" as controller
participant "HoaDonService" as service
participant "HopDongService" as contractService
participant "ChiSoService" as chiSoService
participant "DichVuService" as dichVuService
participant "HoaDon Entity" as invoiceEntity
database "Database" as db

-> view : 1. Navigate to "Lập hóa đơn"
activate view

view -> controller : 2. previewInvoices(kyThanhToan)
activate controller

controller -> service : 3. previewBatchInvoices(kyThanhToan)
activate service

service -> contractService : 4. getActiveContracts()
activate contractService
contractService -> db : 5. SELECT hop_dong WHERE status = 'DANG_HIEU_LUC'
activate db
db --> contractService : 6. List<HopDong>
deactivate db
contractService --> service : 7. Contracts
deactivate contractService

loop Mỗi hợp đồng
    service -> service : 8. Check if invoice exists for this period
    
    alt Invoice NOT exists
        service -> chiSoService : 9. getChiSo(phongId, kyThanhToan)
        activate chiSoService
        chiSoService -> db : 10. SELECT chi_so WHERE phong_id = ? AND ky_ghi = ?
        activate db
        db --> chiSoService : 11. ChiSoDienNuoc (optional)
        deactivate db
        chiSoService --> service : 12. ChiSo
        deactivate chiSoService
        
        service -> dichVuService : 13. getDichVuByLoai("Điện")
        activate dichVuService
        dichVuService -> db : 14. SELECT * FROM dich_vu WHERE ten = 'Điện'
        activate db
        db --> dichVuService : 15. DichVu(Điện, 3500/kWh)
        deactivate db
        dichVuService --> service : 16. DonGiaDien
        deactivate dichVuService
        
        service -> dichVuService : 17. getDichVuByLoai("Nước")
        activate dichVuService
        dichVuService -> db : 18. SELECT * FROM dich_vu WHERE ten = 'Nước'
        activate db
        db --> dichVuService : 19. DichVu(Nước, 10000/m³)
        deactivate db
        dichVuService --> service : 20. DonGiaNuoc
        deactivate dichVuService
        
        service -> service : 21. Calculate:
        note right
            tienPhong = hopDong.giaThue
            tienDien = dienTieuThu × 3500
            tienNuoc = nuocTieuThu × 10000
            phiDichVu = wifi + rac
            tongTien = Phong + Dien + Nuoc + DV
        end note
        
        service -> service : 22. Create preview map
    else Invoice EXISTS
        service -> service : Skip this contract
    end
end

service --> controller : 23. List<Preview>
deactivate service
controller --> view : 24. Preview table
deactivate controller
view --> landlord : 25. Display preview

landlord -> view : 26. Click "Tạo hóa đơn"

view -> controller : 27. createBatchInvoices(kyThanhToan)
activate controller

controller -> service : 28. createBatchInvoices(kyThanhToan)
activate service

loop Mỗi preview
    service -> invoiceEntity : 29. Create HoaDon
    activate invoiceEntity
    invoiceEntity --> service : 30. Entity
    deactivate invoiceEntity
    
    service -> db : 31. INSERT hoa_don
    activate db
    db --> service : 32. Saved
    deactivate db
end

service --> controller : 33. Count: X hóa đơn
deactivate service
controller --> view : 34. Success message
deactivate controller
view --> landlord : 35. "Đã tạo X hóa đơn thành công"
deactivate view
@enduml
```

---

# CHƯƠNG 4: KẾ HOẠCH TRIỂN KHAI

## 4.1 Tổng quan kế hoạch

Dự án được thực hiện trong **11 ngày** với **3 thành viên**, bắt đầu từ ngày **18/03/2026**.

| Thông tin | Chi tiết |
|-----------|----------|
| **Tổng số ngày** | 11 ngày |
| **Ngày bắt đầu** | 18/03/2026 |
| **Ngày kết thúc** | 28/03/2026 |
| **Số thành viên** | 3 người |
| **Công nghệ sử dụng** | Spring Boot + Thymeleaf + MySQL |

### Thông tin thành viên

| STT | Họ tên | Phân công chính |
|-----|--------|-----------------|
| 1 | Trần Quang Toản | Phân tích yêu cầu, thiết kế, cài đặt chức năng chính (phần 1) |
| 2 | Trần Văn Tưởng | Vẽ diagram, cài đặt chức năng (phần 2), chuẩn bị slide |
| 3 | Ô Duy Hoàng Thiện | Phân tích phi chức năng, class diagram, cài đặt chức năng (phần 3) |

## 4.2 Bảng tiến độ chi tiết

### GIAI ĐOẠN 1: Khảo sát & Phân tích yêu cầu (18/03 - 19/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 1.1 | Khảo sát bài toán | Tìm hiểu thực tế quản lý phòng trọ, xác định các bên liên quan | Trần Quang Toản | 18/03/2026 | 19/03/2026 | ✅ Đã xong |
| 1.2 | Phân tích yêu cầu chức năng | Liệt kê chi tiết các chức năng: đăng phòng, tìm phòng, quản lý hợp đồng, tính tiền, thanh toán... | Trần Văn Tưởng | 18/03/2026 | 19/03/2026 | ✅ Đã xong |
| 1.3 | Phân tích yêu cầu phi chức năng | Hiệu năng, bảo mật, giao diện thân thiện, tương thích trình duyệt... | Ô Duy Hoàng Thiện | 18/03/2026 | 19/03/2026 | ✅ Đã xong |
| 1.4 | Viết mô tả bài toán | Tổng hợp thành tài liệu mô tả bài toán hoàn chỉnh | Trần Quang Toản | 18/03/2026 | 19/03/2026 | ✅ Đã xong |

### GIAI ĐOẠN 2: Đặc tả Use Case (19/03 - 20/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 2.1 | Xác định Actor và Use Case | Liệt kê actors (Chủ trọ, Người thuê) và các use case tương ứng | Trần Quang Toản | 19/03/2026 | 20/03/2026 | ✅ Đã xong |
| 2.2 | Vẽ Use Case Diagram | Vẽ sơ đồ use case tổng quan | Trần Văn Tưởng | 19/03/2026 | 20/03/2026 | ✅ Đã xong |
| 2.3 | Đặc tả Use Case chi tiết (nhóm 1) | Viết đặc tả UC01-UC08 | Trần Quang Toản | 19/03/2026 | 20/03/2026 | ✅ Đã xong |
| 2.4 | Đặc tả Use Case chi tiết (nhóm 2) | Viết đặc tả UC09-UC16 | Trần Văn Tưởng | 19/03/2026 | 20/03/2026 | ✅ Đã xong |
| 2.5 | Đặc tả Use Case chi tiết (nhóm 3) | Viết đặc tả UC17-UC23 | Ô Duy Hoàng Thiện | 19/03/2026 | 20/03/2026 | ✅ Đã xong |

### GIAI ĐOẠN 3: Phân tích hướng đối tượng (20/03 - 22/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 3.1 | Vẽ Activity Diagram | Vẽ activity diagram cho các luồng chính: Đăng phòng, Tìm & Đặt phòng, Thanh toán | Trần Quang Toản | 20/03/2026 | 22/03/2026 | ✅ Đã xong |
| 3.2 | Vẽ Sequence Diagram | Vẽ sequence diagram cho các use case quan trọng (ít nhất 4-5 diagram) | Trần Văn Tưởng | 20/03/2026 | 22/03/2026 | ✅ Đã xong |
| 3.3 | Xác định lớp phân tích | Xác định các lớp Boundary, Control, Entity cho từng use case | Ô Duy Hoàng Thiện | 20/03/2026 | 22/03/2026 | ✅ Đã xong |
| 3.4 | Vẽ Class Diagram phân tích | Vẽ sơ đồ lớp mức phân tích với quan hệ giữa các lớp | Ô Duy Hoàng Thiện | 20/03/2026 | 22/03/2026 | ✅ Đã xong |

### GIAI ĐOẠN 4: Thiết kế hướng đối tượng (22/03 - 27/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 4.1 | Thiết kế Class Diagram chi tiết | Bổ sung thuộc tính, phương thức, kiểu dữ liệu, quan hệ kế thừa/kết hợp | Ô Duy Hoàng Thiện | 22/03/2026 | 26/03/2026 | 🔄 Đang làm |
| 4.2 | Thiết kế cơ sở dữ liệu | Vẽ ERD hoặc chuyển từ class diagram sang lược đồ CSDL quan hệ | Trần Văn Tưởng | 22/03/2026 | 26/03/2026 | 🔄 Đang làm |
| 4.3 | Thiết kế Sequence Diagram mức thiết kế | Chi tiết hóa sequence diagram với các lớp thiết kế cụ thể | Trần Quang Toản | 22/03/2026 | 27/03/2026 | 🔄 Đang làm |
| 4.4 | Thiết kế giao diện (Mockup) | Tạo wireframe/mockup cho các màn hình chính bằng Figma/Draw.io | Trần Văn Tưởng | 22/03/2026 | 26/03/2026 | 🔄 Đang làm |

### GIAI ĐOẠN 5: Cài đặt (25/03 - 28/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 5.1 | Chọn công nghệ & setup dự án | Chọn stack (Spring Boot + Thymeleaf + MySQL), tạo project, cấu hình | Trần Quang Toản | 25/03/2026 | 25/03/2026 | 🔄 Đang làm |
| 5.2 | Cài đặt chức năng chính (phần 1) | Đăng ký, Đăng nhập, Quản lý phòng trọ, Đăng tin | Trần Quang Toản | 26/03/2026 | 28/03/2026 | 🔄 Đang làm |
| 5.3 | Cài đặt chức năng chính (phần 2) | Tìm kiếm phòng, Đặt phòng, Quản lý hợp đồng | Trần Văn Tưởng | 26/03/2026 | 28/03/2026 | 🔄 Đang làm |
| 5.4 | Cài đặt chức năng chính (phần 3) | Tính tiền, Thanh toán, Dashboard, Báo cáo thống kê | Ô Duy Hoàng Thiện | 26/03/2026 | 28/03/2026 | 🔄 Đang làm |

### GIAI ĐOẠN 6: Báo cáo & Bảo vệ (27/03 - 28/03/2026)

| STT | Công việc | Mô tả | Người phụ trách | Bắt đầu | Kết thúc | Trạng thái |
|-----|-----------|-------|-----------------|----------|----------|------------|
| 6.1 | Viết báo cáo đồ án | Tổng hợp tất cả tài liệu thành báo cáo hoàn chỉnh | Cả nhóm | 27/03/2026 | 28/03/2026 | 🔄 Đang làm |
| 6.2 | Chuẩn bị slide thuyết trình | Tạo slide PowerPoint tóm tắt đồ án để bảo vệ | Trần Văn Tưởng | 27/03/2026 | 28/03/2026 | ⏳ Chưa làm |
| 6.3 | Review & hoàn thiện | Rà soát toàn bộ, sửa lỗi, đảm bảo nhất quán giữa các phần | Cả nhóm | 28/03/2026 | 28/03/2026 | ⏳ Chưa làm |

## 4.3 Biểu đồ Gantt

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│                              BẢNG TIẾN ĐỘ DỰ ÁN - BIỂU ĐỒ GANTT                             │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                              │
│  Công việc                    │ 18  19  20  21  22  23  24  25  26  27  28                   │
│  ─────────────────────────────┼────────────────────────────────────────────────────────────│
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 1 (18-19/03)      │███│███│   │   │   │   │   │   │   │   │   │   │            │
│  ├─ 1.1 Khảo sát bài toán    │██░│░░░│   │   │   │   │   │   │   │   │   │   │            │
│  ├─ 1.2 Yêu cầu chức năng    │░██│░░░│   │   │   │   │   │   │   │   │   │   │            │
│  ├─ 1.3 Yêu cầu phi chức năng│░░█│░░░│   │   │   │   │   │   │   │   │   │   │            │
│  └─ 1.4 Viết mô tả bài toán  │░░░│███│   │   │   │   │   │   │   │   │   │   │            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 2 (19-20/03)      │   │███│███│   │   │   │   │   │   │   │   │   │            │
│  ├─ 2.1 Actor & UC           │   │██░│░░░│   │   │   │   │   │   │   │   │   │            │
│  ├─ 2.2 Use Case Diagram     │   │░██│░░░│   │   │   │   │   │   │   │   │   │            │
│  ├─ 2.3 Đặc tả UC (nhóm 1)   │   │░░█│░░░│   │   │   │   │   │   │   │   │   │            │
│  ├─ 2.4 Đặc tả UC (nhóm 2)   │   │░░░│██░│   │   │   │   │   │   │   │   │   │            │
│  └─ 2.5 Đặc tả UC (nhóm 3)   │   │░░░│░██│   │   │   │   │   │   │   │   │   │            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 3 (20-22/03)      │   │   │███│███│███│   │   │   │   │   │   │   │            │
│  ├─ 3.1 Activity Diagram     │   │   │██░│██░│██░│   │   │   │   │   │   │   │            │
│  ├─ 3.2 Sequence Diagram     │   │   │░██│░██│░░░│   │   │   │   │   │   │   │            │
│  ├─ 3.3 Xác định lớp BCE     │   │   │░░█│░░█│░░░│   │   │   │   │   │   │   │            │
│  └─ 3.4 Class Diagram PT     │   │   │░░░│░░█│███│   │   │   │   │   │   │   │            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 4 (22-27/03)      │   │   │   │   │███│███│███│███│███│███│   │   │            │
│  ├─ 4.1 Class Diagram TD     │   │   │   │   │██░│██░│██░│██░│██░│░░░│   │   │            │
│  ├─ 4.2 Thiết kế CSDL        │   │   │   │   │░██│░██│░░░│   │   │   │   │   │            │
│  ├─ 4.3 Sequence Diagram TD  │   │   │   │   │░░░│░░░│██░│██░│██░│███│   │   │            │
│  └─ 4.4 Thiết kế Mockup      │   │   │   │   │░░░│░░░│██░│██░│░░░│   │   │   │            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 5 (25-28/03)       │   │   │   │   │   │   │   │███│███│███│███│███│            │
│  ├─ 5.1 Setup dự án          │   │   │   │   │   │   │   │███│   │   │   │   │            │
│  ├─ 5.2 Cài đặt (phần 1)     │   │   │   │   │   │   │   │   │███│███│░░░│   │            │
│  ├─ 5.3 Cài đặt (phần 2)     │   │   │   │   │   │   │   │   │███│███│░░░│   │            │
│  └─ 5.4 Cài đặt (phần 3)     │   │   │   │   │   │   │   │   │░░░│███│███│░░░│            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│  GIAI ĐOẠN 6 (27-28/03)      │   │   │   │   │   │   │   │   │   │   │███│███│            │
│  ├─ 6.1 Viết báo cáo         │   │   │   │   │   │   │   │   │   │   │███│███│            │
│  ├─ 6.2 Slide thuyết trình   │   │   │   │   │   │   │   │   │   │   │░░█│███│            │
│  └─ 6.3 Review & hoàn thiện  │   │   │   │   │   │   │   │   │   │   │░░░│███│            │
│                              │  │  │  │  │  │  │  │  │  │  │  │  │  │  │                     │
│                                                                                              │
│  ██ = Đã hoàn thành    ░░ = Đang thực hiện    ││ = Deadline                               │
│                                                                                              │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 4.4 Bảng phân công công việc theo thành viên

### Trần Quang Toản

| Giai đoạn | Công việc | Ngày | Trạng thái |
|-----------|-----------|------|------------|
| 1 | Khảo sát bài toán | 18-19/03 | ✅ |
| 1 | Viết mô tả bài toán | 18-19/03 | ✅ |
| 2 | Xác định Actor và Use Case | 19-20/03 | ✅ |
| 2 | Đặc tả Use Case chi tiết (UC01-UC08) | 19-20/03 | ✅ |
| 3 | Vẽ Activity Diagram | 20-22/03 | ✅ |
| 3 | Vẽ Sequence Diagram | 20-22/03 | ✅ |
| 4 | Thiết kế Sequence Diagram mức thiết kế | 22-27/03 | 🔄 |
| 5 | Setup dự án Spring Boot | 25/03 | 🔄 |
| 5 | Cài đặt chức năng phần 1 | 26-28/03 | 🔄 |
| 6 | Viết báo cáo đồ án | 27-28/03 | 🔄 |

### Trần Văn Tưởng

| Giai đoạn | Công việc | Ngày | Trạng thái |
|-----------|-----------|------|------------|
| 1 | Phân tích yêu cầu chức năng | 18-19/03 | ✅ |
| 2 | Vẽ Use Case Diagram | 19-20/03 | ✅ |
| 2 | Đặc tả Use Case chi tiết (UC09-UC16) | 19-20/03 | ✅ |
| 3 | Vẽ Sequence Diagram | 20-22/03 | ✅ |
| 4 | Thiết kế cơ sở dữ liệu (ERD) | 22-26/03 | 🔄 |
| 4 | Thiết kế giao diện (Mockup) | 22-26/03 | 🔄 |
| 5 | Cài đặt chức năng phần 2 | 26-28/03 | 🔄 |
| 6 | Chuẩn bị slide thuyết trình | 27-28/03 | ⏳ |
| 6 | Viết báo cáo đồ án | 27-28/03 | 🔄 |

### Ô Duy Hoàng Thiện

| Giai đoạn | Công việc | Ngày | Trạng thái |
|-----------|-----------|------|------------|
| 1 | Phân tích yêu cầu phi chức năng | 18-19/03 | ✅ |
| 2 | Đặc tả Use Case chi tiết (UC17-UC23) | 19-20/03 | ✅ |
| 3 | Xác định lớp phân tích BCE | 20-22/03 | ✅ |
| 3 | Vẽ Class Diagram phân tích | 20-22/03 | ✅ |
| 4 | Thiết kế Class Diagram chi tiết | 22-26/03 | 🔄 |
| 5 | Cài đặt chức năng phần 3 | 26-28/03 | 🔄 |
| 6 | Viết báo cáo đồ án | 27-28/03 | 🔄 |

---

# KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 5.1 Kết luận

Trong quá trình thực hiện đồ án, nhóm đã hoàn thành các nhiệm vụ chính sau:

### Những gì đã đạt được:

1. **Hoàn thành phân tích yêu cầu**: Khảo sát thực tế và xác định đầy đủ các yêu cầu chức năng và phi chức năng của hệ thống.

2. **Mô hình hóa hệ thống**: Xây dựng thành công 23 Use Case với đặc tả chi tiết, biểu đồ Use Case Diagram, Activity Diagram, Sequence Diagram.

3. **Phân tích hướng đối tượng**: Xác định và phân loại các lớp Boundary, Control, Entity; vẽ Class Diagram mức phân tích.

4. **Thiết kế hệ thống**: Thiết kế Class Diagram chi tiết, ERD cho cơ sở dữ liệu, giao diện mockup.

5. **Triển khai code**: Bắt đầu cài đặt hệ thống với Spring Boot, Thymeleaf, MySQL.

### Những hạn chế:

1. Chưa triển khai đầy đủ tính năng Email/SMS notification (UC14).
2. Cổng thanh toán online (VNPay/MoMo) mới ở mức demo, chưa tích hợp thực sự.
3. Giao diện chưa được tối ưu hoàn toàn cho mobile.

## 5.2 Hướng phát triển của dự án

### Ngắn hạn (Cần hoàn thiện):

| STT | Tính năng | Mô tả | Ưu tiên |
|-----|-----------|-------|---------|
| 1 | Tích hợp Email/SMS | Gửi thông báo hóa đơn tự động cho khách thuê | Cao |
| 2 | Cổng thanh toán | Tích hợp thực sự với VNPay hoặc MoMo | Cao |
| 3 | Tối ưu Mobile | Cải thiện responsive cho điện thoại | Cao |

### Trung hạn (Nâng cấp):

| STT | Tính năng | Mô tả | Ưu tiên |
|-----|-----------|-------|---------|
| 4 | Ứng dụng di động | Phát triển app Android/iOS cho chủ trọ | Trung |
| 5 | Quản lý tòa nhà | Hỗ trợ nhiều tòa nhà, nhiều chủ trọ | Trung |
| 6 | Báo cáo nâng cao | Biểu đồ doanh thu, phân tích xu hướng | Trung |

### Dài hạn (Mở rộng):

| STT | Tính năng | Mô tả | Ưu tiên |
|-----|-----------|-------|---------|
| 7 | AI/Chatbot | Hỗ trợ tự động trả lời câu hỏi thường gặp | Thấp |
| 8 | Tích hợp IoT | Đọc chỉ số điện nước tự động qua smart meter | Thấp |
| 9 | Multi-language | Hỗ trợ tiếng Anh, tiếng Trung cho người nước ngoài | Thấp |
| 10 | Blockchain | Lưu trữ hợp đồng thuê trên blockchain để chống giả mạo | Thấp |

## 5.3 Bài học kinh nghiệm

Trong quá trình thực hiện đồ án, nhóm đã rút ra một số bài học kinh nghiệm:

1. **Tầm quan trọng của khảo sát thực tế**: Việc đến tận nơi để khảo sát giúp hiểu rõ bài toán thực tế, tránh thiết kế xa vời.

2. **Sử dụng UML đúng cách**: Các biểu đồ UML chỉ là công cụ, cần chọn đúng loại biểu đồ cho đúng mục đích.

3. **Phân chia công việc hợp lý**: Mỗi thành viên nên phụ trách một phần liên quan để đảm bảo tính liên tục.

4. **Kiểm tra thường xuyên**: Cần review code và tài liệu thường xuyên để phát hiện lỗi sớm.

5. **Deadline cứng**: Tuân thủ deadline nghiêm ngặt giúp đảm bảo tiến độ.

---

# PHỤ LỤC

## Phụ lục A: Từ điển thuật ngữ

| Thuật ngữ | Tiếng Anh | Giải thích |
|-----------|-----------|------------|
| Chủ trọ | Landlord | Người sở hữu và quản lý khu trọ |
| Khách thuê | Tenant | Người thuê phòng trọ |
| Hợp đồng | Contract | Thỏa thuận giữa chủ trọ và khách thuê |
| Khu trọ | Area/Building | Khu vực chứa nhiều phòng trọ |
| Phòng trọ | Room | Đơn vị cho thuê trong khu trọ |
| Hóa đơn | Invoice | Bill thể hiện số tiền cần thanh toán |
| Chỉ số | Meter reading | Số điện/nước đã sử dụng |
| Cọc | Deposit | Tiền đặt cọc khi thuê phòng |

## Phụ lục B: Tài liệu tham khảo

1. Craign, S. vànn somerville, I. (2016). *Software Engineering*. 10th Edition. Pearson.
2. Booch, G., Rumbaugh, J., & Jacobson, I. (2005). *The Unified Modeling Language User Guide*. Addison-Wesley.
3. Fowler, M. (2004). *UML Distilled: A Brief Guide to the Standard Object Modeling Language*. Addison-Wesley.
4. Spring Boot Documentation. https://docs.spring.io/spring-boot/docs/current/reference/html/
5. Thymeleaf Documentation. https://www.thymeleaf.org/documentation.html

## Phụ lục C: Liên hệ nhóm

| Thành viên | Email | Số điện thoại |
|------------|-------|---------------|
| Trần Quang Toản | [email] | [SĐT] |
| Trần Văn Tưởng | [email] | [SĐT] |
| Ô Duy Hoàng Thiện | [email] | [SĐT] |

---

**--- HẾT BÁO CÁO ---**

*Báo cáo hoàn thành ngày 28/03/2026*
