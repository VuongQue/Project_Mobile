# README - Dự án S-Parking

---

## 1. Giới thiệu dự án

S-Parking là hệ thống quản lý bãi đỗ xe thông minh dành cho người dùng thuê chỗ gửi xe máy/ô tô, tích hợp chức năng đặt chỗ, thanh toán trực tuyến, quản lý phiên gửi xe, và thông báo cho khách hàng.  

Mục tiêu chính là tạo ra một nền tảng tiện lợi, giúp người dùng tiết kiệm thời gian và nâng cao hiệu quả quản lý bãi đỗ xe.

---

## 2. Cấu trúc dự án

| Thư mục             | Mô tả                                           |
|---------------------|------------------------------------------------|
| `s_parking_api`     | Backend Spring Boot cung cấp các RESTful API.  |
| `s_parking_app`     | Ứng dụng Android giao diện người dùng.          |

---

## 3. Công nghệ sử dụng

### Backend
- Java 11
- Spring Boot (Web, Security, Data JPA)
- JWT (Json Web Token) cho bảo mật
- Maven quản lý dependencies
- MySQL làm database chính
- Lombok hỗ trợ giảm boilerplate code
- Swagger cho tài liệu API

### Frontend (Android)
- Android Studio
- Java/Kotlin (tuỳ từng module)
- Retrofit2 để gọi API backend
- Glide để tải ảnh
- Material Design UI components
- Tích hợp SDK thanh toán: MoMo, ZaloPay

---

## 4. Backend (API)

### 4.1 Cài đặt và chạy backend

1. Cài đặt JDK 11 trở lên.
2. Cài đặt Maven.
3. Tải source code hoặc clone về thư mục `s_parking_api`.
4. Cấu hình database trong file:  
   `src/main/resources/application.properties` hoặc `application.yml`, ví dụ:  
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/s_parking_db
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   jwt.secret=your_jwt_secret_key
### 4.2 Mô tả API chi tiết theo controller

#### 4.2.1 AuthController (`/auth`)

| Endpoint        | Phương thức | Mô tả                                          | Request Body (JSON)                                              | Response                                          |
|-----------------|-------------|------------------------------------------------|-----------------------------------------------------------------|---------------------------------------------------|
| `/login`        | POST        | Đăng nhập, trả về JWT token                     | `{ "username": "user", "password": "pass" }`                   | `{ "token": "jwt_token_here" }`                   |
| `/register`     | POST        | Đăng ký tài khoản mới                            | `{ "username": "...", "password": "...", "role": "USER" }`      | `"Đăng ký thành công"` hoặc lỗi `"Tài khoản đã tồn tại"` |
| `/send-otp`     | POST        | Gửi mã OTP về email cho mục đích xác thực       | `{ "username": "user", "purpose": "reset_password" }`           | `"Đã gửi OTP về email của bạn"`                    |
| `/verify-otp`   | POST        | Xác thực mã OTP                                  | `{ "username": "user", "otp": "123456", "purpose": "reset_password" }` | `"Xác thực OTP thành công"` hoặc lỗi `"Mã OTP không hợp lệ hoặc đã hết hạn"` |
| `/update-info`  | POST        | Cập nhật thông tin người dùng                    | `{ "username": "user", ... các trường thông tin cần cập nhật ... }` | `"Cập nhật thông tin thành công"` hoặc lỗi phù hợp |
| `/reset-password` | POST      | Đặt lại mật khẩu mới cho tài khoản               | `{ "username": "user", "newPassword": "newpass" }`              | `"Đặt lại mật khẩu thành công"` hoặc lỗi `"Không tìm thấy tài khoản"` |

---



#### 4.2.2 UserController (`/user`)

