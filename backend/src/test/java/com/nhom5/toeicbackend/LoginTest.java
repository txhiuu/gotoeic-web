package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiem thu API dang nhap POST /api/auth/login qua tang HTTP (MockMvc).
 * Du lieu lay tu src/test/resources/data.sql (CSDL H2 in-memory).
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("testLoginSuccess: dung email + mat khau -> 200, success=true, tra ve vai tro")
    void testLoginSuccess() throws Exception {
        String body = """
                {"email": "student@gmail.com", "password": "123456"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.user.email").value("student@gmail.com"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    @DisplayName("testLoginFail: sai mat khau -> 401, success=false, co thong bao loi")
    void testLoginFail() throws Exception {
        // other@gmail.com co mat khau rieng; "123456" phai bi tu choi
        // (truoc day "123456" dang nhap duoc moi tai khoan)
        String body = """
                {"email": "other@gmail.com", "password": "123456"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sai tài khoản hoặc mật khẩu!"))
                .andExpect(jsonPath("$.user").doesNotExist());
    }
}
