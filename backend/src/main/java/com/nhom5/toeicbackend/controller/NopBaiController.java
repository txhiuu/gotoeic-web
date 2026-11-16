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

    @PostMapping("/submit") // SỬA LẠI URL THÀNH /submit
    public ResponseEntity<?> nopBai(@RequestBody Map<String, Object> payload) {
        try {
            // SỬA KEY LẤY DỮ LIỆU CHO KHỚP VỚI FRONTEND GỬI LÊN
            int examId = (Integer) payload.getOrDefault("examId", 1);
            int userId = (Integer) payload.getOrDefault("userId", 2);

            @SuppressWarnings("unchecked")
            Map<String, String> answers = (Map<String, String>) payload.getOrDefault("answers", Collections.emptyMap());

            // Gọi Service
            Map<String, Object> result = nopBaiService.nopBai(examId, userId, answers);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}