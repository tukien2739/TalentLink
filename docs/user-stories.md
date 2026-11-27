# User story TalentLink

**Task T4E7920** · 18 user story cho ứng viên, nhà tuyển dụng và quản trị viên. Các tiêu chí bên dưới mô tả hành vi cần kiểm thử khi triển khai. Xem [wireframe tám màn hình](wireframes.html).

## Ứng viên

### UV-01 · Đăng ký

Là ứng viên, tôi muốn đăng ký bằng email và mật khẩu, để có tài khoản quản lý hồ sơ của mình.

- Khi nhập email hợp lệ chưa được sử dụng, mật khẩu đáp ứng chính sách và xác nhận trùng khớp, hệ thống tạo tài khoản với vai trò ứng viên và dẫn về màn hình đăng nhập.
- Khi dữ liệu thiếu, email đã được dùng hoặc xác nhận mật khẩu sai, biểu mẫu báo lỗi, giữ email đã nhập và xóa hai trường mật khẩu.

### UV-02 · Đăng nhập

Là ứng viên, tôi muốn đăng nhập bằng tài khoản của mình, để truy cập hồ sơ cá nhân.

- Với thông tin hợp lệ và tài khoản hoạt động, hệ thống mở Hồ sơ của tôi.
- Với thông tin sai hoặc tài khoản bị khóa, hệ thống giữ người dùng ở màn hình đăng nhập và từ chối tạo phiên.

### UV-03 · Lưu thông tin cơ bản

Là ứng viên, tôi muốn lưu tên, tiêu đề nghề nghiệp và phần giới thiệu, để nhà tuyển dụng biết tôi có kinh nghiệm gì.

- Khi lưu dữ liệu hợp lệ, hệ thống xác nhận và hiển thị lại các giá trị đã lưu khi tải lại trang.
- Khi bỏ trống họ và tên, hệ thống báo lỗi tại trường đó và giữ các giá trị đang nhập.

### UV-04 · Bổ sung năng lực

Là ứng viên, tôi muốn cập nhật học vấn, kỹ năng, dự án và chứng chỉ, để hồ sơ thể hiện năng lực của mình.

- Có thể thêm, sửa và xóa mục của từng nhóm trong Hồ sơ của tôi. Sau khi lưu, danh sách phản ánh thao tác vừa làm.
- Nếu một kỹ năng đã có trong hồ sơ, hệ thống yêu cầu cập nhật mục hiện tại. Mức kỹ năng hợp lệ nằm trong khoảng 1–5.

### UV-05 · Gửi hồ sơ để duyệt

Là ứng viên, tôi muốn gửi hồ sơ để quản trị viên duyệt, để hồ sơ có thể xuất hiện trong kết quả tìm ứng viên.

- Khi hồ sơ đủ thông tin bắt buộc, thao tác gửi chuyển hồ sơ vào hàng chờ duyệt và Hồ sơ của tôi hiển thị trạng thái mới.
- Khi còn thiếu thông tin bắt buộc, hệ thống chỉ rõ các trường cần bổ sung. Gửi lại cùng nội dung đang chờ chỉ giữ một mục trong hàng chờ.

### UV-06 · Xem phản hồi duyệt

Là ứng viên, tôi muốn xem kết quả và ghi chú duyệt, để biết hồ sơ cần chỉnh sửa ở đâu.

- Hồ sơ của tôi hiển thị quyết định gần nhất và ghi chú nếu có, sau khi quản trị viên ghi nhận quyết định.
- Với hồ sơ được yêu cầu chỉnh sửa, ứng viên có thể cập nhật và gửi lại. Hồ sơ hiển thị trạng thái chờ duyệt cho lần gửi mới.

## Nhà tuyển dụng

### NT-01 · Đăng nhập để tìm ứng viên

Là nhà tuyển dụng, tôi muốn đăng nhập bằng tài khoản tuyển dụng, để truy cập công cụ tìm ứng viên.

