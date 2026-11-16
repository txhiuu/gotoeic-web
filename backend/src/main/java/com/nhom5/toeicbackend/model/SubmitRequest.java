package com.nhom5.toeicbackend.model;

import java.util.Map;

public class SubmitRequest {
    private int userId;
    private int examId;
    private Map<String, String> answers;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
}