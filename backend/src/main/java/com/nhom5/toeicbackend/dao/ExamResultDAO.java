package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.ExamResult;
import java.util.List;
import java.util.Optional;

public interface ExamResultDAO {
    Optional<ExamResult> findById(int id);
    List<ExamResult> findByUserId(int userId);
    int update(ExamResult res);
    
    // 2 HÀM MỚI CẦN THÊM:
    int saveAndGetId(ExamResult res);
    void saveExamResultDetail(int resultId, int questionId, String selectedAnswer, boolean isCorrect);
}