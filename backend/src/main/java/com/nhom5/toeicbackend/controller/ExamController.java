package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search) {
        boolean paginate = page != null || size != null;
        int pageNumber = page == null ? 1 : page;
        int pageSize = size == null ? 5 : size;
        if (pageNumber < 1 || (paginate && pageSize < 1)) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "page phải từ 1 trở lên và size phải lớn hơn 0");
            return ResponseEntity.badRequest().body(response);
        }

        String keyword = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        List<Map<String, Object>> exams = new ArrayList<>();
        for (Exam exam : examDAO.findAll()) {
            if (keyword.isEmpty()
                    || containsIgnoreCase(exam.getTitle(), keyword)
                    || containsIgnoreCase(exam.getDescription(), keyword)) {
                exams.add(toResponse(exam));
            }
        }

        if (paginate) {
            long start = (long) (pageNumber - 1) * pageSize;
            if (start >= exams.size()) {
                return ResponseEntity.ok(List.of());
            }
            int end = (int) Math.min(start + pageSize, exams.size());
            exams = new ArrayList<>(exams.subList((int) start, end));
        }
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable int id) {
        return examDAO.findById(id)
                .map(exam -> {
                    Map<String, Object> response = toResponse(exam);
                    response.put("questions", questionDAO.getCAUHOI(id));
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("success", false);
                    response.put("message", "Không tìm thấy đề thi có id = " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    private Map<String, Object> toResponse(Exam exam) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", exam.getId());
        response.put("title", exam.getTitle());
        response.put("description", exam.getDescription());
        response.put("durationMinutes", exam.getDurationMinutes());
        response.put("createdAt", exam.getCreatedAt());
        return response;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
