package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserDAOImpl implements UserDAO {

    private static final Pattern BCRYPT_HASH_PATTERN =
            Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    @Autowired
    private JdbcTemplate jdbcTemplate; // Bắt buộc phải có JdbcTemplate, không dùng required=false

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("role"),
            rs.getBoolean("is_active"),
            rs.getTimestamp("created_at"));

    @Override
    public boolean checkLogin(String email, String password) {
        String sql = "SELECT password FROM users WHERE email = ?";
        String storedPassword;
        try {
            storedPassword = jdbcTemplate.queryForObject(sql, String.class, email);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        if (storedPassword.startsWith("$2")) {
            return BCRYPT_HASH_PATTERN.matcher(storedPassword).matches()
                    && passwordEncoder.matches(password, storedPassword);
        }

        if (!storedPassword.equals(password)) {
            return false;
        }

        String updateSql = "UPDATE users SET password = ? WHERE email = ? AND password = ?";
        return jdbcTemplate.update(updateSql, passwordEncoder.encode(password), email, storedPassword) > 0;
    }

    @Override
    public boolean registerUser(String email, String password, String fullName) {
        // 1. Kiểm tra xem email đã tồn tại chưa
        String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, email);

        if (count != null && count > 0) {
            return false; // Email đã tồn tại
        }

        // 2. Insert vào DB (Bổ sung is_active và created_at để tránh lỗi NOT NULL)
        String insertSql = "INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES (?, ?, ?, 'STUDENT', true, CURRENT_TIMESTAMP)";
        int rowsAffected = jdbcTemplate.update(insertSql, email, passwordEncoder.encode(password), fullName);
        return rowsAffected > 0;
    }

    @Override
    public Optional<User> getNGUOIDUNG(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public int save(User user) {
        String sql = "INSERT INTO users (email, password, full_name, role, is_active, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        return jdbcTemplate.update(sql, user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getFullName(), user.getRole(), user.getIsActive());
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ?, is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getFullName(), user.getRole(), user.getIsActive(), user.getId());
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}