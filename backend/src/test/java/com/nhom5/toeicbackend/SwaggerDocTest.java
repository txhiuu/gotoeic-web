package com.nhom5.toeicbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Dam bao tai lieu Swagger sinh ra dung va co du cac API chinh. */
@SpringBootTest
@AutoConfigureMockMvc
class SwaggerDocTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Swagger: /v3/api-docs tra ve dac ta OpenAPI co cac API chinh")
    void testApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("GoToeic API"))
                .andExpect(jsonPath("$.paths['/api/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/api/exam/submit']").exists())
                .andExpect(jsonPath("$.paths['/api/questions']").exists());
    }
}
