package com.nhom5.toeicbackend.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ThongKeDAOImpl implements ThongKeDAO {

    private final JdbcTemplate jdbcTemplate;

    public ThongKeDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long countUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
    }

    @Override
    public long countQuestions() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
    }

    @Override
    public long countExams() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exams", Long.class);
    }
}
