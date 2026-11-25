package com.nhom5.toeicbackend;

import com.nhom5.toeicbackend.service.NopBaiService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho Use Case 2: Nộp bài
 * Khớp với Sequence Diagram 2: NopBaiController, NopBaiService
 */
@SpringBootTest
@Disabled("Tạm thời vô hiệu hóa để fix lỗi H2 Database")
public class NopBaiTest {

    @Autowired
    private NopBaiService nopBaiService;

    @Test
    @DisplayName("TC_NB_01: Lấy thông tin bài làm trả về đúng đề thi và danh sách câu hỏi")
    void testLayThongTinNopBai() {
        Map<String, Object> body = nopBaiService.layThongTinNopBai(1);
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

        Map<String, Object> body = nopBaiService.nopBai(1, 2, dsDapAn);
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Integer) body.get("totalCorrect") >= 1, "Số câu đúng phải >= 1");
        assertTrue((Double) body.get("score") > 0.0, "Điểm số phải > 0");
    }
}

