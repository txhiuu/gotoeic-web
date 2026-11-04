package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.DangNhapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller đại diện cho Use Case Đăng nhập
 * Khớp 100% với tên gọi "DangNhapController" trên Sequence Diagram 1
 */
@RestController
@RequestMapping({"/api/dangnhap", "/api/dang-nhap"})
@CrossOrigin(origins = "*")
public class DangNhapController {

    @Autowired
    private DangNhapService dangNhapService;

    public DangNhapController() {}

    public DangNhapController(DangNhapService dangNhapService) {
        this.dangNhapService = dangNhapService;
    }

    /**
     * Bước 4 trên Sequence Diagram 1: kiemTraDangNhap(email, matKhau)
     */
    @PostMapping("/kiemTraDangNhap")
    public ResponseEntity<?> kiemTraDangNhap(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String matKhau = request.get("matKhau") != null ? request.get("matKhau") : request.get("password");

        Map<String, Object> result = dangNhapService.kiemTraDangNhap(email, matKhau);
        boolean success = Boolean.TRUE.equals(result.get("success"));

        if (success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
    }

    /**
     * Đăng ký tài khoản
     */
    @PostMapping("/dangKy")
    public ResponseEntity<?> dangKy(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String matKhau = request.get("password") != null ? request.get("password") : request.get("matKhau");
        String fullName = request.get("fullName") != null ? request.get("fullName") : request.get("full_name");

        Map<String, Object> result = dangNhapService.dangKy(email, matKhau, fullName);
        boolean success = Boolean.TRUE.equals(result.get("success"));

        if (success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

