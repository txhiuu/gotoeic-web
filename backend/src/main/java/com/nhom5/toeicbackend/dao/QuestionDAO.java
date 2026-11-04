package com.nhom5.toeicbackend.dao;

import com.nhom5.toeicbackend.model.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionDAO {

    List<Question> getAllQuestions();

    Question getQuestionById(int id);

    boolean addQuestion(Question q);

    boolean updateQuestion(Question q);

    boolean deleteQuestion(int id);

    List<Question> getCAUHOI(int examId);

    Optional<String> getDAPAN(int questionId);
}
