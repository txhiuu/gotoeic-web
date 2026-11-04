# GoToeic - Website Ôn Luyện Trắc Nghiệm TOEIC

## 📖 Mô tả dự án
Hệ thống website hỗ trợ sinh viên ôn luyện và thi thử TOEIC trực tuyến (dành cho Sinh viên và Quản trị viên). Dự án sử dụng kiến trúc Client-Server với các công nghệ chính: Java Spring Boot (JDBC), HTML/JS/Bootstrap 5 và cơ sở dữ liệu PostgreSQL.

## ⚙️ Cấu hình cần thiết
1. Database: Tạo database tên là `toeic_web_db` trên pgAdmin 4. Mở Query Tool và chạy toàn bộ file `gotoeic.sql` để tạo bảng.
2. File cấu hình: Vào thư mục `backend/src/main/resources/`, tạo file `application.properties` với 3 dòng sau (thay mật khẩu của bạn vào):
   spring.datasource.url=jdbc:postgresql://localhost:5432/toeic_web_db
   spring.datasource.username=postgres
   spring.datasource.password=YOUR_DB_PASSWORD

## 🚀 Cách chạy dự án
1. Chạy Backend: Mở Terminal tại thư mục `backend`, chạy lệnh `.\mvnw.cmd spring-boot:run` (Windows) hoặc `./mvnw spring-boot:run` (Mac/Linux). Đợi đến khi hiện chữ "Started ToeicBackendApplication".
2. Chạy Frontend: Mở file `frontend/index.html` (dành cho sinh viên) hoặc `frontend/admin.html` (dành cho admin) bằng Live Server hoặc trình duyệt.

## 🔑 Tài khoản test mặc định
- Quản trị viên (Admin): Email: admin@gmail.com | Mật khẩu: 123456
- Sinh viên (Student): Email: student@gmail.com | Mật khẩu: 123456

