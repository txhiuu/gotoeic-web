package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.NopBaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Controller phục vụ thi thử và nộp bài cho Frontend
 * Sử dụng tầng nghiệp vụ NopBaiService
 */
@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private NopBaiService nopBaiService;

    public ExamController() {}

    public ExamController(NopBaiService nopBaiService) {
        this.nopBaiService = nopBaiService;
    }

    /**
     * Khớp với Sequence Diagram 2: Bước 2. layThongTinNopBai(maBaiLam)
     */
    @GetMapping("/info/{maBaiLam}")
    public ResponseEntity<?> layThongTinNopBai(@PathVariable("maBaiLam") int maBaiLam) {
        Map<String, Object> data = nopBaiService.layThongTinNopBai(maBaiLam);
        return ResponseEntity.ok(data);
    }

    /**
     * Khớp với Sequence Diagram 2: Bước 8-12. nopBai(maBaiLam, dsDapAnDaChon)
     */
    @PostMapping("/submit")
    public ResponseEntity<?> nopBai(@RequestBody Map<String, Object> payload) {
        int maBaiLam = (Integer) payload.getOrDefault("maBaiLam", 1);
        int userId = (Integer) payload.getOrDefault("userId", 2);

        @SuppressWarnings("unchecked")
        Map<String, String> dsDapAnDaChon = (Map<String, String>) payload.getOrDefault("dsDapAnDaChon", Collections.emptyMap());

        Map<String, Object> result = nopBaiService.nopBai(maBaiLam, userId, dsDapAnDaChon);
        return ResponseEntity.ok(result);
    }
}
