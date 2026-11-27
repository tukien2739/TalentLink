# MySQL và Flyway

- Giữ migration ở src/main/resources/db/migration. Nền Sprint 1 đến V12, V2 là seed dev và V8 là khoảng trống lịch sử. Điều phối cấp phiên bản trước khi viết. Giữ checksum migration đã áp dụng.
- Dev nạp cả db/migration và db/dev. Giữ ddl-auto=validate. Chạy Flyway trên schema trắng và nâng cấp từ nền trước, kiểm tra dữ liệu cũ, FK, unique và check. Đánh giá phục hồi dữ liệu trước DDL thay đổi khó đảo ngược.
- Bind input qua JdbcClient, ánh xạ lựa chọn sắp xếp sang đoạn SQL cố định. Giới hạn dữ liệu trả về, sắp xếp ổn định có khóa phân định, chỉ chọn cột cần đọc và tránh truy vấn theo từng dòng.
- Bao thao tác nhiều ghi trong một transaction. Giữ ownership trong câu ghi, kiểm tra update count và dựa trên constraint cho trùng đồng thời. Kiểm tra rollback cả SQL và hiệu ứng bên ngoài DB.
- Đo số truy vấn, EXPLAIN và cardinality trên dữ liệu đại diện trước khi thêm index hay đổi pagination. Đo HikariCP và tải MySQL trước khi chỉnh pool. Mỗi worker dùng schema độc lập.
