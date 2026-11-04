package com.nhom5.toeicbackend.model;

public class Question {
    // Đã đổi int thành Integer để có thể nhận giá trị null từ JSON
    private Integer id;
    private Integer examId;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    private String part;

    public Question() {}

    // Cập nhật lại Constructor
    public Question(Integer id, Integer examId, String content, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String explanation, String part) {
        this.id = id;
        this.examId = examId;
        this.content = content;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.part = part;
    }

    // Getter và Setter gốc (camelCase)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getPart() { return part; }
    public void setPart(String part) { this.part = part; }

    // Aliases cho Class Diagram (snake_case) - Cũng đã đổi sang Integer
    public Integer getExam_id() { return examId; }
    public void setExam_id(Integer exam_id) { this.examId = exam_id; }

    public String getOption_a() { return optionA; }
    public void setOption_a(String option_a) { this.optionA = option_a; }

    public String getOption_b() { return optionB; }
    public void setOption_b(String option_b) { this.optionB = option_b; }

    public String getOption_c() { return optionC; }
    public void setOption_c(String option_c) { this.optionC = option_c; }

    public String getOption_d() { return optionD; }
    public void setOption_d(String option_d) { this.optionD = option_d; }

    public String getCorrect_answer() { return correctAnswer; }
    public void setCorrect_answer(String correct_answer) { this.correctAnswer = correct_answer; }
}