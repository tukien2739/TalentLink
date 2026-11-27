# Mã nguồn và giao diện

- Tìm route, form, validator, truy vấn và fragment hiện có. Dùng tên theo nghiệp vụ, hậu tố Form cho dữ liệu HTTP, constructor injection. Dùng Optional cho kết quả có thể thiếu và xử lý SQL nullable rõ ràng.
- Bean Validation giữ ràng buộc định dạng, service giữ quy tắc workflow, DB giữ toàn vẹn dữ liệu. Mỗi quy tắc có một nơi quyết định. Đặt tên và đơn vị cho giới hạn nghiệp vụ. Đối chiếu form, UI và DB khi thay đổi giới hạn.
- Ánh xạ lỗi về field, giữ dữ liệu an toàn và redirect kèm flash sau thành công. Giữ nguyên nguyên nhân kỹ thuật trong xử lý nội bộ. Tránh catch rộng và chỉ tách lớp khi trách nhiệm thay đổi độc lập hoặc cần dùng lại thực tế. Xin thống nhất trước khi đổi kiến trúc hoặc dependency.
- Dùng layout, alerts, stylesheet và Bootstrap có sẵn. HTML có ngữ nghĩa, label gắn input, lỗi có liên kết, focus rõ và thao tác được bằng bàn phím. Bảng cuộn trên màn hình nhỏ. Hiển thị trạng thái rỗng, lỗi, thành công tại điểm thao tác và báo đang tải khi có độ trễ đáng kể.
- JavaScript tăng cường tương tác. Backend quyết định kết quả validation. Kiểm tra cả luồng nhập liệu bằng bàn phím và giao diện mobile trước khi nộp.
