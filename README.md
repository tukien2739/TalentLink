# TalentLink

## Chạy trên máy local

Cài JDK 17 và đặt `JAVA_HOME` trỏ đến JDK đó. Trên macOS, có thể chạy `export JAVA_HOME="$(/usr/libexec/java_home -v 17)"`. Từ thư mục gốc của dự án, chạy `./mvnw --version` (Windows dùng `.\mvnw.cmd --version`) để xác nhận Maven dùng Java 17. Maven Wrapper sẽ tải phiên bản Maven của dự án nếu máy chưa có bản đã tải.

Khởi động MySQL. Nếu chưa có database và tài khoản cho TalentLink, đăng nhập bằng tài khoản quản trị MySQL và chạy khối SQL sau. Đổi `your-local-password` thành mật khẩu bạn chọn.

```sql
CREATE DATABASE IF NOT EXISTS talentlink_dev CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'talentlink_dev'@'127.0.0.1' IDENTIFIED BY 'your-local-password';
GRANT ALL PRIVILEGES ON talentlink_dev.* TO 'talentlink_dev'@'127.0.0.1';
```

Đặt các biến môi trường sau trong terminal dùng để chạy ứng dụng. Nếu tài khoản đã tồn tại, dùng thông tin đăng nhập hiện tại của bạn.

```sh
export SPRING_PROFILES_ACTIVE=dev
export DB_URL='jdbc:mysql://127.0.0.1:3306/talentlink_dev'
export DB_USERNAME='talentlink_dev'
export DB_PASSWORD='your-local-password'
```

Trên Windows PowerShell, đặt các biến theo dạng `$env:DB_USERNAME = 'talentlink_dev'`. Có thể bỏ `DB_PASSWORD` nếu tài khoản MySQL local dùng mật khẩu rỗng.

Từ thư mục gốc của dự án, chạy các lệnh sau để build và khởi động ứng dụng.

```sh
./mvnw clean package
./mvnw spring-boot:run
```

Trên Windows, dùng `.\mvnw.cmd clean package` và `.\mvnw.cmd spring-boot:run`.

Mở `http://localhost:8080/`. Trang chủ trả HTTP 200 cho khách, phần thân trống và tab có tiêu đề **TalentLink**.

Profile `dev` lấy cấu hình kết nối từ `application-dev.properties`. Khi chạy trên database trống, Flyway chạy V1 trong `src/main/resources/db/migration` để tạo 13 bảng và V2 trong `src/main/resources/db/dev` để nạp dữ liệu giả lập. V2 được cấu hình riêng cho profile `dev`. Hibernate kiểm tra schema khi khởi động với `ddl-auto=validate`.

### Tài khoản mẫu trên profile dev

Các tài khoản dưới đây dùng HTTP Basic và cùng mật khẩu mẫu `TalentLinkDev2026!`. MySQL lưu hash BCrypt có salt riêng cho từng tài khoản. Chỉ dùng bộ dữ liệu và mật khẩu mẫu trên database phát triển.

| Vai trò | Email mẫu |
| --- | --- |
| Quản trị viên | `admin@talentlink.example` |
| Đối tác tuyển dụng | `doitac01@talentlink.example`, `doitac02@talentlink.example`, `doitac03@talentlink.example` |
| Sinh viên | Từ `sinhvien01@talentlink.example` đến `sinhvien15@talentlink.example` |

V2 tạo một công ty cho mỗi đối tác, 15 hồ sơ sinh viên có học vấn, kỹ năng, dự án và chứng chỉ, cùng 30 kỹ năng trong danh mục. Bộ dữ liệu còn có các ví dụ về lịch sử duyệt, danh sách lưu, lời mời phỏng vấn, thông báo và lượt xem.

## Quy ước làm việc nhóm

- Đặt tên nhánh `feature/<tên-task>`. Khi task có mã, dùng dạng `feature/<mã-task-viết-thường>-<tên-ngắn>`, ví dụ `feature/t4e791f-bootstrap-talentlink`.
- Viết commit theo dạng `<loại>: <mã-task> <mô-tả>`. Dùng `feat` cho tính năng, `fix` cho sửa lỗi và `docs` cho tài liệu. Ví dụ `feat: T4E791F khởi tạo TalentLink`.
- Mở PR từ nhánh feature vào `main`. Đặt mã task trong tiêu đề PR và điền kết quả kiểm tra theo [mẫu PR](.github/pull_request_template.md).
- Mỗi PR vào `main` cần ít nhất một thành viên khác phê duyệt trước khi merge. Quy tắc bảo vệ `main` trên GitHub thực thi yêu cầu này.
