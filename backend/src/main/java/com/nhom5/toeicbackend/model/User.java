package com.nhom5.toeicbackend.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String email;
    private String password;
    private String fullName;
    private String role = "STUDENT";
    private boolean isActive = true;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String email, String password, String fullName, String role, boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Các hàm Getter và Setter (Hỗ trợ cả camelCase trên GitHub và snake_case trên Class Diagram)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Aliases khớp với Class Diagram
    public String getFull_name() { return fullName; }
    public void setFull_name(String full_name) { this.fullName = full_name; }
    public boolean getIs_active() { return isActive; }
    public void setIs_active(boolean is_active) { this.isActive = is_active; }
    public Timestamp getCreated_at() { return createdAt; }
    public void setCreated_at(Timestamp created_at) { this.createdAt = created_at; }
}
