package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.DangNhapService;
import jakarta.servlet.http.HttpSession; // Thêm import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
public class AuthController {

    @Autowired
    private DangNhapService dangNhapService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String password = request.get("password") != null ? request.get("password") : request.get("matKhau");

        Map<String, Object> result = dangNhapService.kiemTraDangNhap(email, password);
        boolean success = Boolean.TRUE.equals(result.get("success"));

        if (success) {
            // LƯU THÔNG TIN VÀO SESSION
            session.setAttribute("userRole", result.get("role"));
            session.setAttribute("userEmail", email);
            // Có thể lấy userId từ result nếu có
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
    }

    // Thêm API Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Xóa toàn bộ session
        return ResponseEntity.ok(Map.of("success", true, "message", "Đăng xuất thành công"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password") != null ? request.get("password") : request.get("matKhau");
        String fullName = request.get("fullName") != null ? request.get("fullName") : request.get("full_name");

        Map<String, Object> result = dangNhapService.dangKy(email, password, fullName);
        boolean success = Boolean.TRUE.equals(result.get("success"));

        if (success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(400).body(result);
        }
    }
}
