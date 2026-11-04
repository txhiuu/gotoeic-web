package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.Exam;
import java.util.List;
import java.util.Optional;

public interface ExamDAO {
    Optional<Exam> findById(int id);
    List<Exam> findAll();
    List<Exam> findExamsByUserId(int userId);
    int save(Exam exam);
    int update(Exam exam);
    int delete(int id);
}

