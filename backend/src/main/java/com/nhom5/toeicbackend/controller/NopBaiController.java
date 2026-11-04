package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.service.NopBaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Controller đại diện cho Use Case Nộp bài
 * Khớp 100% với tên gọi "NopBaiController" trên Sequence Diagram 2
 */
@RestController
@RequestMapping({"/api/nopbai", "/api/nop-bai"})
@CrossOrigin(origins = "*")
public class NopBaiController {

    @Autowired
    private NopBaiService nopBaiService;

    public NopBaiController() {}

    public NopBaiController(NopBaiService nopBaiService) {
        this.nopBaiService = nopBaiService;
    }

    /**
     * Bước 2 trên Sequence Diagram 2: layThongTinNopBai(maBaiLam)
     */
    @GetMapping("/layThongTinNopBai/{maBaiLam}")
    public ResponseEntity<?> layThongTinNopBai(@PathVariable("maBaiLam") int maBaiLam) {
        Map<String, Object> data = nopBaiService.layThongTinNopBai(maBaiLam);
        return ResponseEntity.ok(data);
    }

    /**
     * Bước 8 trên Sequence Diagram 2: nopBai(maBaiLam, dsDapAnDaChon)
     */
    @PostMapping("/nopBai")
    public ResponseEntity<?> nopBai(@RequestBody Map<String, Object> payload) {
        int maBaiLam = (Integer) payload.getOrDefault("maBaiLam", 1);
        int userId = (Integer) payload.getOrDefault("userId", 2);

        @SuppressWarnings("unchecked")
        Map<String, String> dsDapAnDaChon = (Map<String, String>) payload.getOrDefault("dsDapAnDaChon", Collections.emptyMap());

        Map<String, Object> result = nopBaiService.nopBai(maBaiLam, userId, dsDapAnDaChon);
        return ResponseEntity.ok(result);
    }
}

