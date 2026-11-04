package com.nhom5.toeicbackend.model;

import java.sql.Timestamp;

public class ExamResult {
    private int id;
    private int userId;
    private int examId;
    private double score;
    private int totalCorrect;
    private int totalQuestions;
    private Timestamp submittedAt;

    public ExamResult() {}

    public ExamResult(int id, int userId, int examId, double score, int totalCorrect, int totalQuestions, Timestamp submittedAt) {
        this.id = id;
        this.userId = userId;
        this.examId = examId;
        this.score = score;
        this.totalCorrect = totalCorrect;
        this.totalQuestions = totalQuestions;
        this.submittedAt = submittedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public int getTotalCorrect() { return totalCorrect; }
    public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    // Aliases khớp với Class Diagram
    public int getUser_id() { return userId; }
    public void setUser_id(int user_id) { this.userId = user_id; }
    public int getExam_id() { return examId; }
    public void setExam_id(int exam_id) { this.examId = exam_id; }
    public int getTotal_correct() { return totalCorrect; }
    public void setTotal_correct(int total_correct) { this.totalCorrect = total_correct; }
    public int getTotal_questions() { return totalQuestions; }
    public void setTotal_questions(int total_questions) { this.totalQuestions = total_questions; }
    public Timestamp getSubmitted_at() { return submittedAt; }
    public void setSubmitted_at(Timestamp submitted_at) { this.submittedAt = submitted_at; }
}

