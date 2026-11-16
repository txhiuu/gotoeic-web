package com.nhom5.toeicbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Tai lieu dac ta API (Swagger / OpenAPI 3).
 * Swagger UI : http://localhost:8080/swagger-ui.html
 * JSON spec  : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI goToeicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GoToeic API")
                        .version("1.0.0")
                        .description("API website on luyen TOEIC - Nhom 5: dang nhap/dang ky, de thi, cau hoi, nop bai & cham diem."))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local")));
    }
}
