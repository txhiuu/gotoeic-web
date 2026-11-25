package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockHttpSession loginAs(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertNotNull(session);
        return session;
    }

    @Test
    @DisplayName("Chưa đăng nhập hoặc tài khoản STUDENT bị chặn 403 khi Thêm/Sửa/Xóa câu hỏi & đề thi")
    void testUnauthorizedAndStudentForbidden() throws Exception {
        String qBody = """
                {"examId":1,"content":"Test Q","optionA":"A","optionB":"B","optionC":"C","optionD":"D","correctAnswer":"A","part":"Part 5"}
                """;
        String examBody = """
                {"title":"Unauthorized Exam","description":"Desc","durationMinutes":60}
                """;

        // Chưa đăng nhập -> GET công khai OK, ghi bị 403
        mockMvc.perform(get("/api/questions")).andExpect(status().isOk());
        mockMvc.perform(get("/api/exams")).andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/statistics")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/questions").contentType(MediaType.APPLICATION_JSON).content(qBody))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/questions/1").contentType(MediaType.APPLICATION_JSON).content(qBody))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/questions/1"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/exams").contentType(MediaType.APPLICATION_JSON).content(examBody))
                .andExpect(status().isForbidden());

        // Đăng nhập STUDENT -> vẫn bị 403
        MockHttpSession studentSession = loginAs("student@gmail.com", "123456");
        mockMvc.perform(get("/api/admin/statistics").session(studentSession)).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/questions").session(studentSession).contentType(MediaType.APPLICATION_JSON).content(qBody))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/questions/1").session(studentSession).contentType(MediaType.APPLICATION_JSON).content(qBody))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/questions/1").session(studentSession))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/exams").session(studentSession).contentType(MediaType.APPLICATION_JSON).content(examBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin đăng nhập -> Thêm/Sửa/Xóa đề thi và câu hỏi thành công -> Đăng xuất mất quyền")
    void testAdminFullCrudLifecycle() throws Exception {
        MockHttpSession adminSession = loginAs("admin@gmail.com", "123456");

        mockMvc.perform(get("/api/auth/me").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.userId").value(1));
        mockMvc.perform(get("/api/admin/statistics").session(adminSession)).andExpect(status().isOk());

        String examTitle = "CRUD Test Exam " + System.nanoTime();
        String updatedExamTitle = examTitle + " Updated";

        // 1. Tạo đề thi mới
        mockMvc.perform(post("/api/exams")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + examTitle + "\",\"description\":\"Mo ta\",\"durationMinutes\":90}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        Integer newExamId = jdbcTemplate.queryForObject(
                "SELECT id FROM exams WHERE title = ?", Integer.class, examTitle);
        assertNotNull(newExamId);

        // 2. Sửa đề thi
        mockMvc.perform(put("/api/exams/" + newExamId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + updatedExamTitle + "\",\"description\":\"Mo ta moi\",\"durationMinutes\":45}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        String dbExamTitle = jdbcTemplate.queryForObject(
                "SELECT title FROM exams WHERE id = ?", String.class, newExamId);
        assertEquals(updatedExamTitle, dbExamTitle);

        // 3. Thêm câu hỏi vào đề 1, sau đó sửa chuyển sang đề mới (kiểm tra cập nhật cả examId)
        String qContent = "Admin CRUD Q " + System.nanoTime();
        String qUpdatedContent = qContent + " Updated";
        mockMvc.perform(post("/api/questions")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"examId\":1,\"content\":\"" + qContent + "\",\"optionA\":\"a\",\"optionB\":\"b\",\"optionC\":\"c\",\"optionD\":\"d\",\"correctAnswer\":\"b\",\"explanation\":\"exp\",\"part\":\"Part 5\"}"))
                .andExpect(status().isOk());

        Integer newQuestionId = jdbcTemplate.queryForObject(
                "SELECT id FROM questions WHERE content = ?", Integer.class, qContent);
        assertNotNull(newQuestionId);

        mockMvc.perform(put("/api/questions/" + newQuestionId)
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"examId\":" + newExamId + ",\"content\":\"" + qUpdatedContent + "\",\"optionA\":\"a2\",\"optionB\":\"b2\",\"optionC\":\"c2\",\"optionD\":\"d2\",\"correctAnswer\":\"C\",\"explanation\":\"exp2\",\"part\":\"Part 6\"}"))
                .andExpect(status().isOk());

        Integer updatedExamId = jdbcTemplate.queryForObject(
                "SELECT exam_id FROM questions WHERE id = ?", Integer.class, newQuestionId);
        assertEquals(newExamId, updatedExamId);

        // 4. Xóa câu hỏi
        mockMvc.perform(delete("/api/questions/" + newQuestionId).session(adminSession))
                .andExpect(status().isOk());
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM questions WHERE id = ?", Integer.class, newQuestionId));

        // 5. Xóa đề thi
        mockMvc.perform(delete("/api/exams/" + newExamId).session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM exams WHERE id = ?", Integer.class, newExamId));

        // 6. Đăng xuất -> hết quyền
        mockMvc.perform(post("/api/auth/logout").session(adminSession))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/auth/me").session(adminSession))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/questions/1").session(adminSession))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CORS preflight hỗ trợ localhost và 127.0.0.1 với allowCredentials=true")
    void testCorsPreflight() throws Exception {
        for (String origin : new String[]{"http://localhost:5500", "http://127.0.0.1:5500"}) {
            mockMvc.perform(options("/api/auth/login")
                            .header(HttpHeaders.ORIGIN, origin)
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin))
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));

            mockMvc.perform(options("/api/questions/1")
                            .header(HttpHeaders.ORIGIN, origin)
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin))
                    .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
        }
    }
}
