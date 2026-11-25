package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.dao.UserDAO;
import com.nhom5.toeicbackend.model.Question;
import com.nhom5.toeicbackend.service.NopBaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NopBaiController {

    private static final Set<String> VALID_ANSWERS = Set.of("A", "B", "C", "D", "-");

    @Autowired
    private NopBaiService nopBaiService;

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private UserDAO userDAO;

    public NopBaiController() {}

    public NopBaiController(NopBaiService nopBaiService) {
        this.nopBaiService = nopBaiService;
    }

    @GetMapping("/info/{examId}")
    public ResponseEntity<?> layThongTinNopBai(@PathVariable("examId") int examId) {
        if (examDAO.findById(examId).isEmpty()) {
            return error(HttpStatus.NOT_FOUND, "Không tìm thấy đề thi có id = " + examId);
        }
        Map<String, Object> data = nopBaiService.layThongTinNopBai(examId);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> nopBai(
            @RequestBody Map<String, Object> payload,
            @SessionAttribute(value = "userId", required = false) Integer authenticatedUserId) {
        if (authenticatedUserId == null || userDAO.findById(authenticatedUserId).isEmpty()) {
            return error(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập trước khi nộp bài.");
        }

        Integer requestedUserId = toInteger(payload.get("userId"));
        if (requestedUserId != null && !requestedUserId.equals(authenticatedUserId)) {
            return error(HttpStatus.FORBIDDEN, "Không thể nộp bài thay cho tài khoản khác.");
        }

        Integer examId = toInteger(payload.get("examId") != null ? payload.get("examId") : payload.get("maBaiLam"));
        if (examId == null || examId < 1 || examDAO.findById(examId).isEmpty()) {
            return error(HttpStatus.NOT_FOUND, "Đề thi không tồn tại hoặc mã đề không hợp lệ.");
        }

        List<Question> examQuestions = questionDAO.getCAUHOI(examId);
        if (examQuestions.isEmpty()) {
            return error(HttpStatus.BAD_REQUEST, "Đề thi chưa có câu hỏi để chấm điểm.");
        }

        Object answerObject = payload.get("answers") != null ? payload.get("answers") : payload.get("dsDapAnDaChon");
        if (!(answerObject instanceof Map<?, ?> rawAnswers)) {
            return error(HttpStatus.BAD_REQUEST, "Danh sách câu trả lời không hợp lệ.");
        }

        Set<String> questionIds = new LinkedHashSet<>();
        for (Question question : examQuestions) questionIds.add(String.valueOf(question.getId()));

        Map<String, String> answers = new LinkedHashMap<>();
        int answeredCount = 0;
        for (Map.Entry<?, ?> entry : rawAnswers.entrySet()) {
            String questionId = String.valueOf(entry.getKey());
            if (!questionIds.contains(questionId)) {
                return error(HttpStatus.BAD_REQUEST, "Câu hỏi " + questionId + " không thuộc đề thi này.");
            }
            String answer = entry.getValue() == null ? "" : String.valueOf(entry.getValue()).trim().toUpperCase(Locale.ROOT);
            if (!VALID_ANSWERS.contains(answer)) {
                return error(HttpStatus.BAD_REQUEST, "Đáp án câu " + questionId + " phải là A, B, C, D hoặc dấu - nếu bỏ qua.");
            }
            if (!"-".equals(answer)) answeredCount++;
            answers.put(questionId, answer);
        }
        if (answeredCount == 0) {
            return error(HttpStatus.BAD_REQUEST, "Không thể nộp bài trắng. Hãy trả lời ít nhất một câu.");
        }

        Map<String, Object> result = nopBaiService.nopBai(examId, authenticatedUserId, answers);
        return ResponseEntity.ok(result);
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) return number.intValue();
        if (value instanceof String text) {
            try { return Integer.parseInt(text.trim()); }
            catch (NumberFormatException ignored) { return null; }
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("success", false, "message", message));
    }
}
