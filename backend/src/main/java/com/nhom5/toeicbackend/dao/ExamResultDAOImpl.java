package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.ExamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ExamResultDAOImpl implements ExamResultDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ExamResult> findById(int id) {
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
        String sql = "SELECT * FROM exam_results WHERE user_id = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamResult.class), userId);
    }

    @Override
    public int update(ExamResult res) {
        String sql = "UPDATE exam_results SET score = ?, total_correct = ?, total_questions = ? WHERE id = ?";
        return jdbcTemplate.update(sql, res.getScore(), res.getTotalCorrect(), res.getTotalQuestions(), res.getId());
    }

    @Override
    public int saveAndGetId(ExamResult res) {
        String sql = "INSERT INTO exam_results (user_id, exam_id, score, total_correct, total_questions) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, res.getUserId());
            ps.setInt(2, res.getExamId());
            ps.setDouble(3, res.getScore());
            ps.setInt(4, res.getTotalCorrect());
            ps.setInt(5, res.getTotalQuestions());
            return ps;
        }, keyHolder);

        // Sửa lại cách lấy key cho chắc chắn trên PostgreSQL
        Number key = keyHolder.getKeys() != null ? (Number) keyHolder.getKeys().get("id") : null;
        return key != null ? key.intValue() : -1;
    }

    @Override
    public void saveExamResultDetail(int resultId, int questionId, String selectedAnswer, boolean isCorrect) {
        String sql = "INSERT INTO exam_result_details (result_id, question_id, selected_answer, is_correct) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, resultId, questionId, selectedAnswer, isCorrect);
    }
}