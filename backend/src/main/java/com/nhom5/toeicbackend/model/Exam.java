package com.nhom5.toeicbackend.model;

import java.sql.Timestamp;

public class Exam {
    private int id;
    private String title;
    private String description;
    private int durationMinutes = 120;
    private Timestamp createdAt;

    public Exam() {}

    public Exam(int id, String title, String description, int durationMinutes, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Aliases khớp với Class Diagram
    public int getDuration_minutes() { return durationMinutes; }
    public void setDuration_minutes(int duration_minutes) { this.durationMinutes = duration_minutes; }
    public Timestamp getCreated_at() { return createdAt; }
    public void setCreated_at(Timestamp created_at) { this.createdAt = created_at; }
}

