# T4E7933 · Mức hoàn thiện hồ sơ

Trang Hồ sơ của tôi tính lại mức hoàn thiện từ dữ liệu đã lưu của sinh viên đang đăng nhập. Mỗi phần nhận toàn bộ trọng số khi đạt điều kiện. Phần chưa đạt xuất hiện trong danh sách cần bổ sung.

| Phần | Trọng số | Điều kiện đạt |
|---|---:|---|
| Thông tin cá nhân | 30% | Có họ tên, ngày sinh, điện thoại, giới thiệu, vị trí và nơi làm việc mong muốn. Tất cả đều hợp lệ theo form hồ sơ. Ảnh đại diện nằm ngoài tiêu chí. |
| Học vấn | 20% | Có ít nhất một mục với trường, chuyên ngành, ngày bắt đầu hợp lệ. Ngày kết thúc và GPA tuân theo quy tắc của form khi có nhập. |
| Kỹ năng | 20% | Có ít nhất một kỹ năng đã gắn từ danh mục, có tên, nhóm và mức từ 1 đến 5. Kỹ năng đã gắn vẫn được tính nếu quản trị viên ẩn nó khỏi danh mục thêm mới. |
| Dự án | 15% | Có ít nhất một dự án với tên, vai trò, công nghệ, mô tả và URL GitHub hợp lệ. URL demo phải hợp lệ nếu có nhập. |
| Chứng chỉ | 15% | Có ít nhất một chứng chỉ với tên, đơn vị cấp, ngày cấp hợp lệ và khóa minh chứng đã lưu. Việc tính điểm dùng dữ liệu trong hồ sơ, không đọc nội dung tệp. |

`Phần trăm = round(100 × tổng trọng số các phần đạt ÷ tổng trọng số)`. Tổng trọng số hiện tại bằng 100. Làm tròn đến số nguyên gần nhất, làm tròn lên nếu phần lẻ đúng 0,5. Với trọng số hiện tại, kết quả luôn nguyên trước khi làm tròn và nằm trong khoảng 0–100. Nhiều mục hợp lệ trong cùng phần chỉ được tính một lần. Khi mục hợp lệ cuối cùng bị xóa, điểm giảm đúng bằng trọng số của phần tương ứng.

`ProfileCompletionService` trả về phần trăm cùng danh sách phần chưa đạt. Trang hồ sơ dùng dữ liệu học vấn và kỹ năng đã tải, cộng hai lượt đọc danh sách dự án và chứng chỉ theo chủ hồ sơ. Điểm được tính động, kể cả khi hiển thị lại form có lỗi.
