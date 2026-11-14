package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiem thu API nop bai POST /api/exam/submit va viec LUU KET QUA vao bang exam_results.
 * De 1 co 2 cau, dap an dung deu la "A".
 */
@SpringBootTest
@AutoConfigureMockMvc
class NopBaiApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    private int demKetQua(int userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM exam_results WHERE user_id = ?", Integer.class, userId);
    }

    private Map<String, Object> ketQuaMoiNhat(int userId) {
        return jdbc.queryForMap(
                "SELECT * FROM exam_results WHERE user_id = ? ORDER BY id DESC LIMIT 1", userId);
    }

    @Test
    @DisplayName("TC_API_NB_01: Dung het -> 10 diem, 2/2 cau, ket qua duoc luu vao DB")
    void testNopBaiDungHet_LuuKetQua() throws Exception {
        int truoc = demKetQua(2);

        mockMvc.perform(post("/api/exam/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"maBaiLam": 1, "userId": 2, "dsDapAnDaChon": {"1": "A", "2": "a"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.totalCorrect").value(2))
                .andExpect(jsonPath("$.totalQuestions").value(2))
                .andExpect(jsonPath("$.score").value(10.0));

        assertEquals(truoc + 1, demKetQua(2), "Phai co dung 1 ban ghi exam_results moi");
        Map<String, Object> row = ketQuaMoiNhat(2);
        assertEquals(1, ((Number) row.get("exam_id")).intValue());
        assertEquals(10.0, ((Number) row.get("score")).doubleValue());
        assertEquals(2, ((Number) row.get("total_correct")).intValue());
        assertEquals(2, ((Number) row.get("total_questions")).intValue());
    }

    @Test
    @DisplayName("TC_API_NB_02: Dung 1/2 -> 5 diem, luu dung so cau dung")
    void testNopBaiDungMotNua() throws Exception {
        mockMvc.perform(post("/api/nopbai/nopBai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"maBaiLam": 1, "userId": 2, "dsDapAnDaChon": {"1": "A", "2": "C"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCorrect").value(1))
                .andExpect(jsonPath("$.score").value(5.0));

        Map<String, Object> row = ketQuaMoiNhat(2);
        assertEquals(5.0, ((Number) row.get("score")).doubleValue());
        assertEquals(1, ((Number) row.get("total_correct")).intValue());
    }

    @Test
    @DisplayName("TC_API_NB_03: Nop bai trang -> 0 diem, van luu ket qua")
    void testNopBaiTrang() throws Exception {
        int truoc = demKetQua(2);

        mockMvc.perform(post("/api/exam/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"maBaiLam": 1, "userId": 2, "dsDapAnDaChon": {}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCorrect").value(0))
                .andExpect(jsonPath("$.score").value(0.0));

        assertEquals(truoc + 1, demKetQua(2));
    }
}
