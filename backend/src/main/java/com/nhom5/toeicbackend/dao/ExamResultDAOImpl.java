package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.ExamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExamResultDAOImpl implements ExamResultDAO {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ExamResult> findById(int id) {
        if (jdbcTemplate == null) return Optional.empty();
        String sql = "SELECT * FROM exam_results WHERE id = ?";
        try {
            ExamResult res = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ExamResult.class), id);
            return Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ExamResult> findByUserId(int userId) {
        if (jdbcTemplate == null) return List.of();
        String sql = "SELECT * FROM exam_results WHERE user_id = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamResult.class), userId);
    }

    @Override
    public int save(ExamResult res) {
        if (jdbcTemplate == null) return 1;
        String sql = "INSERT INTO exam_results (user_id, exam_id, score, total_correct, total_questions) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, res.getUserId(), res.getExamId(), res.getScore(), res.getTotalCorrect(), res.getTotalQuestions());
    }

    @Override
    public int update(ExamResult res) {
        if (jdbcTemplate == null) return 1;
        String sql = "UPDATE exam_results SET score = ?, total_correct = ?, total_questions = ? WHERE id = ?";
        return jdbcTemplate.update(sql, res.getScore(), res.getTotalCorrect(), res.getTotalQuestions(), res.getId());
    }
}

