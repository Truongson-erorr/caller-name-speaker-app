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

data class TipItem(
    val text: String,
    val detail: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

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
                "OTP là mật khẩu dùng một lần. Không đưa cho bất kỳ ai, kể cả nhân viên ngân hàng.",
                Color(0xFFE3F2FD),
                Icons.Default.Security
            ),
            TipItem(
                "Kiểm tra số tổng đài",
                "Chỉ tin tưởng số điện thoại chính thức của ngân hàng, nhà mạng hoặc cơ quan nhà nước.",
                Color(0xFFF1F8E9),
                Icons.Default.Call
            ),
            TipItem(
                "Cẩn thận với link lạ",
                "Không nhấn vào đường link trong SMS, email hoặc mạng xã hội nếu không chắc chắn an toàn.",
                Color(0xFFFFF3E0),
                Icons.Default.Link
            ),
            TipItem(
                "Đặt mật khẩu mạnh",
                "Mật khẩu nên dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.",
                Color(0xFFF3E5F5),
                Icons.Default.Lock
            ),
            TipItem(
                "Bật xác thực 2 bước",
                "Xác thực hai yếu tố (2FA) giúp bảo vệ tài khoản ngay cả khi mật khẩu bị lộ.",
                Color(0xFFE8F5E9),
                Icons.Default.VerifiedUser
            ),
            TipItem(
                "Không cài app lạ",
                "Chỉ tải ứng dụng từ CH Play hoặc App Store. App lạ có thể chứa mã độc.",
                Color(0xFFFFEBEE),
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
                                tint = Color(0xFF616161),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tip.text,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
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
