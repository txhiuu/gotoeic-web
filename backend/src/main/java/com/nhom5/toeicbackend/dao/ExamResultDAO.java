package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.ExamResult;
import java.util.List;
import java.util.Optional;

public interface ExamResultDAO {
    Optional<ExamResult> findById(int id);
    List<ExamResult> findByUserId(int userId);
    int save(ExamResult res);
    int update(ExamResult res);
}

