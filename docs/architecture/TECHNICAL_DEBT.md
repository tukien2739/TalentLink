# Backlog nợ kỹ thuật

Mở task riêng khi điều phối chốt phạm vi và người phụ trách. Mã dưới đây tham chiếu bằng chứng và rủi ro regression trong [báo cáo audit](ARCHITECTURE_AUDIT.md).

| Thứ tự | Phạm vi task | Điều kiện hoàn thành |
|---|---|---|
| Đã xử lý trên Sprint 2 | SEC-01, bỏ OTP và email khỏi log. Reset local cần opt-in, loopback và hộp thư riêng. | Regression kiểm tra quyền tệp, đầu ra log và vòng đời OTP. Giao mã ngoài local cần task riêng trước khi mở tính năng ở môi trường đó. |
| P0 trước triển khai có HTTP/2 | DEP-01, rà soát ingress, vá dependency riêng | Có phiên bản đã đối chiếu advisory và kết quả regression sau cập nhật. |
| P1 | SEC-02, vòng đời phiên tài khoản | Có chính sách thu hồi và test phiên đang mở, remember-me và khóa tài khoản. |
| P1 | SEC-03, kiểm soát tài nguyên avatar | Kiểm tra giới hạn ảnh và định dạng hợp lệ. |
| P2 | SEC-04, ranh giới storage chứng chỉ | Kiểm tra khóa và đường dẫn trên dữ liệu mẫu cũ và mới. |
| P1 | DATA-01, QA-01 | Có test transaction/đối soát tệp và ma trận kiểm thử MySQL, phân quyền, workflow. |
| P2 | PERF-01 | Có số liệu truy vấn, tải và thống nhất ngưỡng trước khi chọn thay đổi. |
| P2 | ARC-01, UX-01 | Điều chỉnh khi chạm đúng capability, giữ hợp đồng HTTP và kiểm tra hai profile đăng nhập. |

Lịch rà soát phụ thuộc theo [tiêu chuẩn bảo mật](SECURITY_STANDARDS.md) cần dựa trên artifact thực tế. Các mục chưa đóng tiếp tục được theo dõi trong backlog.
