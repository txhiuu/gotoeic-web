package com.nhom5.toeicbackend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm thử API Đề thi, phân trang và nộp bài.
 * Dữ liệu mẫu (src/test/resources/data.sql): đề 1 có 2 câu, đáp án đúng đều là "A";
 * học viên student@gmail.com có id = 2.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BackendTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("GET /api/exams trả về danh sách đề thi")
    void testLayDanhSachDeThi() throws Exception {
        mockMvc.perform(get("/api/exams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("De thi thu TOEIC Full Test 01"));
    }

    @Test
    @DisplayName("GET /api/exams hỗ trợ tìm kiếm và phân trang")
    void testTimKiemVaPhanTrangDeThi() throws Exception {
        mockMvc.perform(get("/api/exams")
                        .param("page", "1")
                        .param("size", "5")
                        .param("search", "toeic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        mockMvc.perform(get("/api/exams").param("search", "khong co"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/exams").param("page", "2").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/exams").param("page", "0").param("size", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC_STATS_01: API thống kê trả số lượng khớp với dữ liệu hiện có")
    void testThongKeDashboardAdmin() throws Exception {
        long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        long totalQuestions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        long totalExams = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exams", Long.class);

        MockHttpSession adminSession = loginAs("admin@gmail.com", "123456");
        mockMvc.perform(get("/api/admin/statistics").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(totalUsers))
                .andExpect(jsonPath("$.totalQuestions").value(totalQuestions))
                .andExpect(jsonPath("$.totalExams").value(totalExams));
    }

    @Test
    @DisplayName("TC_STATS_02: API thống kê cập nhật số lượng khi thêm dữ liệu")
    void testThongKeDashboardSauKhiThemDuLieu() throws Exception {
        long totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        long totalQuestions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        long totalExams = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exams", Long.class);

        String uniqueValue = "DashboardStats-" + System.nanoTime();
        String email = uniqueValue + "@example.com";
        String questionContent = uniqueValue + " question";

        try {
            jdbcTemplate.update(
                    "INSERT INTO users (email, password, full_name, role, is_active) VALUES (?, ?, ?, 'STUDENT', true)",
                    email, "test-password", "Dashboard statistics test");
            jdbcTemplate.update(
                    "INSERT INTO exams (title, description, duration_minutes) VALUES (?, ?, ?)",
                    uniqueValue, "Dashboard statistics test", 120);
            jdbcTemplate.update(
                    "INSERT INTO questions (exam_id, content, option_a, option_b, option_c, option_d, correct_answer, explanation, part) "
                            + "VALUES (1, ?, 'A', 'B', 'C', 'D', 'A', 'Test', 'Part 5')",
                    questionContent);

            MockHttpSession adminSession = loginAs("admin@gmail.com", "123456");
            mockMvc.perform(get("/api/admin/statistics").session(adminSession))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(totalUsers + 1))
                    .andExpect(jsonPath("$.totalQuestions").value(totalQuestions + 1))
                    .andExpect(jsonPath("$.totalExams").value(totalExams + 1));
        } finally {
            jdbcTemplate.update("DELETE FROM questions WHERE content = ?", questionContent);
            jdbcTemplate.update("DELETE FROM users WHERE email = ?", email);
            jdbcTemplate.update("DELETE FROM exams WHERE title = ?", uniqueValue);
        }
    }

    @Test
    @DisplayName("TC_BE_03: GET /api/exams trả đúng số đề ở trang đầu")
    void testPhanTrangDeThiTrangDau() throws Exception {
        String prefix = taoDuLieuDeThiPhanTrang();
        try {
            mockMvc.perform(get("/api/exams")
                            .param("page", "1")
                            .param("size", "2")
                            .param("search", prefix))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-Total-Count", "3"))
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value(prefix + " 1"))
                    .andExpect(jsonPath("$[1].title").value(prefix + " 2"));
        } finally {
            xoaDuLieuDeThiPhanTrang(prefix);
        }
    }

    @Test
    @DisplayName("TC_BE_04: GET /api/exams trả phần còn lại ở trang kế tiếp")
    void testPhanTrangDeThiTrangKeTiep() throws Exception {
        String prefix = taoDuLieuDeThiPhanTrang();
        try {
            mockMvc.perform(get("/api/exams")
                            .param("page", "2")
                            .param("size", "2")
                            .param("search", prefix))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value(prefix + " 3"));
        } finally {
            xoaDuLieuDeThiPhanTrang(prefix);
        }
    }

    @Test
    @DisplayName("GET /api/exams/{id} trả về thông tin đề thi kèm mảng câu hỏi")
    void testLayChiTietDeThiKemCauHoi() throws Exception {
        mockMvc.perform(get("/api/exams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].content").isNotEmpty())
                .andExpect(jsonPath("$.questions[0].correctAnswer").doesNotExist());

        mockMvc.perform(get("/api/exams/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

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
        MockHttpSession studentSession = loginAs("student@gmail.com", "123456");
        // Số lần làm bài trước khi nộp
        String truoc = mockMvc.perform(get("/api/history").session(studentSession).param("userId", "2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        int soLanTruoc = JsonPath.read(truoc, "$.total");

        // Nộp bài: đúng cả 2 câu
        mockMvc.perform(post("/api/exam/submit")
                        .session(studentSession)
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
        String sau = mockMvc.perform(get("/api/history").session(studentSession).param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history[0].examId").value(1))
                .andExpect(jsonPath("$.history[0].examTitle").value("De thi thu TOEIC Full Test 01"))
                .andExpect(jsonPath("$.history[0].score").value(10.0))
                .andExpect(jsonPath("$.history[0].totalCorrect").value(2))
                .andExpect(jsonPath("$.history[0].submittedAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        assertEquals(soLanTruoc + 1, (int) JsonPath.read(sau, "$.total"));
    }

    @Test
    @DisplayName("Nộp bài cần phiên đăng nhập và ít nhất một đáp án; không cho giả mạo userId")
    void testNopBaiValidatesSessionAndAnswers() throws Exception {
        String body = """
                {"examId":1,"dsDapAnDaChon":{"1":"A","2":"-"}}
                """;
        mockMvc.perform(post("/api/exam/submit").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());

        MockHttpSession studentSession = loginAs("student@gmail.com", "123456");
        mockMvc.perform(post("/api/exam/submit").session(studentSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"examId\":1,\"userId\":1,\"dsDapAnDaChon\":{\"1\":\"A\"}}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/exam/submit").session(studentSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"examId\":1,\"dsDapAnDaChon\":{\"1\":\"-\",\"2\":\"-\"}}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/exam/submit").session(studentSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctAnswers['1']").value("A"));
    }

    private String taoDuLieuDeThiPhanTrang() {
        String prefix = "Pagination-" + System.nanoTime();
        for (int i = 1; i <= 3; i++) {
            jdbcTemplate.update(
                    "INSERT INTO exams (title, description, duration_minutes) VALUES (?, ?, ?)",
                    prefix + " " + i, "Du lieu kiem thu phan trang", 120);
        }
        return prefix;
    }

    private void xoaDuLieuDeThiPhanTrang(String prefix) {
        jdbcTemplate.update("DELETE FROM exams WHERE title LIKE ?", prefix + "%");
    }

    private MockHttpSession loginAs(String email, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession(false);
    }
}
