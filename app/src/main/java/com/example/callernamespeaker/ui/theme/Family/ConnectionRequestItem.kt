package com.example.callernamespeaker.ui.theme.Family

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callernamespeaker.model.ConnectionRequest

@Composable
fun ConnectionRequestItem(
    request: ConnectionRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDEE7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = request.fromName.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B6EF6)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = request.fromName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E2D)
                    )
                )
                Text(
                    text = request.fromPhone,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Đã gửi lời mời kết nối",
                    fontSize = 12.sp,
                    color = Color(0xFF3B6EF6)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B6EF6))
                ) {
                    Text("Chấp nhận", fontSize = 13.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Từ chối", fontSize = 13.sp)
                }
            }
        }
    }
}
