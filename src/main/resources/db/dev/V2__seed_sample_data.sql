-- Dữ liệu giả lập cho profile dev. Email và URL dùng tên miền dành riêng cho kiểm thử.
INSERT INTO companies (id, name, website_url, created_at) VALUES
    (1, 'Sao Mộc Labs', 'https://sao-moc.example', '2026-11-01 08:00:00'),
    (2, 'Hải Âu Digital', 'https://hai-au.example', '2026-11-01 08:00:00'),
    (3, 'Mây Lam Studio', 'https://may-lam.example', '2026-11-01 08:00:00');

-- Tài khoản mẫu dùng BCrypt với salt riêng cho từng dòng.
INSERT INTO users (id, company_id, email, password_hash, role, created_at) VALUES
    (1, NULL, 'admin@talentlink.example', '$2y$10$5A9wlLK.WQs2PCUvdvZp9.6X31zlNnqUNr28q4by3NlVTQvomXwum', 'ADMIN', '2026-11-02 08:00:00'),
    (2, 1, 'doitac01@talentlink.example', '$2y$10$5DTMcFinHssniSj7p6OTMuPpfXrvTOcbs.v/xutahlJ8IW8tT80Fa', 'RECRUITER', '2026-11-02 08:00:00'),
    (3, 2, 'doitac02@talentlink.example', '$2y$10$EXCOfr098O1rQtwcH3sbCew7Q43lzWX.sAY0kQGQW0VZDIgnUsSMK', 'RECRUITER', '2026-11-02 08:00:00'),
    (4, 3, 'doitac03@talentlink.example', '$2y$10$2l7cosLzqsb5RrxQS6nUPObUG0W8UXaPc22igpEMNZXW78yiCapjW', 'RECRUITER', '2026-11-02 08:00:00'),
    (101, NULL, 'sinhvien01@talentlink.example', '$2y$10$7zUhBDQ/N6j4FzvI5pv7yuRDYeEnK/j4OH0MR/P6vtKvTNd/3mmEO', 'CANDIDATE', '2026-11-02 08:00:00'),
    (102, NULL, 'sinhvien02@talentlink.example', '$2y$10$W0GHLVyNG3K7FG2WkKiw/up/30zU0JpdRtSjE8y3osG8uzUOYVSE2', 'CANDIDATE', '2026-11-02 08:00:00'),
    (103, NULL, 'sinhvien03@talentlink.example', '$2y$10$.i3CGEuSnbY/G.U9p5G3rOW179EIXgxykTkcYASX09ON93LEzODvG', 'CANDIDATE', '2026-11-02 08:00:00'),
    (104, NULL, 'sinhvien04@talentlink.example', '$2y$10$xj06Ip5I9C.ReueYEhboDOqphdzSiQacPaylC5D5LVxY.wPs.qFqi', 'CANDIDATE', '2026-11-02 08:00:00'),
    (105, NULL, 'sinhvien05@talentlink.example', '$2y$10$An3vsMABHY.ANG2NQDrXsO6LHtMtTRwE5nai9rHd2dfHU3Cqskmh2', 'CANDIDATE', '2026-11-02 08:00:00'),
    (106, NULL, 'sinhvien06@talentlink.example', '$2y$10$S93v0iN5nnGYAAl1D.iQWutXWwE5LPQInbZy/yOGfWPow.wWvQYqS', 'CANDIDATE', '2026-11-02 08:00:00'),
    (107, NULL, 'sinhvien07@talentlink.example', '$2y$10$nxGTZwA/Edv4iw6qY/j8Lu9Qe7scs/Od9jP8/my9ggbFO/jHCW4iy', 'CANDIDATE', '2026-11-02 08:00:00'),
    (108, NULL, 'sinhvien08@talentlink.example', '$2y$10$OidepxQqShQpz9IGjLrCZOwRflabo8.ie2UP./pxSefTsuQX3sUyW', 'CANDIDATE', '2026-11-02 08:00:00'),
    (109, NULL, 'sinhvien09@talentlink.example', '$2y$10$BvjCT0JGrTDAzHLFhtTjSel9t1me2ydq12t0WnINdZ9YgmrDCtsaa', 'CANDIDATE', '2026-11-02 08:00:00'),
    (110, NULL, 'sinhvien10@talentlink.example', '$2y$10$x5sWgx5yOOfKvalBdKbosuhJkIcoLF9PH0temS3kGSYnqUo2xB7sy', 'CANDIDATE', '2026-11-02 08:00:00'),
    (111, NULL, 'sinhvien11@talentlink.example', '$2y$10$fK/EYZlpcEYwr90DgPDiHe8MVBqiLS47AoNpn6r76vtoRw.I3rayS', 'CANDIDATE', '2026-11-02 08:00:00'),
    (112, NULL, 'sinhvien12@talentlink.example', '$2y$10$pleoOQEd6DLnzFaX5dX7B.WQ18XLnJupE7Tf6.utXTihyfESISgZa', 'CANDIDATE', '2026-11-02 08:00:00'),
    (113, NULL, 'sinhvien13@talentlink.example', '$2y$10$dtRILKC2.gmhFbroAeqSEuyBRwoBb5hj0.qOZlD5WbsVdAKiDVssa', 'CANDIDATE', '2026-11-02 08:00:00'),
    (114, NULL, 'sinhvien14@talentlink.example', '$2y$10$8.JO5WrnEFZMav5p18bm6ex9aqo1CocmEfP07m5zunmLGmEqYb1Cy', 'CANDIDATE', '2026-11-02 08:00:00'),
    (115, NULL, 'sinhvien15@talentlink.example', '$2y$10$hY/W.UujAHdIm2rV3AwkyO4pKrcKlJnnjvRADsUN/bVGF/AFT66iG', 'CANDIDATE', '2026-11-02 08:00:00');

