package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestionDAOImpl implements QuestionDAO {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Question> getAllQuestions() {
        if (jdbcTemplate == null)
            return List.of();
        String sql = "SELECT * FROM questions ORDER BY id ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public Question getQuestionById(int id) {
        if (jdbcTemplate == null)
            return null;
        String sql = "SELECT * FROM questions WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Question.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean addQuestion(Question q) {
        if (jdbcTemplate == null)
            return true;
        String sql = "INSERT INTO questions (exam_id, content, option_a, option_b, option_c, option_d, correct_answer, explanation, part) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql, q.getExamId(), q.getContent(), q.getOptionA(), q.getOptionB(),
                q.getOptionC(), q.getOptionD(), q.getCorrectAnswer(), q.getExplanation(), q.getPart());
        return rows > 0;
    }

    @Override
    public boolean updateQuestion(Question q) {
        if (jdbcTemplate == null)
            return true;
        String sql = "UPDATE questions SET content = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, explanation = ?, part = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, q.getContent(), q.getOptionA(), q.getOptionB(), q.getOptionC(),
                q.getOptionD(), q.getCorrectAnswer(), q.getExplanation(), q.getPart(), q.getId());
        return rows > 0;
    }

    @Override
    public boolean deleteQuestion(int id) {
        if (jdbcTemplate == null)
            return true;
        String sql = "DELETE FROM questions WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    @Override
    public List<Question> getCAUHOI(int examId) {
        if (jdbcTemplate == null)
            return List.of();
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), examId);
    }

    @Override
    public Optional<String> getDAPAN(int questionId) {
        if (jdbcTemplate == null)
            return Optional.empty();
        String sql = "SELECT correct_answer FROM questions WHERE id = ?";
        try {
            String ans = jdbcTemplate.queryForObject(sql, String.class, questionId);
            return Optional.ofNullable(ans);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