| Endpoint           | Phương thức | Mô tả                                         | Request Body (JSON)                                       | Response                                    |
|--------------------|-------------|-----------------------------------------------|----------------------------------------------------------|---------------------------------------------|
| `/profile`         | POST        | Lấy thông tin profile người dùng hiện tại     | `{ "username": "user" }`                                  | `UserInfoResponse` hoặc lỗi nếu không có dữ liệu hoặc không có quyền |
| `/update-avatar`   | PUT         | Cập nhật URL ảnh đại diện người dùng           | `{ "username": "user", "avatarUrl": "https://..." }`     | `{ success: true, message: "Image uploaded successfully" } hoặc lỗi |



---

#### 4.2.3 BookingController (`/booking`)

| Endpoint             | Phương thức | Mô tả                                | Request Body (JSON)                               | Response                           |
|----------------------|-------------|------------------------------------|-------------------------------------------------|----------------------------------|
| `/my-booking-history` | POST        | Lấy lịch sử đặt chỗ của user hiện tại (chỉ được xem chính mình) | `{ "username": "user" }`                        | Danh sách BookingResponse hoặc lỗi không tìm thấy |
| `/create`            | POST        | Tạo mới một đặt chỗ gửi xe          | BookingRequest (có các trường như username, thời gian, bãi đỗ, ...) | BookingResponse hoặc lỗi "Booking không thành công" |
| `/unpaid`            | POST        | Lấy danh sách các booking chưa thanh toán của user | `{ "username": "user" }`                        | Danh sách BookingResponse chưa thanh toán |
| `/get`               | POST        | Lấy chi tiết booking theo ID        | BookingRequest chứa trường `id` booking          | BookingResponse hoặc 404 nếu không tìm thấy |

---

#### 4.2.4 PaymentController (`/payment`)

| Endpoint                   | Phương thức | Mô tả                                        | Request Body (JSON)                             | Response                                  |
|----------------------------|-------------|----------------------------------------------|------------------------------------------------|-------------------------------------------|
| `/create-transaction`       | POST        | Tạo giao dịch thanh toán qua Ngân hàng        | `PaymentRequest`                               | `PaymentResponse` hoặc lỗi                |
| `/momo/create-transaction`  | POST        | Tạo giao dịch thanh toán qua MoMo             | `PaymentRequest`                               | `PaymentResponse` hoặc lỗi                |
| `/momo/notify`              | POST        | Callback xử lý kết quả thanh toán MoMo        | `{ ... }` payload từ MoMo                      | `"Success"` hoặc lỗi (signature, lỗi thanh toán) |
| `/confirm`                  | PUT         | Xác nhận trạng thái thanh toán (Ngân hàng)    | `ConfirmPaymentRequest`                        | `SuccessResponse` (thành công hoặc lỗi)  |
| `/zalopay/create-transaction`| POST      | Tạo giao dịch thanh toán qua ZaloPay           | `PaymentRequest`                               | `PaymentResponse` hoặc lỗi                |
| `/zalopay/notify`           | POST        | Callback xử lý kết quả thanh toán ZaloPay      | `{ ... }` payload từ ZaloPay                   | `{ return_code: 1, return_message: "Notify success" } hoặc lỗi  |

---

**Lưu ý:**

- Endpoint `/momo/notify` kiểm tra tính hợp lệ chữ ký HMAC SHA256 từ MoMo để xác thực callback.
- Endpoint `/zalopay/notify` nhận notify và cập nhật trạng thái thanh toán.
- Các giao dịch thanh toán đều gắn với user hiện tại qua `Authentication`.
- Mỗi request tạo giao dịch đều cần thông tin chi tiết trong `PaymentRequest`.
- API trả về các response object hoặc thông báo lỗi tương ứng với từng trường hợp.



---

#### 4.2.5 ParkingLotController (`/parking-lots`)

| Endpoint         | Phương thức | Mô tả                                   | Request Body (JSON) | Response                           |
|------------------|-------------|-----------------------------------------|--------------------|----------------------------------|
| `/all`           | GET         | Lấy danh sách tất cả các bãi đỗ xe      | Không có           | Danh sách `ParkingLotResponse` hoặc lỗi không tìm dữ liệu |
| `/available`     | GET         | Lấy danh sách các bãi đỗ xe còn chỗ trống | Không có           | Danh sách `ParkingLotResponse` hoặc lỗi không tìm dữ liệu |


---

#### 4.2.6 SessionController (`/sessions`)

| Endpoint             | Phương thức | Mô tả                                               | Request Body (JSON)                                | Response                                    |
|----------------------|-------------|-----------------------------------------------------|--------------------------------------------------|---------------------------------------------|
| `/user`              | POST        | Lấy danh sách phiên gửi xe của user hiện tại       | `{ "username": "user" }`                          | Danh sách SessionResponse hoặc lỗi không tìm thấy |
| `/my-current-session`| POST        | Lấy phiên gửi xe hiện tại của user                   | `{ "username": "user" }`                          | MyCurrentSessionResponse (có thể rỗng nếu không có) |
| `/check-in-out`      | POST        | Thực hiện check-in hoặc check-out (chỉ ADMIN)       | `{ "username": "...", "licensePlate": "...", "areaId": "..." }` | Tiêu đề notification ("Check In" hoặc "Check Out") hoặc lỗi |
| `/unpaid`            | POST        | Lấy danh sách các phiên gửi xe chưa thanh toán      | `{ "username": "user" }`                          | Danh sách SessionResponse chưa thanh toán hoặc lỗi |

---

**Lưu ý:**
- Endpoint `/check-in-out` được bảo vệ, chỉ có user với vai trò `ADMIN` mới được phép sử dụng.
- Các API yêu cầu username trong request body phải trùng với username của user đang đăng nhập, nếu không trả về lỗi 403.
- Check-in tạo phiên gửi xe mới, check-out cập nhật thời gian kết thúc và tính phí.
- Hệ thống cập nhật trạng thái bãi đỗ xe và gửi notification real-time cho user qua socket.


