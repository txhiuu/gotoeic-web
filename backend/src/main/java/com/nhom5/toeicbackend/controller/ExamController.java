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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> exams = new ArrayList<>();
        for (Exam exam : examDAO.findAll()) {
            exams.add(toResponse(exam));
        }
        return exams;
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
}
