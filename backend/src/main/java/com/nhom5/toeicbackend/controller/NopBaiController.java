package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.NopBaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
public class NopBaiController {

    @Autowired
    private NopBaiService nopBaiService;

    public NopBaiController() {}

    public NopBaiController(NopBaiService nopBaiService) {
        this.nopBaiService = nopBaiService;
    }

    @GetMapping("/info/{examId}")
    public ResponseEntity<?> layThongTinNopBai(@PathVariable("examId") int examId) {
        Map<String, Object> data = nopBaiService.layThongTinNopBai(examId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> nopBai(@RequestBody Map<String, Object> payload) {
        try {
            // Đọc được cả 2 key: examId / maBaiLam
            Object examIdObj = payload.get("examId") != null ? payload.get("examId") : payload.get("maBaiLam");
            int examId = examIdObj != null ? (Integer) examIdObj : 1;
            
            int userId = (Integer) payload.getOrDefault("userId", 2);

            // Đọc được cả 2 key: answers / dsDapAnDaChon
            Object answersObj = payload.get("answers") != null ? payload.get("answers") : payload.get("dsDapAnDaChon");
            @SuppressWarnings("unchecked")
            Map<String, String> answers = answersObj != null ? (Map<String, String>) answersObj : Collections.emptyMap();

            Map<String, Object> result = nopBaiService.nopBai(examId, userId, answers);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}