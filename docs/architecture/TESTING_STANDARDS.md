# Kiểm thử và Quality Gate

Đặt test theo capability. Viết ca mới cho chức năng và lỗi được sửa. Assert kết quả nghiệp vụ và tác động dữ liệu. Mockito phù hợp với điều phối, MockMvc có filter thực cho RBAC và CSRF. Dùng MySQL cho constraint, transaction và Flyway. Dùng HTTP và trình duyệt cho render, upload và trải nghiệm. Mỗi worker dùng fixture giả độc lập.

| Tiêu chí | Bằng chứng trước khi hoàn thành |
|---|---|
| 1. Acceptance criteria | Mỗi tiêu chí có test hoặc quan sát. |
| 2. Kiến trúc | Review trách nhiệm, phụ thuộc, vùng dùng chung. |
| 3. Trùng lặp | Tìm quy tắc, route, SQL và fragment hiện có. |
| 4. Bảo mật | Kiểm tra input, role, ownership, CSRF theo phạm vi. |
| 5. Regression | Chạy workflow chịu ảnh hưởng, kiểm tra dữ liệu giữ nguyên khi từ chối. |
| 6. Build | ./mvnw clean package trên JDK 17. |
| 7. Test liên quan | Ghi số ca đạt, lỗi, bỏ qua và môi trường. |
| 8. Migration | Khi đổi schema, kiểm tra Flyway mới và nâng cấp trên MySQL. |
| 9. Git diff | Review worktree, staged diff, diff so với base và git diff --check. |
| 10. Secrets | Review staged diff cho bí mật và fixture thật. |
| 11. PR minh chứng | Theo yêu cầu task, xác minh base, head SHA, scope và URL. Ghi ngoại lệ khi task cấm PR. |
| 12. Giới hạn | Ghi ca chưa chạy và tác động. |

Maven/Surefire và Git tự động hóa build, kết quả test và whitespace. Flyway kiểm tra lịch sử và chạy migration với schema riêng. [Mẫu PR](../../.github/pull_request_template.md) thu thập minh chứng. Kiến trúc, bảo mật, mức đầy đủ của assertions và UI cần review theo ngữ cảnh. Giữ pipeline hiện tại trong đợt audit.