- Khi đăng nhập thành công bằng tài khoản hoạt động có vai trò nhà tuyển dụng, hệ thống mở màn hình Tìm ứng viên.
- Hệ thống từ chối truy cập chức năng tuyển dụng nếu phiên đăng nhập thuộc vai trò khác hoặc tài khoản đã bị khóa.

### NT-02 · Tìm và lọc hồ sơ

Là nhà tuyển dụng, tôi muốn tìm theo từ khóa và lọc theo kỹ năng cùng mức tối thiểu, để thu hẹp danh sách ứng viên phù hợp.

- Khi bấm Tìm, hệ thống áp dụng đồng thời các điều kiện đang chọn và chỉ trả những hồ sơ được phép hiển thị.
- Khi không có kết quả, hệ thống hiển thị trạng thái rỗng và giữ các bộ lọc. Thao tác Xóa bộ lọc đưa các điều kiện về mặc định.

### NT-03 · Xem chi tiết ứng viên

Là nhà tuyển dụng, tôi muốn mở hồ sơ từ kết quả tìm kiếm, để đọc phần giới thiệu và các minh chứng năng lực.

- Chọn một kết quả mở đúng hồ sơ và hiển thị phần giới thiệu, học vấn, kỹ năng, dự án và chứng chỉ có trong hồ sơ.
- Khi quay lại, các điều kiện tìm kiếm trước đó được giữ. Nếu hồ sơ không còn quyền xem, hệ thống báo hồ sơ không khả dụng.

### NT-04 · Lưu ứng viên

Là nhà tuyển dụng, tôi muốn lưu hồ sơ quan tâm, để quay lại đánh giá sau.

- Bấm Lưu ứng viên trên trang chi tiết cập nhật trạng thái nút và đưa hồ sơ vào danh sách Đã lưu của tài khoản.
- Thao tác lưu lặp lại chỉ có một mục. Khi bỏ lưu, mục này biến mất khỏi danh sách Đã lưu của tài khoản.

### NT-05 · Gửi yêu cầu phỏng vấn

Là nhà tuyển dụng, tôi muốn gửi lời mời từ hồ sơ ứng viên, để đề xuất trao đổi về một vị trí.

- Biểu mẫu hiển thị đúng ứng viên đã chọn. Khi nhập vị trí và lời nhắn hợp lệ rồi gửi, hệ thống xác nhận và tạo yêu cầu ở trạng thái Chờ phản hồi.
- Thời gian đề xuất có thể bỏ trống. Nếu đã nhập, thời gian phải ở tương lai và được hiển thị cùng múi giờ trước khi gửi.

### NT-06 · Theo dõi lời mời đã gửi

Là nhà tuyển dụng, tôi muốn xem các yêu cầu phỏng vấn mình đã gửi, để biết lời mời nào còn chờ phản hồi.

- Mục Yêu cầu đã gửi hiển thị ứng viên, vị trí và trạng thái của từng yêu cầu thuộc tài khoản hiện tại.
- Khi trạng thái thay đổi, danh sách hiển thị trạng thái mới sau khi tải lại. Tài khoản khác không truy cập được yêu cầu qua liên kết trực tiếp.

## Quản trị viên

### QT-01 · Đăng nhập vào khu vực quản trị

Là quản trị viên, tôi muốn đăng nhập vào khu vực quản trị, để quản lý tài khoản và duyệt hồ sơ.

- Khi đăng nhập bằng tài khoản quản trị hoạt động, hệ thống mở Quản trị tài khoản.
- Tài khoản thường truy cập trực tiếp địa chỉ quản trị bị từ chối ở phía máy chủ.

### QT-02 · Tìm tài khoản

Là quản trị viên, tôi muốn tìm theo email và lọc theo vai trò hoặc trạng thái khóa, để xác định đúng tài khoản cần xử lý.

