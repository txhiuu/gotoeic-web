package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.Exam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExamDAOImpl implements ExamDAO {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Exam> findById(int id) {
        if (jdbcTemplate == null) return Optional.empty();
        String sql = "SELECT * FROM exams WHERE id = ?";
        try {
            Exam exam = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Exam.class), id);
            return Optional.ofNullable(exam);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Exam> findAll() {
        if (jdbcTemplate == null) return List.of();
        String sql = "SELECT * FROM exams ORDER BY id ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Exam.class));
    }

    @Override
    public List<Exam> findExamsByUserId(int userId) {
        if (jdbcTemplate == null) return List.of();
        String sql = "SELECT e.* FROM exams e JOIN user_exam ue ON e.id = ue.exam_id WHERE ue.user_id = ? ORDER BY e.id ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Exam.class), userId);
    }

    @Override
    public int save(Exam exam) {
        if (jdbcTemplate == null) return 1;
        String sql = "INSERT INTO exams (title, description, duration_minutes) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, exam.getTitle(), exam.getDescription(), exam.getDurationMinutes());
    }

    @Override
    public int update(Exam exam) {
        if (jdbcTemplate == null) return 1;
        String sql = "UPDATE exams SET title = ?, description = ?, duration_minutes = ? WHERE id = ?";
        return jdbcTemplate.update(sql, exam.getTitle(), exam.getDescription(), exam.getDurationMinutes(), exam.getId());
    }

    @Override
    public int delete(int id) {
        if (jdbcTemplate == null) return 1;
        String sql = "DELETE FROM exams WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}

