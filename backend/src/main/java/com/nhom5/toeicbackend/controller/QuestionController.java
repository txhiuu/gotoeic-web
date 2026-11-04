package com.nhom5.toeicbackend.controller;

import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionDAO questionDAO;

    // API Lấy danh sách tất cả câu hỏi
    @GetMapping
    public List<Question> getAll() {
        return questionDAO.getAllQuestions();
    }

    // API Lấy 1 câu hỏi theo ID
    @GetMapping("/{id}")
    public Question getById(@PathVariable int id) {
        return questionDAO.getQuestionById(id);
    }

    // API Thêm câu hỏi mới
    @PostMapping
    public String add(@RequestBody Question q) {
        return questionDAO.addQuestion(q) ? "Thêm thành công" : "Thêm thất bại";
    }

    // API Cập nhật câu hỏi
    @PutMapping("/{id}")
    public String update(@PathVariable int id, @RequestBody Question q) {
        q.setId(id);
        return questionDAO.updateQuestion(q) ? "Cập nhật thành công" : "Cập nhật thất bại";
    }

    // API Xóa câu hỏi
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        return questionDAO.deleteQuestion(id) ? "Xóa thành công" : "Xóa thất bại";
    }
}