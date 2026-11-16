package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.ExamResultDAO;
import com.nhom5.toeicbackend.dao.UserDAO;
import com.nhom5.toeicbackend.model.Exam;
import com.nhom5.toeicbackend.model.ExamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * API Lịch sử làm bài (bảng exam_results).
 *
 *  GET  /api/history?userId={id}  -> danh sách lịch sử của 1 học viên (mới nhất trước)
 *  GET  /api/history/{id}         -> chi tiết 1 lần làm bài
 *  POST /api/history              -> lưu 1 kết quả làm bài
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class ExamResultController {

    @Autowired
    private ExamResultDAO examResultDAO;

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private UserDAO userDAO;

    // ===================== GET: danh sách lịch sử =====================
    @GetMapping
    public ResponseEntity<?> layLichSu(@RequestParam("userId") int userId) {
        if (userDAO.findById(userId).isEmpty()) {
            return loi(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng có id = " + userId);
        }

        List<ExamResult> ketQua = examResultDAO.findByUserId(userId);
        Map<Integer, String> tenDe = new HashMap<>(); // tránh truy vấn lại cùng 1 đề

        List<Map<String, Object>> ds = new ArrayList<>();
        for (ExamResult r : ketQua) {
            String title = tenDe.computeIfAbsent(r.getExamId(),
                    id -> examDAO.findById(id).map(Exam::getTitle).orElse("Đề thi #" + id));
            ds.add(toJson(r, title));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("userId", userId);
        body.put("total", ds.size());
        body.put("history", ds);
        return ResponseEntity.ok(body);
    }

    // ===================== GET: chi tiết 1 lần làm bài =====================
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable("id") int id) {
        Optional<ExamResult> opt = examResultDAO.findById(id);
        if (opt.isEmpty()) {
            return loi(HttpStatus.NOT_FOUND, "Không tìm thấy lịch sử làm bài có id = " + id);
        }
        ExamResult r = opt.get();
        String title = examDAO.findById(r.getExamId()).map(Exam::getTitle).orElse("Đề thi #" + r.getExamId());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("result", toJson(r, title));
        return ResponseEntity.ok(body);
    }

    // ===================== POST: lưu lịch sử =====================
    /**
     * Body: {"userId": 2, "examId": 1, "totalCorrect": 1, "totalQuestions": 2, "score": 5.0}
     * "score" (thang 10) có thể bỏ trống -> tự tính từ totalCorrect / totalQuestions.
     */
    @PostMapping
    public ResponseEntity<?> luuLichSu(@RequestBody Map<String, Object> req) {
        Integer userId = soNguyen(req.get("userId"));
        Integer examId = soNguyen(req.get("examId"));
        Integer totalCorrect = soNguyen(req.get("totalCorrect"));
        Integer totalQuestions = soNguyen(req.get("totalQuestions"));

        if (userId == null || examId == null || totalCorrect == null || totalQuestions == null) {
            return loi(HttpStatus.BAD_REQUEST, "Thiếu hoặc sai kiểu dữ liệu: userId, examId, totalCorrect, totalQuestions là bắt buộc (số nguyên).");
        }
        if (totalQuestions <= 0 || totalCorrect < 0 || totalCorrect > totalQuestions) {
            return loi(HttpStatus.BAD_REQUEST, "Số câu không hợp lệ: cần 0 <= totalCorrect <= totalQuestions và totalQuestions > 0.");
        }
        if (userDAO.findById(userId).isEmpty()) {
            return loi(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng có id = " + userId);
        }
        if (examDAO.findById(examId).isEmpty()) {
            return loi(HttpStatus.NOT_FOUND, "Không tìm thấy đề thi có id = " + examId);
        }

        Double score = soThuc(req.get("score"));
        if (req.get("score") != null && score == null) {
            return loi(HttpStatus.BAD_REQUEST, "score phải là số.");
        }
        if (score == null) {
            score = Math.round((double) totalCorrect / totalQuestions * 100.0) / 10.0; // thang 10, 1 chữ số thập phân
        }
        if (score < 0 || score > 10) {
            return loi(HttpStatus.BAD_REQUEST, "score phải nằm trong khoảng 0 - 10.");
        }

        ExamResult r = new ExamResult();
        r.setUserId(userId);
        r.setExamId(examId);
        r.setTotalCorrect(totalCorrect);
        r.setTotalQuestions(totalQuestions);
        r.setScore(score);

        if (examResultDAO.save(r) <= 0) {
            return loi(HttpStatus.INTERNAL_SERVER_ERROR, "Lưu lịch sử làm bài thất bại!");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("message", "Lưu lịch sử làm bài thành công!");
        body.put("userId", userId);
        body.put("examId", examId);
        body.put("score", score);
        body.put("totalCorrect", totalCorrect);
        body.put("totalQuestions", totalQuestions);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // ===================== Hàm phụ =====================
    private Map<String, Object> toJson(ExamResult r, String examTitle) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId());
        m.put("userId", r.getUserId());
        m.put("examId", r.getExamId());
        m.put("examTitle", examTitle);
        m.put("score", r.getScore());
        m.put("totalCorrect", r.getTotalCorrect());
        m.put("totalQuestions", r.getTotalQuestions());
        m.put("submittedAt", r.getSubmittedAt());
        return m;
    }

    private ResponseEntity<Map<String, Object>> loi(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    /** Nhận cả 2 và "2"; trả null nếu không phải số nguyên. */
    private static Integer soNguyen(Object v) {
        if (v instanceof Integer i) return i;
        if (v instanceof Long l && l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) return l.intValue();
        if (v instanceof String s) {
            try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    private static Double soThuc(Object v) {
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) {
            try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return null; }
        }
        return null;
    }
}
