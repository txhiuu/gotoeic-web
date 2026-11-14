package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiem thu LUONG NGHIEP VU TONG THE cua sinh vien (end-to-end qua HTTP):
 * Dang ky -> Dang nhap -> Xem de thi -> Nop bai -> Ket qua duoc luu DB.
 * Va luong Admin: Dang nhap ADMIN -> them cau hoi -> de thi co them cau.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LuongNghiepVuTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("E2E_01: Sinh vien moi dang ky -> dang nhap -> lam de -> nop bai -> ket qua luu DB")
    void testLuongSinhVienDayDu() throws Exception {
        String email = "e2e_" + System.nanoTime() + "@gmail.com";

        // 1. Dang ky
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pass_e2e\",\"fullName\":\"SV E2E\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 2. Dang ky trung email -> 400
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"x\",\"fullName\":\"X\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // 3. Dang nhap -> lay userId
        String loginJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pass_e2e\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andReturn().getResponse().getContentAsString();
        int userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Integer.class, email);
        org.junit.jupiter.api.Assertions.assertTrue(loginJson.contains("\"id\":" + userId));

        // 4. Xem de thi
        mockMvc.perform(get("/api/exam/info/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.examId").value(1))
                .andExpect(jsonPath("$.questions.length()").value(2));

        // 5. Nop bai
        mockMvc.perform(post("/api/exam/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"maBaiLam\":1,\"userId\":" + userId + ",\"dsDapAnDaChon\":{\"1\":\"A\",\"2\":\"B\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCorrect").value(1))
                .andExpect(jsonPath("$.score").value(5.0));

        // 6. Ket qua da luu dung cho user moi
        int soKetQua = jdbc.queryForObject(
                "SELECT COUNT(*) FROM exam_results WHERE user_id = ? AND exam_id = 1", Integer.class, userId);
        assertEquals(1, soKetQua);
    }

    @Test
    @DisplayName("E2E_02: Tai khoan bi khoa khong dang nhap duoc")
    void testTaiKhoanBiKhoa() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"locked@gmail.com\",\"password\":\"abc123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Tài khoản của bạn đã bị khóa!"));
    }

    @Test
    @DisplayName("E2E_03: Admin dang nhap -> them cau hoi -> de thi co them cau")
    void testLuongAdminThemCauHoi() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@gmail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        int truoc = jdbc.queryForObject("SELECT COUNT(*) FROM questions WHERE exam_id = 1", Integer.class);

        mockMvc.perform(post("/api/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"examId":1,"content":"She _____ to work every day.","optionA":"go",
                                 "optionB":"goes","optionC":"going","optionD":"gone",
                                 "correctAnswer":"B","explanation":"S so it + V(s/es)","part":"Part 5"}
                                """))
                .andExpect(status().isOk());

        assertEquals(truoc + 1,
                jdbc.queryForObject("SELECT COUNT(*) FROM questions WHERE exam_id = 1", Integer.class));

        // Don dep: cac test nop bai khac gia dinh de 1 co dung 2 cau
        jdbc.update("DELETE FROM questions WHERE content = ?", "She _____ to work every day.");
    }
}
