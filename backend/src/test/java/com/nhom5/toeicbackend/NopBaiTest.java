package com.nhom5.toeicbackend;

import com.nhom5.toeicbackend.controller.NopBaiController;
import com.nhom5.toeicbackend.service.NopBaiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho Use Case 2: Nộp bài
 * Khớp với Sequence Diagram 2: NopBaiController, NopBaiService
 */
@SpringBootTest
public class NopBaiTest {

    @Autowired
    private NopBaiController nopBaiController;

    @Autowired
    private NopBaiService nopBaiService;

    @Test
    @DisplayName("TC_NB_01: Lấy thông tin bài làm trả về đúng đề thi và danh sách câu hỏi")
    void testLayThongTinNopBai() {
        ResponseEntity<?> resp = nopBaiController.layThongTinNopBai(1);
        assertEquals(200, resp.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertNotNull(body);
        assertEquals(1, body.get("examId"));
        assertNotNull(body.get("title"));

        @SuppressWarnings("unchecked")
        List<?> questions = (List<?>) body.get("questions");
        assertNotNull(questions);
        assertTrue(questions.size() >= 2, "Đề thi phải có ít nhất 2 câu hỏi");
    }

    @Test
    @DisplayName("TC_NB_02: Nộp bài và chấm điểm tự động, lưu kết quả thành công")
    void testNopBaiVaChamDiem() {
        Map<String, String> dsDapAn = new HashMap<>();
        dsDapAn.put("1", "A"); // Chọn câu 1 là A
        dsDapAn.put("2", "A"); // Chọn câu 2 là A

        Map<String, Object> payload = new HashMap<>();
        payload.put("maBaiLam", 1);
        payload.put("userId", 2); // Nguyễn Văn Sinh Viên
        payload.put("dsDapAnDaChon", dsDapAn);

        ResponseEntity<?> resp = nopBaiController.nopBai(payload);
        assertEquals(200, resp.getStatusCode().value());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) resp.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Integer) body.get("totalCorrect") >= 1, "Số câu đúng phải >= 1");
        assertTrue((Double) body.get("score") > 0.0, "Điểm số phải > 0");
    }
}

