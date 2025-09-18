package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

data class TipItem(
    val text: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun SecuritytipScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Text(
            text = "Tiện ích mở rộng",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val tips = listOf(
            TipItem("Không chia sẻ mã OTP", Color.White, Icons.Default.Security),
            TipItem("Kiểm tra số tổng đài", Color(0xFFFFEBEE), Icons.Default.Call),
            TipItem("Cẩn thận với link lạ", Color(0xFFE8F5E9), Icons.Default.Link),
            TipItem("Đặt mật khẩu mạnh", Color(0xFFFFF3E0), Icons.Default.Lock),
            TipItem("Bật xác thực 2 bước", Color(0xFFEDE7F6), Icons.Default.VerifiedUser),
            TipItem("Không cài app lạ", Color(0xFFE0F7FA), Icons.Default.Android),
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
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = tip.color
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .size(100.dp)
                            .clickable { }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tip.icon,
                                contentDescription = null,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tip.text,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
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
}
