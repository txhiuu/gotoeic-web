package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Kiểm thử API lịch sử làm bài GET/POST /api/history (ExamResultController). */
@SpringBootTest
@AutoConfigureMockMvc
class ExamResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private org.springframework.test.web.servlet.ResultActions postHistory(String json) throws Exception {
        return mockMvc.perform(post("/api/history").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    @DisplayName("TC_HIS_01: POST hợp lệ -> 201, rồi GET thấy bản ghi mới nhất")
    void testLuuVaXemLichSu() throws Exception {
        postHistory("""
                {"userId": 2, "examId": 1, "totalCorrect": 1, "totalQuestions": 2, "score": 5.0}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.score").value(5.0));

        mockMvc.perform(get("/api/history").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history[0].score").value(5.0))
                .andExpect(jsonPath("$.history[0].totalCorrect").value(1))
                .andExpect(jsonPath("$.history[0].examTitle").value("De thi thu TOEIC Full Test 01"));
    }

    @Test
    @DisplayName("TC_HIS_02: POST không gửi score -> tự tính theo thang 10")
    void testTuTinhDiem() throws Exception {
        postHistory("""
                {"userId": "2", "examId": "1", "totalCorrect": 1, "totalQuestions": 3}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(3.3));
    }

    @Test
    @DisplayName("TC_HIS_03: POST thiếu trường / số câu sai / score ngoài 0-10 -> 400")
    void testDuLieuKhongHopLe() throws Exception {
        postHistory("{\"userId\": 2, \"examId\": 1}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value(false));
        postHistory("{\"userId\": 2, \"examId\": 1, \"totalCorrect\": 5, \"totalQuestions\": 2}")
                .andExpect(status().isBadRequest());
        postHistory("{\"userId\": 2, \"examId\": 1, \"totalCorrect\": 1, \"totalQuestions\": 2, \"score\": 11}")
                .andExpect(status().isBadRequest());
        postHistory("{\"userId\": \"abc\", \"examId\": 1, \"totalCorrect\": 1, \"totalQuestions\": 2}")
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC_HIS_04: userId / examId / id lịch sử không tồn tại -> 404")
    void testKhongTonTai() throws Exception {
        postHistory("{\"userId\": 999, \"examId\": 1, \"totalCorrect\": 1, \"totalQuestions\": 2}")
                .andExpect(status().isNotFound());
        postHistory("{\"userId\": 2, \"examId\": 999, \"totalCorrect\": 1, \"totalQuestions\": 2}")
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/history").param("userId", "999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/history/999999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TC_HIS_05: GET /api/history/{id} trả về chi tiết bản ghi mẫu")
    void testXemChiTiet() throws Exception {
        mockMvc.perform(get("/api/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.userId").value(2))
                .andExpect(jsonPath("$.result.examTitle").value("De thi thu TOEIC Full Test 01"));
    }
}
