package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.DangNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller phục vụ đăng nhập, đăng ký cho Frontend
 * Sử dụng tầng nghiệp vụ DangNhapService
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
public class AuthController {

    @Autowired
    private DangNhapService dangNhapService;

    public AuthController() {}

    public AuthController(DangNhapService dangNhapService) {
        this.dangNhapService = dangNhapService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password") != null ? request.get("password") : request.get("matKhau");

        Map<String, Object> result = dangNhapService.kiemTraDangNhap(email, password);
        boolean success = Boolean.TRUE.equals(result.get("success"));

        if (success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
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
