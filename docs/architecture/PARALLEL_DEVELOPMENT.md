# Phát triển song song

1. Chốt task ID, acceptance criteria, base nộp bài, phụ thuộc, phạm vi file, quyền sửa vùng dùng chung và version migration trước khi giao.
2. Một task, một Chat Codex, một branch và một worktree. Tách schema MySQL, port HTTP và CERTIFICATE_UPLOAD_DIR. Xác minh process, bản build, profile và đường dẫn trước test ghi.
3. Chỉ định worker phụ trách mỗi thay đổi SecurityConfig, layout, CSS, cấu hình và hợp đồng liên module. Nếu file trùng, thống nhất thứ tự và người chịu trách nhiệm.
4. Điều phối cấp version Flyway tập trung, ghi trạng thái chưa áp dụng hoặc đã áp dụng. Đối chiếu chuỗi migration và kiểm tra fresh/upgrade theo [tiêu chuẩn DB](DATABASE_STANDARDS.md).
5. Tự review diff và chạy [Quality Gate](TESTING_STANDARDS.md). Trước tích hợp, so cả route, role map, quy tắc trùng và phụ thuộc. Chạy lại regression trên SHA tích hợp.
6. Đồng bộ nền tảng trước task tiếp theo. Giữ base PR theo yêu cầu nộp bài, bảo toàn PR minh chứng cũ và không tự động merge. Ghi SHA và phạm vi khi phải dựa trên PR chưa nhập.
