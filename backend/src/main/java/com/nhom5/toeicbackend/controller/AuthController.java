package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.model.User;
import com.nhom5.toeicbackend.service.DangNhapService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
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
            session.setAttribute("userEmail", email != null ? email.trim() : null);
            Object userObj = result.get("user");
            if (userObj instanceof User user) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getFullName());
            }
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
    }

    // Kiểm tra trạng thái phiên đăng nhập hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object roleObj = session != null ? session.getAttribute("userRole") : null;
        if (roleObj == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "authenticated", false,
                    "message", "Chưa đăng nhập"
            ));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("authenticated", true);
        body.put("role", roleObj);
        body.put("email", session.getAttribute("userEmail"));
        body.put("userId", session.getAttribute("userId"));
        if (session.getAttribute("userName") != null) {
            body.put("fullName", session.getAttribute("userName"));
        }
        return ResponseEntity.ok(body);
    }

    // Thêm API Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Xóa toàn bộ session
        }
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