INSERT INTO profiles (id, user_id, full_name, headline, summary, created_at, updated_at) VALUES
    (1, 101, 'Sinh viên mẫu 01', 'Lập trình Java', 'Thực hành xây dựng API quản lý thư viện.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (2, 102, 'Sinh viên mẫu 02', 'Lập trình JavaScript', 'Xây dựng web quản lý lịch học trên trình duyệt.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (3, 103, 'Sinh viên mẫu 03', 'Phân tích dữ liệu', 'Dùng dữ liệu mô phỏng để phân tích lưu lượng xe.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (4, 104, 'Sinh viên mẫu 04', 'Lập trình web', 'Thiết kế giao diện danh mục sách dễ dùng trên điện thoại.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (5, 105, 'Sinh viên mẫu 05', 'Kỹ thuật dữ liệu', 'Thử nghiệm xử lý dữ liệu bán vé giả lập theo lô.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (6, 106, 'Sinh viên mẫu 06', 'Lập trình Android', 'Phát triển ứng dụng ghi chú chạy ngoại tuyến.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (7, 107, 'Sinh viên mẫu 07', 'Lập trình iOS', 'Xây dựng ứng dụng nhắc lịch học cá nhân.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (8, 108, 'Sinh viên mẫu 08', 'Kỹ thuật DevOps', 'Đóng gói và triển khai hệ thống học tập mô phỏng.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (9, 109, 'Sinh viên mẫu 09', 'Kiểm thử phần mềm', 'Viết kịch bản kiểm thử hệ thống đặt phòng giả lập.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (10, 110, 'Sinh viên mẫu 10', 'Lập trình .NET', 'Xây dựng trình quản lý kho cho bài tập nhóm.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (11, 111, 'Sinh viên mẫu 11', 'Phát triển sản phẩm số', 'Thiết kế quy trình đăng ký cho cổng câu lạc bộ giả lập.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (12, 112, 'Sinh viên mẫu 12', 'Học máy', 'Phân loại tài liệu bằng tập dữ liệu tổng hợp.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (13, 113, 'Sinh viên mẫu 13', 'Phân tích nghiệp vụ', 'Mô hình hóa quy trình mượn thiết bị trong trường giả lập.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (14, 114, 'Sinh viên mẫu 14', 'Lập trình đa nền tảng', 'Làm ứng dụng theo dõi chi tiêu từ dữ liệu mô phỏng.', '2026-11-03 08:00:00', '2026-11-06 08:00:00'),
    (15, 115, 'Sinh viên mẫu 15', 'Lập trình hệ thống', 'Viết công cụ tổng hợp nhật ký hệ thống mẫu.', '2026-11-03 08:00:00', '2026-11-06 08:00:00');

INSERT INTO skills (id, name, slug) VALUES
    (1, 'Java', 'java'),
    (2, 'Spring Boot', 'spring-boot'),
    (3, 'JavaScript', 'javascript'),
    (4, 'React', 'react'),
    (5, 'Python', 'python'),
    (6, 'Pandas', 'pandas'),
    (7, 'HTML', 'html'),
    (8, 'CSS', 'css'),
    (9, 'SQL', 'sql'),
    (10, 'MySQL', 'mysql'),
    (11, 'Kotlin', 'kotlin'),
    (12, 'Android', 'android'),
    (13, 'Swift', 'swift'),
    (14, 'SwiftUI', 'swiftui'),
    (15, 'Docker', 'docker'),
    (16, 'Kubernetes', 'kubernetes'),
    (17, 'JUnit', 'junit'),
    (18, 'Playwright', 'playwright'),
    (19, 'C#', 'c-sharp'),
    (20, '.NET', 'dotnet'),
    (21, 'Figma', 'figma'),
    (22, 'Thiết kế UX', 'ux-design'),
    (23, 'scikit-learn', 'scikit-learn'),
    (24, 'NumPy', 'numpy'),
    (25, 'BPMN', 'bpmn'),
    (26, 'UML', 'uml'),
    (27, 'Dart', 'dart'),
    (28, 'Flutter', 'flutter'),
    (29, 'C++', 'cpp'),
    (30, 'Linux', 'linux');

INSERT INTO profile_skills (profile_id, skill_id, skill_level) VALUES
    (1, 1, 4), (1, 2, 3),
    (2, 3, 4), (2, 4, 3),
    (3, 5, 4), (3, 6, 3),
    (4, 7, 5), (4, 8, 4),
    (5, 9, 4), (5, 10, 3),
    (6, 11, 3), (6, 12, 4),
    (7, 13, 4), (7, 14, 3),
    (8, 15, 4), (8, 16, 2),
    (9, 17, 4), (9, 18, 3),
    (10, 19, 4), (10, 20, 3),
    (11, 21, 4), (11, 22, 3),
    (12, 23, 3), (12, 24, 4),
    (13, 25, 4), (13, 26, 3),
    (14, 27, 3), (14, 28, 4),
    (15, 29, 3), (15, 30, 4);

INSERT INTO educations (profile_id, institution, degree, field_of_study, start_date, end_date)
SELECT id, 'Học viện Giả lập TalentLink', 'Cử nhân', 'Công nghệ thông tin', '2022-09-05', '2026-06-30'
FROM profiles
WHERE id BETWEEN 1 AND 15
ORDER BY id;

INSERT INTO projects (profile_id, name, description, project_url, start_date, end_date)
SELECT id, CONCAT('Đồ án mô phỏng ', LPAD(id, 2, '0')), summary,
       CONCAT('https://du-an.example/', LPAD(id, 2, '0')), '2025-09-01', '2025-12-20'
FROM profiles
WHERE id BETWEEN 1 AND 15
ORDER BY id;

INSERT INTO certificates (profile_id, name, issuer, issued_on, expires_on, credential_url)
SELECT id, CONCAT('Chứng chỉ mô phỏng · ', headline), 'Trung tâm Thực hành Giả lập',
       '2026-07-01', NULL, CONCAT('https://chung-chi.example/', LPAD(id, 2, '0'))
FROM profiles
WHERE id BETWEEN 1 AND 15
ORDER BY id;

-- Hồ sơ 01 có hai lần duyệt để minh họa lịch sử.
INSERT INTO profile_reviews (profile_id, reviewer_user_id, decision, note, reviewed_at)
VALUES (1, 1, 'CHANGES_REQUESTED', 'Bổ sung vai trò trong đồ án.', '2026-11-05 09:00:00');

INSERT INTO profile_reviews (profile_id, reviewer_user_id, decision, note, reviewed_at)
SELECT id, 1,
       CASE WHEN id IN (13, 14) THEN 'CHANGES_REQUESTED' WHEN id = 15 THEN 'REJECTED' ELSE 'APPROVED' END,
       CASE WHEN id IN (13, 14) THEN 'Bổ sung kết quả đồ án.' WHEN id = 15 THEN 'Thiếu bằng chứng hoàn thành.' ELSE 'Đã duyệt hồ sơ mẫu.' END,
       '2026-11-07 09:00:00'
FROM profiles
WHERE id BETWEEN 1 AND 15
ORDER BY id;

INSERT INTO shortlists (recruiter_user_id, profile_id, created_at) VALUES
    (2, 1, '2026-11-08 08:00:00'),
    (3, 5, '2026-11-08 08:15:00'),
    (4, 9, '2026-11-08 08:30:00');

INSERT INTO interview_requests (recruiter_user_id, profile_id, status, message, scheduled_at, created_at, updated_at) VALUES
    (2, 1, 'PENDING', 'Mời trao đổi về vị trí Java thực tập.', NULL, '2026-11-09 08:00:00', '2026-11-09 08:00:00'),
    (3, 5, 'ACCEPTED', 'Mời trao đổi về vị trí kỹ thuật dữ liệu.', '2026-12-15 02:00:00', '2026-11-09 08:10:00', '2026-11-10 08:10:00'),
    (4, 9, 'DECLINED', 'Mời trao đổi về vị trí kiểm thử.', NULL, '2026-11-09 08:20:00', '2026-11-10 08:20:00');

INSERT INTO notifications (user_id, type, title, body, created_at, read_at) VALUES
    (101, 'PROFILE_REVIEW', 'Hồ sơ đã được duyệt', 'Hồ sơ mẫu 01 đã được duyệt.', '2026-11-07 09:00:00', '2026-11-07 10:00:00'),
    (101, 'INTERVIEW_REQUEST', 'Yêu cầu phỏng vấn mới', 'Sao Mộc Labs gửi lời mời trao đổi.', '2026-11-09 08:00:00', NULL),
    (113, 'PROFILE_REVIEW', 'Hồ sơ cần chỉnh sửa', 'Bổ sung kết quả đồ án và gửi duyệt lại.', '2026-11-07 09:00:00', NULL);

INSERT INTO profile_views (profile_id, viewer_user_id, viewed_at) VALUES
    (1, 2, '2026-11-08 07:50:00'),
    (5, 3, '2026-11-08 08:05:00'),
    (9, 4, '2026-11-08 08:20:00'),
    (1, NULL, '2026-11-08 10:00:00');
