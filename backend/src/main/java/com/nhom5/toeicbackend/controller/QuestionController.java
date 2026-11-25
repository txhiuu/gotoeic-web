package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class QuestionController {

    private static final Set<String> VALID_ANSWERS = Set.of("A", "B", "C", "D");

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private ExamDAO examDAO;

    @GetMapping
    public List<Question> getAll() {
        return questionDAO.getAllQuestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Question q = questionDAO.getQuestionById(id);
        if (q == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy câu hỏi có id = " + id);
        }
        return ResponseEntity.ok(q);
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Question q) {
        String validationError = validateAndNormalize(q, true);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        return questionDAO.addQuestion(q)
                ? ResponseEntity.ok("Thêm thành công")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm thất bại");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Question q) {
        if (questionDAO.getQuestionById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy câu hỏi có id = " + id);
        }
        String validationError = validateAndNormalize(q, false);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        q.setId(id);
        return questionDAO.updateQuestion(q)
                ? ResponseEntity.ok("Cập nhật thành công")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật thất bại");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        if (questionDAO.getQuestionById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy câu hỏi có id = " + id);
        }
        return questionDAO.deleteQuestion(id)
                ? ResponseEntity.ok("Xóa thành công")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại");
    }

    private String validateAndNormalize(Question q, boolean requireExamId) {
        if (q == null) {
            return "Dữ liệu câu hỏi không được trống";
        }
        if (requireExamId && q.getExamId() == null) {
            return "Vui lòng chọn đề thi cho câu hỏi";
        }
        if (q.getExamId() != null && examDAO.findById(q.getExamId()).isEmpty()) {
            return "Không tìm thấy đề thi có id = " + q.getExamId();
        }
        if (isBlank(q.getContent()) || isBlank(q.getOptionA()) || isBlank(q.getOptionB())
                || isBlank(q.getOptionC()) || isBlank(q.getOptionD())) {
            return "Nội dung câu hỏi và 4 đáp án A, B, C, D không được để trống";
        }
        String ans = q.getCorrectAnswer() == null ? "" : q.getCorrectAnswer().trim().toUpperCase(Locale.ROOT);
        if (!VALID_ANSWERS.contains(ans)) {
            return "Đáp án đúng phải là A, B, C hoặc D";
        }
        q.setContent(q.getContent().trim());
        q.setOptionA(q.getOptionA().trim());
        q.setOptionB(q.getOptionB().trim());
        q.setOptionC(q.getOptionC().trim());
        q.setOptionD(q.getOptionD().trim());
        q.setCorrectAnswer(ans);
        q.setExplanation(q.getExplanation() == null ? "" : q.getExplanation().trim());
        q.setPart(isBlank(q.getPart()) ? "Part 5" : q.getPart().trim());
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}