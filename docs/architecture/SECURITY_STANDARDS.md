# Bảo mật

- Dùng role mapping tại SecurityConfig. Lấy identity từ principal và kiểm tra quyền theo đối tượng ở backend. Kiểm tra khách, đúng role, sai role và cùng role khác chủ. Dùng 403/404 nhất quán theo chính sách tài nguyên.
- Giữ CSRF ở form thay đổi trạng thái, gồm multipart và logout. Kiểm tra form login, khóa tài khoản, remember-me, logout và phiên sau đổi thông tin xác thực. Xác định nhu cầu HTTP Basic ở môi trường triển khai.
- Dùng PasswordEncoder hiện có và giới hạn BCrypt theo byte. Giữ credentials, token, OTP và dữ liệu cá nhân khỏi diff, model và log. Dùng cấu hình môi trường cho bí mật. Cách ly dev có seed, OTP và log khỏi dữ liệu thật. Trước khi mở reset ngoài dev, yêu cầu kênh giao mã an toàn, giới hạn tần suất, mã dùng một lần có hạn và chính sách thu hồi phiên.
- Escape bằng Thymeleaf, giới hạn scheme URL và dùng DOM API an toàn. Xác minh nội dung upload, hạn chế byte và tài nguyên giải mã. Server tạo khóa, kiểm tra containment và lưu ngoài static webroot. Kiểm tra quyền mỗi lượt tải, dùng attachment và nosniff phù hợp, đặt chính sách cache cho dữ liệu riêng tư.
- Đối chiếu phiên bản dependency thực tế trong Maven hoặc JAR với cảnh báo chính thức, ghi thời điểm, nguồn và phạm vi. Giữ dữ liệu, cấu hình, log và bí mật trong môi trường dự án.
