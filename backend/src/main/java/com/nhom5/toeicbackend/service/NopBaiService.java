package com.nhom5.toeicbackend.service;

import java.util.Map;

/**
 * Tầng nghiệp vụ (Service Interface) cho Use Case Nộp bài
 * Khớp với Sequence Diagram 2: Nộp bài
 */
public interface NopBaiService {

    /**
     * Bước 2-5: Lấy thông tin bài làm, đề thi và danh sách câu hỏi
     * @param maBaiLam Mã đề thi / bài làm
     * @return Map chứa thông tin đề thi, thời gian và danh sách câu hỏi
     */
    Map<String, Object> layThongTinNopBai(int maBaiLam);

    /**
     * Bước 8-13: Nhận đáp án học viên, chấm bài, cập nhật kết quả và trả về kết quả
     * @param maBaiLam Mã đề thi / bài làm
     * @param userId ID học viên
     * @param dsDapAnDaChon Danh sách đáp án học viên đã chọn (questionId -> selectedOption)
     * @return Map kết quả điểm số, số câu đúng và lời nhắn
     */
    Map<String, Object> nopBai(int maBaiLam, int userId, Map<String, String> dsDapAnDaChon);
}

