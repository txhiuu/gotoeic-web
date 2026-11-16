# Tài liệu kiểm thử – GoToeic (Nhóm 5)

## 1. Chạy test tự động

```bash
cd backend
./mvnw test          # Windows: .\mvnw.cmd test
```

Kết quả mong đợi: `Tests run: 25, Failures: 0, Errors: 0` và **BUILD SUCCESS**.

Test dùng CSDL **H2 in-memory** (chế độ tương thích PostgreSQL) nên **không cần cài PostgreSQL**
và không đụng vào dữ liệu thật. Cấu hình nằm trong `backend/src/test/resources/`:

| File | Vai trò |
|---|---|
| `application.properties` | Trỏ datasource sang H2 (chỉ dùng khi chạy test) |
| `schema.sql` | Tạo 5 bảng `users, exams, user_exam, questions, exam_results` |
| `data.sql` | Dữ liệu mẫu: admin, student, 1 tài khoản bị khóa, 1 tài khoản mật khẩu riêng, đề 1 có 2 câu (đáp án đúng đều là A) |

## 2. Danh sách test case

### 2.1 Đăng nhập – `LoginTest` (gọi HTTP thật qua MockMvc)

| ID | Tên | Input | Kết quả mong đợi |
|---|---|---|---|
| TC_LG_01 | `testLoginSuccess` | `student@gmail.com` / `123456` | 200, `success=true`, `role=STUDENT`, trả về `user` |
| TC_LG_02 | `testLoginFail` | `other@gmail.com` / `123456` (sai mật khẩu) | 401, `success=false`, message “Sai tài khoản hoặc mật khẩu!” |

### 2.2 Nộp bài & lưu kết quả – `NopBaiApiTest`

| ID | Kịch bản | Kết quả mong đợi |
|---|---|---|
| TC_API_NB_01 | Chọn `{1:A, 2:a}` (đúng hết, chữ thường) | 10 điểm, 2/2; **thêm đúng 1 dòng** `exam_results` với score=10, total_correct=2 |
| TC_API_NB_02 | Chọn `{1:A, 2:C}` | 5 điểm, 1/2; dòng mới nhất trong DB khớp |
| TC_API_NB_03 | Nộp bài trắng `{}` | 0 điểm, vẫn lưu kết quả |

### 2.3 Luồng nghiệp vụ tổng thể – `LuongNghiepVuTest`

| ID | Luồng | Các bước kiểm tra |
|---|---|---|
| E2E_01 | Sinh viên mới | Đăng ký → đăng ký trùng (400) → đăng nhập → xem đề 1 (2 câu) → nộp bài (1/2, 5 điểm) → DB có đúng 1 kết quả cho user mới |
| E2E_02 | Tài khoản bị khóa | Đăng nhập đúng mật khẩu nhưng `is_active=false` → 401 “Tài khoản của bạn đã bị khóa!” |
| E2E_03 | Admin | Đăng nhập ADMIN → thêm câu hỏi → số câu của đề 1 tăng 1 |

### 2.5 API Đề thi / Nộp bài – `BackendTest`

| ID | Tên | Kiểm tra |
|---|---|---|
| TC_BE_01 | `testLayThongTinDeThi` | `GET /api/exam/info/1` → 200, đúng tên đề, 120 phút, 2 câu hỏi có nội dung và đáp án |
| TC_BE_02 | `testNopBaiVaLuuLichSu` | `POST /api/exam/submit` đúng 2/2 → 10 điểm; sau đó `GET /api/history?userId=2` có thêm đúng 1 lần, lần mới nhất ở đầu, đúng điểm và tên đề |

### 2.6 API Lịch sử làm bài – `ExamResultControllerTest`

| ID | Kịch bản | Mong đợi |
|---|---|---|
| TC_HIS_01 | POST hợp lệ rồi GET | 201; GET thấy bản ghi mới nhất ở đầu |
| TC_HIS_02 | POST không gửi `score` (1/3 câu) | Tự tính 3.3 điểm |
| TC_HIS_03 | Thiếu trường / totalCorrect > totalQuestions / score > 10 / userId không phải số | 400 |
| TC_HIS_04 | userId, examId hoặc id lịch sử không tồn tại | 404 |
| TC_HIS_05 | `GET /api/history/1` | 200, đúng chi tiết bản ghi mẫu |

