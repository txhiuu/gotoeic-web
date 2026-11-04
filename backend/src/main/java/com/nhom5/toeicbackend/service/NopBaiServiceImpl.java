package com.nhom5.toeicbackend.service;

import com.nhom5.toeicbackend.dao.ExamDAO;
import com.nhom5.toeicbackend.dao.ExamResultDAO;
import com.nhom5.toeicbackend.dao.QuestionDAO;
import com.nhom5.toeicbackend.model.Exam;
import com.nhom5.toeicbackend.model.ExamResult;
import com.nhom5.toeicbackend.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Cài đặt tầng nghiệp vụ (Service Implementation) cho Use Case Nộp bài
 * Khớp với Sequence Diagram 2: NopBaiController -> CAUHOI, BAILAM, CHITIETBAILAM, DAPAN
 */
@Service
public class NopBaiServiceImpl implements NopBaiService {

    @Autowired
    private ExamDAO examDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private ExamResultDAO examResultDAO;

    public NopBaiServiceImpl() {}

    public NopBaiServiceImpl(ExamDAO examDAO, QuestionDAO questionDAO, ExamResultDAO examResultDAO) {
        this.examDAO = examDAO;
        this.questionDAO = questionDAO;
        this.examResultDAO = examResultDAO;
    }

    @Override
    public Map<String, Object> layThongTinNopBai(int maBaiLam) {
        // Bước 3-4: getCAUHOI(), getBAILAM()
        Optional<Exam> examOpt = examDAO.findById(maBaiLam);
        List<Question> questions = questionDAO.getCAUHOI(maBaiLam);

        // Bước 5: return danh sách câu hỏi, thời gian còn lại
        Map<String, Object> data = new HashMap<>();
        data.put("examId", maBaiLam);
        data.put("title", examOpt.map(Exam::getTitle).orElse("Đề thi thử TOEIC Full Test 01"));
        data.put("durationMinutes", examOpt.map(Exam::getDurationMinutes).orElse(120));
        data.put("totalQuestions", questions.size());
        data.put("questions", questions);

        return data;
    }

    @Override
    public Map<String, Object> nopBai(int maBaiLam, int userId, Map<String, String> dsDapAnDaChon) {
        if (dsDapAnDaChon == null) {
            dsDapAnDaChon = Collections.emptyMap();
        }

        // Bước 9-10: getCAUHOI / getDAPAN()
        List<Question> questions = questionDAO.getCAUHOI(maBaiLam);
        int totalQuestions = questions.isEmpty() ? 10 : questions.size();
        int totalCorrect = 0;

        // Bước 11: Chấm bài()
        for (Question q : questions) {
            String selected = dsDapAnDaChon.get(String.valueOf(q.getId()));
            if (selected != null && selected.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                totalCorrect++;
            }
        }

        // Thang điểm 10 và quy đổi TOEIC 990
        double score = totalQuestions > 0 ? Math.round(((double) totalCorrect / totalQuestions * 10) * 10.0) / 10.0 : 0.0;
        int scoreToeic = (int) Math.round(totalCorrect * 85.0 + 100);
        if (scoreToeic > 990) scoreToeic = 990;

        // Bước 12: updateBAILAM() (Lưu kết quả vào exam_results)
        ExamResult result = new ExamResult();
        result.setUserId(userId > 0 ? userId : 2);
        result.setExamId(maBaiLam);
        result.setScore(score);
        result.setTotalCorrect(totalCorrect);
        result.setTotalQuestions(totalQuestions);
        examResultDAO.save(result);

        // Bước 13: return kết quả làm bài
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("score", score);
        res.put("scoreToeic", scoreToeic);
        res.put("totalCorrect", totalCorrect);
        res.put("totalQuestions", totalQuestions);
        res.put("message", "Nộp bài và chấm điểm thành công!");

        return res;
    }
}

