package com.nhom5.toeicbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Chặn các API thay đổi dữ liệu câu hỏi (Thêm, Sửa, Xóa) nếu không phải Admin
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/api/questions/**")
                .excludePathPatterns("/api/questions"); // Cho phép GET /api/questions (nếu muốn ai cũng xem được)
        
        // Chặn luôn API xem danh sách user (nếu có)
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/api/users/**");
    }
}