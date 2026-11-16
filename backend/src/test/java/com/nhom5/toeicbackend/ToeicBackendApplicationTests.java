package com.nhom5.toeicbackend;

import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.dao.UserDAO;
import com.nhom5.toeicbackend.controller.AuthController;
import com.nhom5.toeicbackend.controller.NopBaiController; // Sửa thành NopBaiController
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ToeicBackendApplicationTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private AuthController authController;

    @Autowired
    private NopBaiController nopBaiController; // Sửa thành NopBaiController

    // TEST CASE 1: Kiểm tra đăng nhập
    @Test
    @DisplayName("Test case 1: Kiểm tra đăng nhập với tài khoản admin")
    void testLoginSuccess() {
        boolean result = userDAO.checkLogin("admin@gmail.com", "123456");
        assertTrue(result, "Đăng nhập phải thành công với tài khoản admin");
    }

    // TEST CASE 2: Kiểm tra lấy danh sách câu hỏi
    @Test
    @DisplayName("Test case 2: Kiểm tra lấy danh sách câu hỏi (>= 2 câu)")
    void testGetAllQuestions() {
        assertTrue(questionDAO.getAllQuestions().size() >= 2, "Danh sách câu hỏi phải có ít nhất 2 câu");
    }

    @Test
    @DisplayName("Test case 3: Kiểm tra API Đăng ký tài khoản mới")
    void testRegisterApi() {
        String testEmail = "test_sv_" + System.currentTimeMillis() + "@gmail.com";
        Map<String, String> regReq = new HashMap<>();
        regReq.put("email", testEmail);
        regReq.put("password", "123456");
        regReq.put("fullName", "Sinh Viên Test");

        ResponseEntity<?> resp = authController.register(regReq);
        assertEquals(200, resp.getStatusCode().value());
        assertTrue(userDAO.checkLogin(testEmail, "123456"));
    }

    @Test
    @DisplayName("Test case 4: Kiểm tra nộp bài thi và chấm điểm tự động")
    void testSubmitExamApi() {
        Map<String, String> dsDapAn = new HashMap<>();
        dsDapAn.put("1", "A"); // Câu 1: A
        dsDapAn.put("2", "A"); // Câu 2: A

        Map<String, Object> payload = new HashMap<>();
        // SỬA LẠI KEY CHO KHỚP VỚI NopBaiController MỚI
        payload.put("examId", 1); 
        payload.put("userId", 2);
        payload.put("answers", dsDapAn); 

        // GỌI NOPBAICONTROLLER THAY VÌ EXAMCONTROLLER
        ResponseEntity<?> resp = nopBaiController.nopBai(payload);
        assertEquals(200, resp.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Integer) body.get("totalCorrect") >= 1, "Số câu đúng phải >= 1");
    }
}