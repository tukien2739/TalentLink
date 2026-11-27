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

Profile `dev` lấy cấu hình kết nối từ `application-dev.properties`. Khi chạy trên database trống, Flyway chạy V1 trong `src/main/resources/db/migration` để tạo 13 bảng và V2 trong `src/main/resources/db/dev` để nạp dữ liệu giả lập. V2 được cấu hình riêng cho profile `dev`. Hibernate kiểm tra schema khi khởi động với `ddl-auto=validate`.

### Tài khoản mẫu trên profile dev

Mở `http://localhost:8080/login` và nhập email cùng mật khẩu mẫu `TalentLinkDev2026!`. MySQL lưu hash BCrypt có salt riêng cho từng tài khoản. Chỉ dùng bộ dữ liệu và mật khẩu mẫu trên database phát triển.

| Vai trò | Email mẫu |
| --- | --- |
| Quản trị viên | `admin@talentlink.example` |
| Đối tác tuyển dụng | `doitac01@talentlink.example`, `doitac02@talentlink.example`, `doitac03@talentlink.example` |
| Sinh viên | Từ `sinhvien01@talentlink.example` đến `sinhvien15@talentlink.example` |

V2 tạo một công ty cho mỗi đối tác, 15 hồ sơ sinh viên có học vấn, kỹ năng, dự án và chứng chỉ, cùng 30 kỹ năng trong danh mục. Bộ dữ liệu còn có các ví dụ về lịch sử duyệt, danh sách lưu, lời mời phỏng vấn, thông báo và lượt xem.

Mỗi tài khoản được chuyển tới trang chủ theo quyền sau khi đăng nhập. `ADMIN` vào `/admin`, `PARTNER` vào `/partner`, và `STUDENT` vào `/me`. Ứng dụng ánh xạ `RECRUITER` và `CANDIDATE` trong MySQL thành quyền `PARTNER` và `STUDENT`.

Tùy chọn **Giữ đăng nhập** có hiệu lực 14 ngày. Đặt biến môi trường `TALENTLINK_REMEMBER_ME_KEY` bằng một giá trị ngẫu nhiên riêng nếu cần giữ hiệu lực cookie qua lần khởi động lại. Khi biến này trống, Spring Security tạo khóa mới khi ứng dụng khởi động. Đăng xuất kết thúc phiên và xóa cookie ghi nhớ trong trình duyệt.

### Đăng ký sinh viên

Mở `http://localhost:8080/register` để tạo tài khoản sinh viên. Mật khẩu cần ít nhất 8 ký tự và tối đa 72 byte UTF-8. Khi biểu mẫu có lỗi, ứng dụng giữ họ tên và email, rồi yêu cầu nhập lại mật khẩu. Sau khi đăng ký, người dùng được chuyển về trang đăng nhập.

Migration V3 bổ sung `profiles.visibility` với giá trị mặc định `PRIVATE`. Tài khoản sinh viên mới và hồ sơ riêng tư chỉ chứa họ tên được tạo trong cùng một giao dịch.

### Đổi và đặt lại mật khẩu

Sau khi đăng nhập, chọn **Đổi mật khẩu** trên thanh điều hướng. Cần nhập đúng mật khẩu hiện tại. Sau khi đổi, ứng dụng kết thúc phiên hiện tại và yêu cầu đăng nhập lại.

Trên profile `dev`, chọn **Quên mật khẩu?** ở trang đăng nhập, nhập email rồi tìm dòng `Dev password reset OTP` trong log của ứng dụng. Nhập mã sáu số và mật khẩu mới ở trang đặt lại. Migration V4 lưu hash của OTP trong MySQL. Mã hết hạn sau 10 phút, bị hủy sau 5 lần nhập sai và chỉ dùng được một lần. Có thể yêu cầu mã mới sau 60 giây. Thông báo gửi mã giống nhau cho mọi email. Luồng này ghi OTP vào log phát triển và chỉ được bật trên profile `dev`.

### Quản trị tài khoản

Đăng nhập bằng tài khoản `ADMIN`, rồi mở `/admin/users`. Danh sách hiển thị tối đa 10 tài khoản mỗi trang và lọc được theo tên hoặc email, vai trò và trạng thái khóa. Khi tạo đối tác, nhập tên người dùng, email, mật khẩu ban đầu và tên công ty. Ứng dụng tạo công ty và tài khoản trong cùng một giao dịch. V5 bổ sung tên hiển thị của tài khoản.

Khóa hoặc mở khóa cần xác nhận trên trang riêng. Quản trị viên không thể tự khóa. Một tài khoản bị khóa sẽ mất phiên đang dùng ở lần truy cập trang tiếp theo và chỉ đăng nhập lại được sau khi mở khóa.

### Hồ sơ sinh viên

Sinh viên đăng nhập rồi mở `/me/profile` để xem và cập nhật hồ sơ của mình. Họ tên là trường bắt buộc. Ngày sinh dùng định dạng `YYYY-MM-DD`. Số điện thoại nhận 10 chữ số bắt đầu bằng `0` hoặc định dạng quốc tế bắt đầu bằng `+`. V6 bổ sung ngày sinh, số điện thoại, vị trí và nơi làm việc mong muốn cho bảng `profiles`.

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

Đặt `successMessage`, `errorMessage` hoặc `infoMessage` trong model để dùng vùng thông báo. Sau khi chuyển hướng có thể đặt các giá trị này bằng `RedirectAttributes.addFlashAttribute`. Menu lấy vai trò và email đăng nhập từ Spring Security. Khi thêm route bên dưới `/me`, `/partner` hoặc `/admin`, quy tắc bảo mật áp dụng theo vai trò tương ứng.

## Xử lý lỗi và thông báo flash

Dùng `BusinessRuleException` khi thao tác vi phạm quy tắc nghiệp vụ và `ResourceNotFoundException` khi bản ghi không tồn tại. `GlobalExceptionHandler` hiển thị trang tương ứng với HTTP 422, 404 hoặc 403 cho lỗi quyền truy cập. Spring Boot dùng các trang lỗi chung cho lỗi được chuyển qua `/error`, gồm lỗi 500. Nội dung trang lỗi dùng thông báo cố định dành cho người dùng.

Khi một thao tác cần chuyển hướng, gọi `redirectAttributes.addFlashAttribute("successMessage", "Đã lưu thay đổi.")` hoặc dùng `errorMessage` cho lỗi, rồi trả về `redirect:` đến trang đích. Layout hiển thị thông báo ở lần mở trang đó. Đăng xuất và đăng nhập thất bại đã dùng cơ chế này.

### File minh chứng chứng chỉ

Đặt `CERTIFICATE_UPLOAD_DIR` trỏ tới thư mục lưu trữ bền vững ngoài thư mục tài nguyên web.
Mặc định ứng dụng dùng `./uploads/certificates`. Khi sao lưu hồ sơ, lưu cả thư mục này cùng cơ sở dữ liệu.