- Khi áp dụng bộ lọc, danh sách hiển thị các dòng phù hợp với điều kiện và có cột email, vai trò, trạng thái.
- Khi không có kết quả, màn hình hiển thị trạng thái rỗng và cho phép xóa bộ lọc.

### QT-03 · Khóa hoặc mở khóa tài khoản

Là quản trị viên, tôi muốn khóa hoặc mở khóa tài khoản, để kiểm soát quyền đăng nhập khi cần.

- Hệ thống yêu cầu xác nhận riêng cho tài khoản đã chọn. Sau khi xác nhận, bảng cập nhật trạng thái. Hủy xác nhận giữ trạng thái cũ.
- Tài khoản bị khóa không thể đăng nhập. Mở khóa cho phép đăng nhập lại nếu thông tin hợp lệ.

### QT-04 · Xem hàng chờ duyệt

Là quản trị viên, tôi muốn xem hồ sơ chờ duyệt theo thời điểm gửi, để xử lý các hồ sơ đã chờ lâu.

- Hàng chờ hiển thị tên ứng viên, thời điểm gửi và hồ sơ chờ xử lý lâu nhất trước.
- Chọn một dòng mở nội dung đã gửi để duyệt, cùng quyết định và ghi chú trước đó nếu có.

### QT-05 · Ghi quyết định duyệt

Là quản trị viên, tôi muốn duyệt hoặc yêu cầu chỉnh sửa hồ sơ, để ứng viên nhận được kết quả xử lý.

- Bấm Duyệt hoặc Yêu cầu sửa ghi một dòng lịch sử gồm người duyệt, thời điểm và quyết định. Sau khi lưu, hồ sơ rời hàng chờ của lần gửi hiện tại.
- Yêu cầu sửa phải có lý do. Sau khi ghi quyết định, ứng viên xem được kết quả và ghi chú trên Hồ sơ của tôi.

### QT-06 · Xem lịch sử duyệt

Là quản trị viên, tôi muốn xem các lần duyệt trước của một hồ sơ, để hiểu các quyết định đã đưa ra.

- Mục Lịch sử duyệt hiển thị người duyệt, thời điểm, quyết định và ghi chú theo thứ tự mới nhất trước.
- Sau lần gửi lại và duyệt tiếp, hồ sơ có thêm một mục lịch sử. Các quyết định trước giữ nguyên nội dung.

## Đối chiếu wireframe

| Màn hình | User story |
| --- | --- |
| [Đăng nhập](wireframes.html#dang-nhap) | UV-02, NT-01, QT-01 |
| [Đăng ký](wireframes.html#dang-ky) | UV-01 |
| [Hồ sơ của tôi](wireframes.html#ho-so) | UV-03 đến UV-06 |
| [Quản trị tài khoản](wireframes.html#tai-khoan) | QT-02, QT-03 |
| [Hàng chờ duyệt](wireframes.html#hang-cho) | QT-04 đến QT-06 |
| [Tìm ứng viên](wireframes.html#tim-ung-vien) | NT-02, NT-04 |
| [Chi tiết ứng viên](wireframes.html#chi-tiet) | NT-03, NT-04 |
| [Yêu cầu phỏng vấn](wireframes.html#phong-van) | NT-05, NT-06 |

## Giả định cần xác nhận

- Đăng ký công khai dành cho ứng viên. Quy trình cấp tài khoản nhà tuyển dụng và quản trị viên cần mentor xác nhận.
- Cần chốt trường bắt buộc trước khi gửi duyệt và quy tắc hiển thị hồ sơ đã duyệt khi ứng viên gửi chỉnh sửa. Màn hình hàng chờ mô tả các hành động Duyệt và Yêu cầu sửa. Quyền và điều kiện từ chối dứt điểm cần chốt cùng ERD.
- Tên vị trí, lịch hẹn tùy chọn và cách truy cập danh sách lời mời đã gửi là đề xuất cho luồng phỏng vấn. Cần đối chiếu dữ liệu lưu và quy tắc chuyển trạng thái khi thống nhất thiết kế CSDL.
