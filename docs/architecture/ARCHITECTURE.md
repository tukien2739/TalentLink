# Kiến trúc TalentLink

## Hiện trạng Sprint 1

Một ứng dụng Spring Boot trên Java 17, MVC render phía server bằng Thymeleaf và Bootstrap WebJars. MySQL lưu dữ liệu, Flyway quản lý schema. Avatar là BLOB trong hồ sơ. Minh chứng chứng chỉ nằm trên ổ đĩa, DB lưu khóa tệp. Xem [README](../../README.md) để chạy và [ERD](../erd.md) để hiểu mô hình. Migration quyết định schema thực tế.

| Phạm vi | Điểm vào và phụ thuộc |
|---|---|
| Tài khoản | SecurityConfig và LockedAccountFilter đọc users. StudentRegistrationService ghi users và profiles cùng transaction. AccountPasswordService ghi users và password_reset_codes. |
| Quản trị | AdminUsersController → AdminAccountService → users, companies và profiles. AdminSkillsController → SkillRepository. CertificateController cung cấp màn chứng chỉ quản trị. |
| Hồ sơ | StudentProfileController gọi service hồ sơ, avatar và các repository kỹ năng. Học vấn qua StudentEducationService. Dự án qua StudentProjectRepository. Chứng chỉ qua CertificateService và CertificateRepository. |
| Đối tác | NavigationController phục vụ workspace. V1 có bảng shortlist, phỏng vấn, duyệt hồ sơ, thông báo và lượt xem. Dùng route và test để xác định workflow đã triển khai. |

Package chia theo tầng. Một số service giữ SQL, một số controller dùng repository trực tiếp. DTO record phục vụ bind form. Projection SQL thường nằm cạnh truy vấn. Configuration, validators và exception handler cung cấp hành vi dùng chung. Các luồng trên không cho thấy vòng phụ thuộc bean trực tiếp. Repository nhận form và enum định nghĩa trong DTO làm ranh giới kém rõ.

## Kiến trúc mục tiêu

Giữ modular monolith một tiến trình triển khai và một DB. Phân định quyền sở hữu theo tài khoản, hồ sơ, danh mục kỹ năng và từng workflow đối tác khi xuất hiện. Giữ package hiện tại với task nhỏ. Nếu cần chuyển sang package theo nghiệp vụ, đề xuất phạm vi và regression trước khi di chuyển.

Controller bind form, lấy principal, điều hướng và render. Service sở hữu quy tắc nhiều bước, quyền theo đối tượng và transaction. Repository sở hữu SQL và projection. Đọc đơn giản có thể đi trực tiếp tới repository khi quyền và dữ liệu trả về đã rõ. Khi cần hành vi liên module, gọi điểm vào nghiệp vụ của module sở hữu và ghi phụ thuộc mới trong PR. Tránh gọi ngược và interface chuyển tiếp thiếu nhu cầu.
