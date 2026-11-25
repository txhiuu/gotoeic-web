package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", exposedHeaders = "X-Total-Count")
public class ExamController {

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String skill) {
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
                Map<String, Object> item = toResponse(exam);
                String skillGroup = String.valueOf(item.get("skillGroup"));
                if (skill == null || skill.isBlank() || "all".equalsIgnoreCase(skill)
                        || "both".equals(skillGroup) || skill.equalsIgnoreCase(skillGroup)) {
                    exams.add(item);
                }
            }
        }

        int total = exams.size();
        if (paginate) {
            long start = (long) (pageNumber - 1) * pageSize;
            if (start >= exams.size()) {
                return ResponseEntity.ok().header("X-Total-Count", String.valueOf(total)).body(List.of());
            }
            int end = (int) Math.min(start + pageSize, exams.size());
            exams = new ArrayList<>(exams.subList((int) start, end));
        }
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(total)).body(exams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable int id) {
        return examDAO.findById(id)
                .map(exam -> {
                    Map<String, Object> response = toResponse(exam);
                    response.put("questions", questionDAO.getCAUHOI(id).stream().map(question -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", question.getId());
                        item.put("examId", question.getExamId());
                        item.put("content", question.getContent());
                        item.put("optionA", question.getOptionA());
                        item.put("optionB", question.getOptionB());
                        item.put("optionC", question.getOptionC());
                        item.put("optionD", question.getOptionD());
                        item.put("explanation", question.getExplanation());
                        item.put("part", question.getPart());
                        return item;
                    }).toList());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("success", false);
                    response.put("message", "Không tìm thấy đề thi có id = " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Exam exam) {
        String error = validateAndNormalizeExam(exam);
        if (error != null) {
            return errorResponse(HttpStatus.BAD_REQUEST, error);
        }
        int rows = examDAO.save(exam);
        if (rows <= 0) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Thêm đề thi thất bại");
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Thêm đề thi thành công");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable int id, @RequestBody Exam exam) {
        if (examDAO.findById(id).isEmpty()) {
            return errorResponse(HttpStatus.NOT_FOUND, "Không tìm thấy đề thi có id = " + id);
        }
        String error = validateAndNormalizeExam(exam);
        if (error != null) {
            return errorResponse(HttpStatus.BAD_REQUEST, error);
        }
        exam.setId(id);
        int rows = examDAO.update(exam);
        if (rows <= 0) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Cập nhật đề thi thất bại");
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Cập nhật đề thi thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
        if (examDAO.findById(id).isEmpty()) {
            return errorResponse(HttpStatus.NOT_FOUND, "Không tìm thấy đề thi có id = " + id);
        }
        int rows = examDAO.delete(id);
        if (rows <= 0) {
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa đề thi thất bại");
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Xóa đề thi thành công");
        return ResponseEntity.ok(response);
    }

    private String validateAndNormalizeExam(Exam exam) {
        if (exam == null || exam.getTitle() == null || exam.getTitle().trim().isEmpty()) {
            return "Tên đề thi không được để trống";
        }
        if (exam.getDurationMinutes() == null || exam.getDurationMinutes() < 1 || exam.getDurationMinutes() > 300) {
            return "Thời gian làm bài phải từ 1 đến 300 phút";
        }
        exam.setTitle(exam.getTitle().trim());
        exam.setDescription(exam.getDescription() == null ? "" : exam.getDescription().trim());
        return null;
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, Object> toResponse(Exam exam) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<com.nhom5.toeicbackend.model.Question> questions = questionDAO.getCAUHOI(exam.getId());
        boolean hasListeningReading = questions.stream().anyMatch(q -> q.getPart() != null
                && q.getPart().toLowerCase(Locale.ROOT).matches(".*(listening|reading|part [1-7]|nghe|đọc).*"));
        boolean hasSpeakingWriting = questions.stream().anyMatch(q -> q.getPart() != null
                && q.getPart().toLowerCase(Locale.ROOT).matches(".*(speaking|writing|nói|viết).*"));
        response.put("id", exam.getId());
        response.put("title", exam.getTitle());
        response.put("description", exam.getDescription());
        response.put("durationMinutes", exam.getDurationMinutes());
        response.put("questionCount", questions.size());
        response.put("skillGroup", hasListeningReading && hasSpeakingWriting ? "both"
                : hasSpeakingWriting ? "sw" : "lr");
        response.put("createdAt", exam.getCreatedAt());
        return response;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }
}
