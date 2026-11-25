package com.nhom5.toeicbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Total-Count")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Chặn các API Thêm, Sửa, Xóa câu hỏi & đề thi nếu không phải Admin (GET vẫn cho phép)
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns(
                        "/api/questions", "/api/questions/**",
                        "/api/exams", "/api/exams/**",
                        "/api/users", "/api/users/**",
                        "/api/admin", "/api/admin/**"
                );
    }
}