---

#### 4.2.7 NotificationController (`/notification`)

| Endpoint         | Phương thức | Mô tả                                  | Request Body (JSON)                        | Response                                      |
|------------------|-------------|----------------------------------------|-------------------------------------------|-----------------------------------------------|
| `/yours`         | POST        | Lấy danh sách thông báo của user hiện tại | `{ "username": "user" }`                   | Danh sách `NotificationResponse` hoặc lỗi nếu không có thông báo |
| `/update`        | POST        | Cập nhật trạng thái thông báo (ví dụ: đã đọc) | `{ "username": "user", "id": "notificationId" }` | `SuccessResponse` báo thành công hoặc thất bại |

---

**Lưu ý:**
- Các endpoint chỉ cho phép user truy cập thông báo của chính mình, nếu truy cập thông tin người khác trả về lỗi 403.
- Cập nhật trạng thái thông báo giúp quản lý thông báo đã đọc hoặc xử lý.



#### 4.2.8 ParkingAreaController (`/parking-area`)

| Endpoint   | Phương thức | Mô tả                         | Request Body (JSON) | Response                               |
|------------|-------------|-------------------------------|--------------------|--------------------------------------|
| `/all`     | GET         | Lấy danh sách tất cả khu vực bãi đỗ xe | Không có           | Danh sách `ParkingAreaResponse` hoặc lỗi không tìm thấy dữ liệu |

---

#### 4.2.9 ImageController (`/img`)

| Endpoint  | Phương thức | Mô tả                        | Request Body (JSON) | Response                               |
|-----------|-------------|------------------------------|--------------------|--------------------------------------|
| `/all`    | GET         | Lấy danh sách tất cả ảnh đã lưu | Không có           | Danh sách `ImageResponse` hoặc lỗi 404 nếu không tìm thấy ảnh |



## 5. Frontend (Android App)

### 5.1 Cài đặt và chạy app

- Cài đặt Android Studio phiên bản mới nhất.
- Mở thư mục `s_parking_app` bằng Android Studio.
- Kiểm tra cấu hình API backend (URL base) trong file cấu hình, ví dụ:  
  `Constants.java` hoặc `BuildConfig.java`, thay đổi URL nếu backend chạy trên máy khác.
- Kết nối thiết bị thật hoặc giả lập Android.
- Build và Run app.

---

### 5.2 Mô tả chức năng chính

- **Đăng nhập/Đăng ký:** Người dùng tạo tài khoản hoặc đăng nhập vào hệ thống.
- **Xem bãi đỗ xe:** Danh sách và chi tiết các bãi đỗ, số lượng chỗ còn trống.
- **Đặt chỗ gửi xe:** Chọn bãi đỗ, thời gian gửi, tạo đặt chỗ.
- **Thanh toán:** Hỗ trợ thanh toán qua các cổng MoMo, ZaloPay tích hợp trực tiếp trong app.
- **Quản lý phiên gửi xe:** Theo dõi trạng thái gửi xe, bắt đầu, kết thúc phiên gửi.
- **Cập nhật trạng thái bãi xe:** Theo dõi trạng thái bãi xe theo thời gian
- **Thông báo:** Nhận thông báo về trạng thái đặt chỗ, thanh toán, nhắc nhở.
- **Lịch sử:** Xem lại các đặt chỗ, giao dịch đã thực hiện.
- **Đổi chủ đề và ngôn nhữ:** Hỗ trợ 2 ngôn ngữ Anh - Việt, 2 mode chủ đề sáng và tối

---

## 6. Hướng dẫn sử dụng

1. Khởi động backend, đảm bảo database đã sẵn sàng và backend chạy ổn định.
2. Mở app Android trên thiết bị.
3. Đăng ký tài khoản mới hoặc đăng nhập bằng tài khoản có sẵn.
4. Xem danh sách bãi đỗ xe và đặt chỗ phù hợp.
5. Thanh toán qua MoMo hoặc ZaloPay theo hướng dẫn.
6. Quản lý phiên gửi xe từ lúc gửi đến khi nhận xe.
7. Xem thông báo và lịch sử giao dịch trong app.

---

## 7. Ghi chú quan trọng

- Cần cài đặt và cấu hình đúng các key API cho MoMo, ZaloPay trong backend và frontend.
- Backend sử dụng JWT cho bảo mật, mỗi request yêu cầu có header `Authorization: Bearer <token>`.
- Tài liệu API có thể được kiểm thử qua Swagger UI (nếu tích hợp), thường tại `http://localhost:8080/swagger-ui.html`.
- App yêu cầu kết nối internet để đồng bộ dữ liệu và thanh toán trực tuyến.
- Nếu thay đổi cổng hoặc địa chỉ backend, nhớ cập nhật URL base trong app.
- Phần backend có thể cần cấu hình bảo mật firewall, CORS phù hợp với môi trường deploy.
