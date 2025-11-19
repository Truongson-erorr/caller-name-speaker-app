package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callernamespeaker.model.TipItem

@Composable
fun SecuritytipScreen() {
    var selectedTip by remember { mutableStateOf<TipItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Text(
            text = "Mẹo bảo mật",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )

        val tips = listOf(
            TipItem(
                "Không chia sẻ mã OTP",
                """
        OTP (One-Time Password) là mật khẩu chỉ dùng một lần để xác thực giao dịch hoặc đăng nhập. 
        Hacker/thủ đoạn lừa đảo thường giả danh ngân hàng hoặc người quen để xin OTP của bạn. 
        Hãy nhớ: Ngân hàng KHÔNG bao giờ yêu cầu khách hàng cung cấp OTP.

        Các bước thực hiện an toàn:
        1. Không đọc mã OTP cho bất kỳ ai, kể cả người tự xưng là nhân viên ngân hàng.  
        2. Luôn giữ bí mật tin nhắn OTP trên điện thoại.  
        3. Nếu nghi ngờ bị lộ OTP, hãy liên hệ ngay với ngân hàng để khóa dịch vụ.  
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.Security
            ),
            TipItem(
                "Kiểm tra số tổng đài",
                """
        Hiện nay kẻ gian có thể giả mạo số điện thoại giống tổng đài thật để gọi lừa đảo. 
        Trước khi nghe máy hoặc cung cấp thông tin, hãy xác minh kỹ số gọi đến.

        Các bước thực hiện an toàn:
        1. Kiểm tra số gọi đến có đúng là tổng đài chính thức (trên website ngân hàng, nhà mạng).  
        2. Không tin các số lạ tự xưng nhân viên hỗ trợ.  
        3. Nếu nghi ngờ, hãy chủ động gọi lại đến số tổng đài chính thức.  
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.Call
            ),
            TipItem(
                "Cẩn thận với link lạ",
                """
        Hacker thường gửi đường link giả mạo qua SMS, email, Zalo hoặc Facebook để đánh cắp thông tin. 
        Link này có thể dẫn đến website giả mạo hoặc chứa mã độc.

        Các bước thực hiện an toàn:
        1. Không nhấp vào link trong tin nhắn lạ hoặc email không rõ nguồn gốc.  
        2. Kiểm tra đường link có phải HTTPS và tên miền chính thức không.  
        3. Nếu muốn xác minh, hãy nhập trực tiếp địa chỉ website vào trình duyệt thay vì nhấp link.  
        Ngoài ra, bạn có thể truy cập vào Website ở phần tra cứu nhanh để kiểm tra độ an toàn của các đường link mà bạn thắc mắc.
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.Link
            ),
            TipItem(
                "Đặt mật khẩu mạnh",
                """
        Một mật khẩu mạnh giúp bảo vệ tài khoản trước hacker. 
        Mật khẩu yếu (123456, ngày sinh, tên riêng) rất dễ bị dò ra.

        Các bước tạo mật khẩu mạnh:
        1. Dùng ít nhất 8–12 ký tự.  
        2. Kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt.  
        3. Không dùng cùng một mật khẩu cho nhiều tài khoản.  
        4. Định kỳ thay đổi mật khẩu (3–6 tháng/lần).  
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.Lock
            ),
            TipItem(
                "Bật xác thực 2 bước",
                """
        Xác thực hai yếu tố (2FA) là lớp bảo vệ bổ sung: ngoài mật khẩu, bạn cần thêm mã từ SMS hoặc app xác thực. 
        Ngay cả khi mật khẩu bị lộ, tài khoản vẫn được bảo vệ.

        Các bước kích hoạt:
        1. Vào cài đặt bảo mật của tài khoản (Gmail, Facebook, ngân hàng, v.v.).  
        2. Chọn bật "Xác thực 2 bước" hoặc "Two-Factor Authentication".  
        3. Chọn phương thức: SMS OTP hoặc ứng dụng Google Authenticator.  
        4. Luôn giữ mã khôi phục ở nơi an toàn.  
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.VerifiedUser
            ),
            TipItem(
                "Không cài app lạ",
                """
        Ứng dụng từ nguồn không rõ (file APK, link tải ngoài) có thể chứa mã độc đánh cắp dữ liệu. 
        Chỉ nên tải app từ CH Play hoặc App Store.

        Các bước an toàn:
        1. Chỉ cài ứng dụng từ cửa hàng chính thức (Google Play, App Store).  
        2. Kiểm tra số lượt tải, đánh giá và nhà phát triển trước khi cài.  
        3. Không cấp quyền truy cập nhạy cảm cho ứng dụng lạ (danh bạ, SMS, camera...).  
        4. Nếu thấy app khả nghi, gỡ cài đặt ngay và quét virus.  
        """.trimIndent(),
                Color(0xFFE3F2FD),
                Icons.Default.Android
            ),
        )

        tips.chunked(2).forEach { rowTips ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowTips.forEach { tip ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = tip.color),
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp)
                            .height(110.dp)
                            .clickable { selectedTip = tip },
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tip.icon,
                                contentDescription = null,
                                tint = Color(0xFF2A2AFC),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tip.text,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }
                }
                if (rowTips.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    if (selectedTip != null) {
        AlertDialog(
            onDismissRequest = { selectedTip = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = selectedTip!!.icon,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(22.dp).padding(end = 6.dp)
                    )
                    Text(
                        text = selectedTip!!.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Text(
                    text = selectedTip!!.detail,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedTip = null }) {
                    Text("Đóng", fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
