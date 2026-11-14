package com.nhom5.toeicbackend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 2 Test Case JUnit cho API Đề thi / Nộp bài.
 * Dữ liệu mẫu (src/test/resources/data.sql): đề 1 có 2 câu, đáp án đúng đều là "A";
 * học viên student@gmail.com có id = 2.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BackendTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("TC_BE_01: GET /api/exam/info/1 trả về đúng thông tin đề thi và danh sách câu hỏi")
    void testLayThongTinDeThi() throws Exception {
        mockMvc.perform(get("/api/exam/info/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.examId").value(1))
                .andExpect(jsonPath("$.title").value("De thi thu TOEIC Full Test 01"))
                .andExpect(jsonPath("$.durationMinutes").value(120))
                .andExpect(jsonPath("$.totalQuestions").value(2))
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].content").isNotEmpty())
                .andExpect(jsonPath("$.questions[0].optionA").value("attend"));
    }

    @Test
    @DisplayName("TC_BE_02: POST /api/exam/submit chấm điểm đúng và kết quả xuất hiện trong GET /api/history")
    void testNopBaiVaLuuLichSu() throws Exception {
        // Số lần làm bài trước khi nộp
        String truoc = mockMvc.perform(get("/api/history").param("userId", "2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        int soLanTruoc = JsonPath.read(truoc, "$.total");

        // Nộp bài: đúng cả 2 câu
        mockMvc.perform(post("/api/exam/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"maBaiLam": 1, "userId": 2, "dsDapAnDaChon": {"1": "A", "2": "A"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.totalCorrect").value(2))
                .andExpect(jsonPath("$.totalQuestions").value(2))
                .andExpect(jsonPath("$.score").value(10.0));

        // Lịch sử có thêm đúng 1 lần, lần mới nhất ở đầu danh sách
        String sau = mockMvc.perform(get("/api/history").param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history[0].examId").value(1))
                .andExpect(jsonPath("$.history[0].examTitle").value("De thi thu TOEIC Full Test 01"))
                .andExpect(jsonPath("$.history[0].score").value(10.0))
                .andExpect(jsonPath("$.history[0].totalCorrect").value(2))
                .andExpect(jsonPath("$.history[0].submittedAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        assertEquals(soLanTruoc + 1, (int) JsonPath.read(sau, "$.total"));
    }
}
