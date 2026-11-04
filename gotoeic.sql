-- ===================================================
-- CSDL GoToeic (Khớp với ERD & Class Diagram)
-- ===================================================

-- 1. Bảng USERS (Lưu Khách, Sinh viên, Admin)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'STUDENT', -- 'ADMIN' hoặc 'STUDENT'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng EXAMS (Lưu thông tin Đề thi)
CREATE TABLE IF NOT EXISTS exams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    duration_minutes INT NOT NULL DEFAULT 120, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng USER_EXAM (Bảng liên kết n-n giữa Người dùng và Đề thi theo ERD)
CREATE TABLE IF NOT EXISTS user_exam (
    user_id INT NOT NULL,
    exam_id INT NOT NULL,
    PRIMARY KEY (user_id, exam_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

-- 4. Bảng QUESTIONS (Ngân hàng câu hỏi theo Đề thi)
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    content VARCHAR(255) NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer CHAR(1) NOT NULL CHECK (correct_answer IN ('A', 'B', 'C', 'D')),
    explanation VARCHAR(255), 
    part VARCHAR(50),
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

-- 5. Bảng EXAM_RESULTS (Lưu kết quả & lịch sử làm bài của Sinh viên)
CREATE TABLE IF NOT EXISTS exam_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    exam_id INT NOT NULL,
    score DECIMAL(5,2) NOT NULL, -- Điểm số (VD: 8.5)
    total_correct INT NOT NULL,
    total_questions INT NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

-- ===================================================
-- DỮ LIỆU KHỞI TẠO MẪU (SEED DATA)
-- ===================================================

-- Thêm 1 Admin và 1 Sinh viên (Mật khẩu text thô 123456)
INSERT INTO users (id, email, password, full_name, role, is_active) VALUES 
(1, 'admin@gmail.com', '123456', 'Quản Trị Viên', 'ADMIN', TRUE),
(2, 'student@gmail.com', '123456', 'Nguyễn Văn Sinh Viên', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Thêm Đề thi TOEIC mẫu
INSERT INTO exams (id, title, description, duration_minutes) VALUES 
(1, 'Đề thi thử TOEIC Full Test 01', 'Đề thi thử sát với đề thi thật, gồm 200 câu.', 120)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Thêm phân công / gán đề thi cho người dùng (user_exam)
INSERT INTO user_exam (user_id, exam_id) VALUES 
(2, 1)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Thêm 2 Câu hỏi mẫu theo đề 1
INSERT INTO questions (id, exam_id, content, option_a, option_b, option_c, option_d, correct_answer, explanation, part) VALUES 
(1, 1, 'The manager requested that all employees _____ the safety training.', 'attend', 'attends', 'attending', 'attended', 'A', 'Cấu trúc: request that + S + (should) + V(bare).', 'Part 5'),
(2, 1, 'The company will _____ a new product line next month.', 'launch', 'launching', 'launched', 'launches', 'A', 'Sau trợ động từ "will" dùng động từ nguyên mẫu.', 'Part 5')
ON DUPLICATE KEY UPDATE content=VALUES(content);

-- Thêm Lịch sử làm bài mẫu cho Sinh viên
INSERT INTO exam_results (id, user_id, exam_id, score, total_correct, total_questions) VALUES 
(1, 2, 1, 5.0, 1, 2)
ON DUPLICATE KEY UPDATE score=VALUES(score);