**Đặc tả API lịch sử**

| Method | URL | Body / Tham số | Trả về |
|---|---|---|---|
| GET | `/api/history?userId=2` | `userId` (bắt buộc) | `{success, userId, total, history:[{id, examId, examTitle, score, totalCorrect, totalQuestions, submittedAt}]}`, mới nhất trước |
| GET | `/api/history/{id}` | – | `{success, result:{...}}` hoặc 404 |
| POST | `/api/history` | `{"userId":2,"examId":1,"totalCorrect":1,"totalQuestions":2,"score":5.0}` (`score` không bắt buộc) | 201 `{success, message, ...}`; 400 nếu dữ liệu sai; 404 nếu user/đề không tồn tại |

### 2.4 Tài liệu API – `SwaggerDocTest`
`/v3/api-docs` trả về đặc tả OpenAPI có các API chính.

## 3. Tài liệu API

- **Swagger UI**: chạy backend rồi mở <http://localhost:8080/swagger-ui.html>
- **File spec tĩnh**: [`openapi.json`](openapi.json) (import được vào Postman / Swagger Editor)
- **Postman Collection**: [`GoToeic.postman_collection.json`](GoToeic.postman_collection.json)
  – Postman → Import → chọn file → **Run collection** (chạy theo thứ tự, mỗi request có sẵn test script).
  Biến: `baseUrl` (mặc định `http://localhost:8080`), `userId` (tự lấy sau bước Login), `examId`.

## 4. Checklist test thủ công trên giao diện (cho cả nhóm)

| # | Bước | Mong đợi | Người test | Kết quả |
|---|---|---|---|---|
| 1 | Mở `frontend/index.html`, đăng ký tài khoản mới | Báo đăng ký thành công | | |
| 2 | Đăng ký lại cùng email | Báo email đã tồn tại | | |
| 3 | Đăng nhập sai mật khẩu | Báo sai tài khoản/mật khẩu, không vào được | | |
| 4 | Đăng nhập đúng (student) | Vào trang sinh viên | | |
| 5 | Vào làm đề thi 1 | Hiển thị đủ câu hỏi, đồng hồ 120 phút | | |
| 6 | Chọn đáp án, nộp bài | Hiển thị điểm, số câu đúng | | |
| 7 | Kiểm tra bảng `exam_results` trong pgAdmin | Có dòng mới đúng user_id/exam_id/score | | |
| 8 | Đăng nhập admin → `admin.html` | Vào trang quản trị | | |
| 9 | Thêm / sửa / xóa câu hỏi | Danh sách cập nhật đúng | | |
| 10 | Đăng nhập student vào `admin.html` | Không được truy cập | | |

## 5. Lỗi phát hiện khi kiểm thử

| # | Mức độ | Mô tả | Trạng thái |
|---|---|---|---|
| 1 | **Nghiêm trọng** | `DangNhapServiceImpl`: mật khẩu `123456` đăng nhập được **mọi** tài khoản (`|| matKhau.equals("123456")`) | **Đã sửa**, có `testLoginFail` bảo vệ |
| 2 | **Nghiêm trọng** | Response đăng nhập trả về object `user` có cả trường `password` | Chưa sửa – nên thêm `@JsonIgnore` cho `getPassword()` hoặc trả DTO |
| 3 | Cao | API nộp bài nhận `userId` từ client, không xác thực → ai cũng nộp bài thay người khác được | Chưa sửa (cần session/JWT) |
| 4 | Cao | API câu hỏi (thêm/sửa/xóa) không kiểm tra quyền ADMIN | Chưa sửa |
| 5 | Trung bình | Nộp bài cho đề không có câu hỏi → service tự gán `totalQuestions = 10` | Chưa sửa |
| 6 | Trung bình | Gửi `maBaiLam`/`userId` dạng chuỗi (`"1"`) → `ClassCastException`, lỗi 500 | Chưa sửa |
| 7 | Thấp | Công thức `scoreToeic = đúng × 85 + 100` không theo thang TOEIC thực tế (nộp trắng vẫn 100 điểm) | Cần nhóm thống nhất |
| 8 | Thấp | Mật khẩu lưu dạng text thô trong DB | Nên mã hóa BCrypt |
