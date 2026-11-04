package com.nhom5.toeicbackend;

import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.dao.UserDAO;
import com.nhom5.toeicbackend.controller.AuthController;
import com.nhom5.toeicbackend.controller.ExamController;
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
    private ExamController examController;

    // TEST CASE 1: Kiểm tra đăng nhập
    @Test
    @DisplayName("Test case 1 (GitHub): Kiểm tra đăng nhập với tài khoản admin")
    void testLoginSuccess() {
        boolean result = userDAO.checkLogin("admin@gmail.com", "123456");
        assertTrue(result, "Đăng nhập phải thành công với tài khoản admin");
    }

    // TEST CASE 2: Kiểm tra lấy danh sách câu hỏi
    @Test
    @DisplayName("Test case 2 (GitHub): Kiểm tra lấy danh sách câu hỏi (>= 2 câu)")
    void testGetAllQuestions() {
        // Vì trong DB mẫu có sẵn câu hỏi lúc nạp SQL, nên list trả về phải >= 2
        assertTrue(questionDAO.getAllQuestions().size() >= 2, "Danh sách câu hỏi phải có ít nhất 2 câu");
    }

    @Test
    @DisplayName("Test case 3: Kiểm tra API Đăng ký tài khoản mới qua AuthController")
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
    @DisplayName("Test case 4: Kiểm tra nộp bài thi và chấm điểm tự động qua ExamController")
    void testSubmitExamApi() {
        Map<String, String> dsDapAn = new HashMap<>();
        dsDapAn.put("1", "A"); // Câu 1: A
        dsDapAn.put("2", "A"); // Câu 2: A

        Map<String, Object> payload = new HashMap<>();
        payload.put("maBaiLam", 1);
        payload.put("userId", 2);
        payload.put("dsDapAnDaChon", dsDapAn);

        ResponseEntity<?> resp = examController.nopBai(payload);
        assertEquals(200, resp.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Integer) body.get("totalCorrect") >= 1);
    }
}
