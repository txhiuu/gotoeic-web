package com.nhom5.toeicbackend.service;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.ExamResultDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Exam;
import com.nhom5.toeicbackend.model.ExamResult;
import com.nhom5.toeicbackend.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NopBaiServiceImpl implements NopBaiService {

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private ExamResultDAO examResultDAO;

    public NopBaiServiceImpl() {
    }

    public NopBaiServiceImpl(ExamDAO examDAO, QuestionDAO questionDAO, ExamResultDAO examResultDAO) {
        this.examDAO = examDAO;
        this.questionDAO = questionDAO;
        this.examResultDAO = examResultDAO;
    }

    // 1. HÀM LẤY THÔNG TIN BÀI LÀM (Em bị thiếu hàm này nên gây lỗi)
    @Override
    public Map<String, Object> layThongTinNopBai(int maBaiLam) {
        Optional<Exam> examOpt = examDAO.findById(maBaiLam);
        List<Question> questions = questionDAO.getCAUHOI(maBaiLam);

        Map<String, Object> data = new HashMap<>();
        data.put("examId", maBaiLam);
        data.put("title", examOpt.map(Exam::getTitle).orElse("Đề thi thử TOEIC Full Test 01"));
        data.put("durationMinutes", examOpt.map(Exam::getDurationMinutes).orElse(120));
        data.put("totalQuestions", questions.size());
        data.put("questions", questions);

        return data;
    }

    // 2. HÀM XỬ LÝ NỘP BÀI VÀ CHẤM ĐIỂM
    @Override
    @Transactional
    public Map<String, Object> nopBai(int maBaiLam, int userId, Map<String, String> dsDapAnDaChon) {
        if (dsDapAnDaChon == null) {
            dsDapAnDaChon = Collections.emptyMap();
        }

        List<Question> questions = questionDAO.getCAUHOI(maBaiLam);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Đề thi chưa có câu hỏi để chấm điểm.");
        }
        int totalQuestions = questions.size();
        int totalCorrect = 0;
        Map<String, String> correctAnswers = new LinkedHashMap<>();

        for (Question q : questions) {
            correctAnswers.put(String.valueOf(q.getId()), q.getCorrectAnswer());
            String selected = dsDapAnDaChon.get(String.valueOf(q.getId()));
            if (selected != null && selected.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                totalCorrect++;
            }
        }

        double score = totalQuestions > 0 ? Math.round(((double) totalCorrect / totalQuestions * 10) * 10.0) / 10.0
                : 0.0;
        int scoreToeic = 0;
        if (totalQuestions > 0) {
            double ratio = (double) totalCorrect / totalQuestions;
            scoreToeic = (int) Math.round(ratio * 990);
        }
        if (scoreToeic > 990)
            scoreToeic = 990;

        ExamResult result = new ExamResult();
        result.setUserId(userId);
        result.setExamId(maBaiLam);
        result.setScore(score);
        result.setTotalCorrect(totalCorrect);
        result.setTotalQuestions(totalQuestions);

        // Lưu kết quả và lấy ID (Dùng hàm saveAndGetId đã sửa ở bài trước)
        int resultId = examResultDAO.saveAndGetId(result);

        // Lưu chi tiết vào bảng thứ 5
        if (resultId != -1) {
            for (Question q : questions) {
                String selected = dsDapAnDaChon.getOrDefault(String.valueOf(q.getId()), "");
                boolean isCorrect = selected.trim().equalsIgnoreCase(q.getCorrectAnswer().trim());
                examResultDAO.saveExamResultDetail(resultId, q.getId(), selected, isCorrect);
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("score", score);
        res.put("scoreToeic", scoreToeic);
        res.put("totalCorrect", totalCorrect);
        res.put("totalQuestions", totalQuestions);
        res.put("correctAnswers", correctAnswers);
        res.put("message", "Nộp bài và chấm điểm thành công!");

        return res;
    }
}
