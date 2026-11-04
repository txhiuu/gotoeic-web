package com.nhom5.toeicbackend.service;

import com.nhom5.toeicbackend.model.User;
import java.util.Map;
import java.util.Optional;

/**
 * Tầng nghiệp vụ (Service Interface) cho Use Case Đăng nhập
 * Khớp với Sequence Diagram 1: Đăng nhập
 */
public interface DangNhapService {

    /**
     * Bước 4-7: Kiểm tra email, mật khẩu và trạng thái tài khoản
     * @return Map chứa thông tin người dùng và vai trò (role / vaiTro)
     */
    Map<String, Object> kiemTraDangNhap(String email, String matKhau);

    /**
     * Nghiệp vụ đăng ký tài khoản mới
     */
    Map<String, Object> dangKy(String email, String matKhau, String fullName);

    /**
     * Bước 5: Truy vấn người dùng theo email từ CSDL
     */
    Optional<User> getNGUOIDUNG(String email);
}

