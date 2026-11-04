package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {

    boolean checkLogin(String email, String password);

    boolean registerUser(String email, String password, String fullName);

    Optional<User> getNGUOIDUNG(String email);

    Optional<User> findById(int id);

    List<User> findAll();

    int save(User user);

    int update(User user);

    int delete(int id);
}
