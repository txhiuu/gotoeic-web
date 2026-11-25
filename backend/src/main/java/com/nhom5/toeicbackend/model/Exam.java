package com.nhom5.toeicbackend.model;

import java.sql.Timestamp;

public class Exam {
    private Integer id;
    private String title;
    private String description;
    private Integer durationMinutes = 120;
    private Timestamp createdAt;

    public Exam() {}

    public Exam(Integer id, String title, String description, Integer durationMinutes, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Aliases khớp với Class Diagram
    public Integer getDuration_minutes() { return durationMinutes; }
    public void setDuration_minutes(Integer duration_minutes) { this.durationMinutes = duration_minutes; }
    public Timestamp getCreated_at() { return createdAt; }
    public void setCreated_at(Timestamp created_at) { this.createdAt = created_at; }
}

