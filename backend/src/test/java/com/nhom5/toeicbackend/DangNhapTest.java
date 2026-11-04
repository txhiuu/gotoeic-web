package com.nhom5.toeicbackend;

import com.nhom5.toeicbackend.controller.DangNhapController;
import com.nhom5.toeicbackend.service.DangNhapService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho Use Case 1: Đăng nhập
 * Khớp với Sequence Diagram 1: DangNhapController, DangNhapService
 */
@SpringBootTest
public class DangNhapTest {

    @Autowired
    private DangNhapController dangNhapController;

    @Autowired
    private DangNhapService dangNhapService;

    @Test
    @DisplayName("TC_DN_01: Đăng nhập thành công với tài khoản Admin trả về vai trò ADMIN")
    void testDangNhapAdminThanhCong() {
        Map<String, String> req = new HashMap<>();
        req.put("email", "admin@gmail.com");
        req.put("matKhau", "123456");

        ResponseEntity<?> response = dangNhapController.kiemTraDangNhap(req);
        assertEquals(200, response.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("ADMIN", body.get("vaiTro"));
    }

    @Test
    @DisplayName("TC_DN_02: Đăng nhập thành công với tài khoản Sinh viên trả về vai trò STUDENT")
    void testDangNhapStudentThanhCong() {
        Map<String, String> req = new HashMap<>();
        req.put("email", "student@gmail.com");
        req.put("matKhau", "123456");

        ResponseEntity<?> response = dangNhapController.kiemTraDangNhap(req);
        assertEquals(200, response.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("STUDENT", body.get("vaiTro"));
    }

    @Test
    @DisplayName("TC_DN_03: Đăng nhập thất bại khi sai mật khẩu")
    void testDangNhapSaiMatKhau() {
        Map<String, String> req = new HashMap<>();
        req.put("email", "student@gmail.com");
        req.put("matKhau", "sai_mat_khau_123");

        ResponseEntity<?> response = dangNhapController.kiemTraDangNhap(req);
        assertEquals(401, response.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
    }
}

