# Caller Name Speaker

Ứng dụng Caller Name Speaker được xây dựng nhằm hỗ trợ người dùng nhận biết cuộc gọi và tin nhắn đến mà không cần nhìn vào điện thoại. Hệ thống sử dụng các API của Android như Text-to-Speech, BroadcastReceiver và AI xử lý nội dung để mang lại trải nghiệm rảnh tay, an toàn và tiện lợi.

# Quy trình xử lý trong hệ thống

- Hệ thống lắng nghe sự kiện cuộc gọi đến thông qua BroadcastReceiver  
- Kiểm tra số điện thoại trong danh bạ và danh sách blacklist  
- Xác định trạng thái số: trong danh bạ / số lạ / bị chặn  
- Sử dụng Text-to-Speech để đọc nội dung phù hợp  
- Tin nhắn SMS được phân tích nội dung để phát hiện URL hoặc nội dung bất thường  
- Nếu phát hiện rủi ro, hệ thống tạo cảnh báo bằng giọng nói  
- Dữ liệu số điện thoại có thể được tra cứu và lưu lịch sử  
- Các tính năng AI xử lý nội dung tin nhắn và website để đánh giá mức độ an toàn  
- Kết quả được hiển thị và thông báo theo thời gian thực  


# Pipeline xử lý hệ thống

- Input: cuộc gọi đến / SMS đến  
- Tiền xử lý: kiểm tra danh bạ + blacklist + phân tích nội dung  
- Xử lý logic: phân loại số (contact / unknown / blocked)  
- AI processing: phân tích SMS / website (spam, lừa đảo, nguy hiểm)  
- Output: thông báo + giọng nói TTS + cảnh báo UI  
- Lưu trữ: lịch sử tra cứu và đánh giá số điện thoại  
- UI: hiển thị thông tin và cảnh báo người dùng  


# Lợi ích hệ thống

- Hoạt động realtime ngay khi có cuộc gọi / SMS  
- Không cần mở ứng dụng vẫn hoạt động  
- Tăng độ an toàn và trải nghiệm rảnh tay  
- Hỗ trợ mở rộng AI trong tương lai  


# Đối tượng sử dụng

- Người lái xe / di chuyển nhiều  
- Người cần hỗ trợ rảnh tay  
- Người lớn tuổi  
- Người dùng cần cảnh báo spam và lừa đảo  
- Gia đình muốn theo dõi an toàn liên lạc  

# Các tính năng chính

## Cuộc gọi

- Đọc tên người gọi trong danh bạ  
- Đọc số lạ nếu không có trong danh bạ  
- Cảnh báo cuộc gọi bị chặn  
- Cảnh báo cuộc gọi từ số nghi ngờ  

## Tin nhắn

- Đọc nội dung SMS  
- Cảnh báo SMS chứa URL lạ  
- Phân tích nội dung SMS bằng AI  

## Khẩn cấp

- Chế độ gọi khẩn cấp  
- Gửi vị trí GPS qua SMS  
- Tự động gửi thông tin cho người thân  

## AI & An toàn

- Phân tích website độc hại  
- Phân tích tin nhắn spam / lừa đảo  
- Đánh giá số điện thoại từ cộng đồng  

## Gia đình & cộng đồng

- Kết nối người thân  
- Cảnh báo khi người thân nhận cuộc gọi lạ  
- Xem đánh giá số điện thoại từ cộng đồng  

## Lịch sử & tra cứu

- Lưu lịch sử tra cứu số điện thoại  
- Hiển thị thông tin số đã tìm kiếm  
- Quản lý danh sách đánh giá  

# Cơ chế chặn toàn hệ thống (Server-based Spam Score)

Hệ thống Caller Name Speaker không chỉ chặn số điện thoại ở local mà còn sử dụng cơ chế phân tích tập trung (server-side scoring) để đánh giá mức độ spam của một số điện thoại dựa trên dữ liệu cộng đồng, lịch sử cuộc gọi và hành vi người dùng.

Cơ chế này giúp phát hiện số rác / spam / lừa đảo ngay cả khi người dùng chưa từng chặn trước đó.

# Quy trình tính điểm Spam Score

- Khi có cuộc gọi đến, hệ thống bắt sự kiện thông qua BroadcastReceiver  
- Chuẩn hóa số điện thoại (ví dụ +84 → 0)  
- Kiểm tra số có trong danh bạ hay không  
- Ghi nhận lịch sử cuộc gọi vào hệ thống thống kê (CallStats)  
- Truy vấn dữ liệu báo cáo cộng đồng (Reports)  
- Tính toán Spam Score dựa trên nhiều yếu tố  
- Nếu vượt ngưỡng → kích hoạt cảnh báo hoặc chặn  

## Công thức tính Spam Score

- Tần suất gọi (Call Frequency)
  - ≥ 10 cuộc gọi → +5 điểm  
  - ≥ 5 cuộc gọi → +3 điểm  

- Báo cáo từ cộng đồng (Reports)
  - Mỗi report → +3 điểm  

- Trạng thái danh bạ
  - Không có trong danh bạ → +2 điểm  

## Logic xử lý chặn cuộc gọi

