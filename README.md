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

Mở `http://localhost:8080/`. Trang chủ trả HTTP 200 cho khách, dùng layout chung và có tiêu đề tab **TalentLink**.

Profile `dev` lấy cấu hình kết nối từ `application-dev.properties`. Flyway áp dụng các migration có trong `src/main/resources/db/migration`, còn Hibernate kiểm tra schema khi khởi động với `ddl-auto=validate`.

## Quy ước làm việc nhóm

- Đặt tên nhánh `feature/<tên-task>`. Khi task có mã, dùng dạng `feature/<mã-task-viết-thường>-<tên-ngắn>`, ví dụ `feature/t4e791f-bootstrap-talentlink`.
- Viết commit theo dạng `<loại>: <mã-task> <mô-tả>`. Dùng `feat` cho tính năng, `fix` cho sửa lỗi và `docs` cho tài liệu. Ví dụ `feat: T4E791F khởi tạo TalentLink`.
- Mở PR từ nhánh feature vào `main`. Đặt mã task trong tiêu đề PR và điền kết quả kiểm tra theo [mẫu PR](.github/pull_request_template.md).
- Mỗi PR vào `main` cần ít nhất một thành viên khác phê duyệt trước khi merge. Quy tắc bảo vệ `main` trên GitHub thực thi yêu cầu này.

## Layout cho trang mới

Gọi fragment `fragments/layout :: page` với tiêu đề tab và phần nội dung của trang. Header, footer, Bootstrap và vùng thông báo được lấy từ layout.

```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: page('Tiêu đề · TalentLink', ~{::#page-content})}">
<body>
<section id="page-content">
    <h1>Tiêu đề trang</h1>
</section>
</body>
</html>
```

Đặt `successMessage`, `errorMessage` hoặc `infoMessage` trong model để dùng vùng thông báo. Sau khi chuyển hướng có thể đặt các giá trị này bằng `RedirectAttributes.addFlashAttribute`. Menu lấy vai trò và tên đăng nhập từ Spring Security. Khi thêm route bên dưới `/candidate`, `/recruiter` hoặc `/admin`, quy tắc bảo mật áp dụng theo vai trò tương ứng.

## Xử lý lỗi và thông báo flash

Dùng `BusinessRuleException` khi thao tác vi phạm quy tắc nghiệp vụ và `ResourceNotFoundException` khi bản ghi không tồn tại. `GlobalExceptionHandler` hiển thị trang tương ứng với HTTP 422, 404 hoặc 403 cho lỗi quyền truy cập. Spring Boot dùng các trang lỗi chung cho lỗi được chuyển qua `/error`, gồm lỗi 500. Nội dung trang lỗi dùng thông báo cố định dành cho người dùng.

Khi một thao tác cần chuyển hướng, gọi `redirectAttributes.addFlashAttribute("successMessage", "Đã lưu thay đổi.")` hoặc dùng `errorMessage` cho lỗi, rồi trả về `redirect:` đến trang đích. Layout hiển thị thông báo ở lần mở trang đó. Đăng xuất và đăng nhập thất bại đã dùng cơ chế này.
