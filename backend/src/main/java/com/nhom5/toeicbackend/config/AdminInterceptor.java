package com.nhom5.toeicbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Cho phép request OPTIONS (Preflight) đi qua để không bị lỗi CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. Cho phép đọc (GET) danh sách/chi tiết câu hỏi và đề thi công khai
        String path = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod())
                && (path.startsWith("/api/questions") || path.startsWith("/api/exams"))) {
            return true;
        }

        // 3. Kiểm tra Session cho các thao tác Thêm / Sửa / Xóa hoặc API quản trị
        HttpSession session = request.getSession(false);
        String role = (session != null) ? (String) session.getAttribute("userRole") : null;

        if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\": false, \"error\": \"Bạn không có quyền truy cập API này!\"}");
            return false;
        }
        return true;
    }
}