- Nếu Spam Score >= 10:
  - Hiển thị cảnh báo nguy cơ spam cao  
  - Kích hoạt Text-to-Speech cảnh báo người dùng  
  - Hiển thị Toast warning trên màn hình  
  - Không phát thông báo bình thường  

- Nếu Spam Score < 10:
  - Đọc tên người gọi bình thường (TTS)

# Use case

<p align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778751319/z7825267209444_edb7a404fcb51c79cb37c6435995b260_xnb5kj.jpg" width="60%"/>
</p>

# Sơ đồ ERD

<p align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778751320/z7825267209446_52290aa1bfd25259b7f6ea5809f97c77_wwbgno.jpg" width="70%"/>
</p>

# Giao diện các màn hình ứng dụng

## Xác thực người dùng

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131288474_0d4afbd13a7f02dbb0025e2e5e79725b_loihv7.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131207775_6215896abfa8bd478dd88d41ce4c7fca_fksemh.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131021703_868b75d94660a17e90145bde38f5c92a_qjgdro.jpg" width="23%"/>
</div>

## Trang chủ & Bài báo

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825131983170_7be2b5706f69913e716d12f126d12e00_rhwght.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825132030433_aefe47a9d5c8bce1b8b93cd97dfe840c_fbe29i.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825132030429_1bd84cc33138c84f9d6b1c67d1712b29_ay65az.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825131956055_085de8a3c3547619195510211d419e83_n7be03.jpg" width="23%"/>
</div>

## Tra cứu, chặn số, số khẩn cấp

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131140988_835bf66be2c6db59140feca7cd8652df_gkdhux.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131237574_397bff53f630af36c93b91472f3a0d9f_skhbjf.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131453808_546da16a374f356f7542d6cd7db407ea_ywjruj.jpg" width="23%"/>
</div>

## Báo cáo, Website, Hỗ trợ

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750405/z7825131171295_7ca4f6200746340bfd6bb84805b66cce_ahukc2.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825130991202_7fabbb1e2f6c4cb11888de64c10d5e05_n8fqaa.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825130991201_d13d1f52e25f352890d2b600ae0fdd45_bapxls.jpg" width="23%"/>
</div>

## Kết nối gia đình & Cảnh báo

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131469830_e943d6d86dd84125010d47da40afcd32_sglwmz.jpg" width="18%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131469831_40d22f448cd8fc82a79c91da16be2b37_x3j6rw.jpg" width="18%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131288469_ed9541341a62a59c323d361a3802743b_lfvld1.jpg" width="18%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131091248_a7b0ffb41d0ee8a2526dab771a5acf6f_pzyukz.jpg" width="18%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825131288476_2d7941c5d6c1e8e97ea76a4ab4c3c153_upc5nv.jpg" width="18%"/>
</div>

## Danh sách chặn, chia sẻ & hồ sơ người dùng

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131505665_a5c3f809f22b7e9cc8c994ee31fcafa0_tfilf2.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131691877_2ac41237b26ec0dc79c0d885bb05a73c_n4fcte.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131840011_26347d30c6950597d79c8aa18594f778_xmnppv.jpg" width="23%"/>
</div>

## Trung tâm lịch sử & đánh giá cộng đồng

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825131727228_ab25edb21fb2418b15e015205246069f_wayqyj.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825131789375_555b34ec8a330373276e7a24e5ce22cd_zhi7qr.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750407/z7825131641472_073f78e52052c0a547376fb7d1f9aac0_wzgss0.jpg" width="23%"/>
</div>

## Phân tích SMS chứa liên kết lạ & kịch bản lừa đảo

<div align="left">
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825159976895_54c8f803f3604cde9876816eacc98310_xq2cxp.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750408/z7825159976888_fb67a023082bdf59ac573aee9a3ef78b_hhl9g1.jpg" width="23%"/>
  <img src="https://res.cloudinary.com/dq64aidpx/image/upload/v1778750406/z7825159988200_07d3dd983a4c40a89d434b232e19581b_sskwpy.jpg" width="23%"/>
</div>


# Hướng phát triển hệ thống Caller Name Speaker

## Nâng cấp AI (AI Intelligence Layer)

### Cải thiện mô hình AI
- Huấn luyện thêm dữ liệu về spam, lừa đảo, phishing
- Nâng cao độ chính xác trong phân tích SMS và website
- Cải thiện khả năng nhận diện ngữ cảnh tin nhắn phức tạp

### AI hỗ trợ người dùng
- Giải thích lý do số bị cảnh báo
- Đề xuất hành động: chặn, báo cáo hoặc bỏ qua


## Bảo mật hệ thống

### Chống giả mạo
- Phát hiện spoofing số điện thoại
- Xác thực nguồn cuộc gọi

### Bảo mật dữ liệu
- Mã hóa dữ liệu người dùng
- Bảo vệ thông tin cá nhân

### Chống spam hệ thống
- Ngăn report giả từ bot
- Kiểm soát dữ liệu cộng đồng

## Tối ưu hiệu năng

### Hiệu suất ứng dụng
- Giảm độ trễ xử lý cuộc gọi
- Tối ưu service chạy nền

### Tối ưu tài nguyên
- Giảm tiêu thụ pin
- Lazy loading dữ liệu AI

### Offline mode
- Hỗ trợ một số tính năng khi không có mạng


