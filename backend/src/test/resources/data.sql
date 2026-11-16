-- Du lieu mau cho test (giong seed trong gotoeic.sql)
INSERT INTO users (email, password, full_name, role, is_active) VALUES
('admin@gmail.com',   '123456', 'Quan Tri Vien',        'ADMIN',   TRUE),
('student@gmail.com', '123456', 'Nguyen Van Sinh Vien', 'STUDENT', TRUE),
('locked@gmail.com',  'abc123', 'Tai Khoan Bi Khoa',    'STUDENT', FALSE),
('other@gmail.com',   'matkhau_rieng', 'Hoc Vien Khac', 'STUDENT', TRUE);

INSERT INTO exams (title, description, duration_minutes) VALUES
('De thi thu TOEIC Full Test 01', 'De thi thu sat voi de thi that, gom 200 cau.', 120);

INSERT INTO user_exam (user_id, exam_id) VALUES (2, 1);

INSERT INTO questions (exam_id, content, option_a, option_b, option_c, option_d, correct_answer, explanation, part) VALUES
(1, 'The manager requested that all employees _____ the safety training.', 'attend', 'attends', 'attending', 'attended', 'A', 'request that + S + V(bare)', 'Part 5'),
(1, 'The company will _____ a new product line next month.', 'launch', 'launching', 'launched', 'launches', 'A', 'Sau will dung V nguyen mau', 'Part 5');

INSERT INTO exam_results (user_id, exam_id, score, total_correct, total_questions) VALUES (2, 1, 5.0, 1, 2);
