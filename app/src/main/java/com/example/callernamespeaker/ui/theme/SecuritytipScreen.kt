package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
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

    val background = Color(0xFF0A0F1F)
    val cardColor = Color(0xFF111829)
    val iconColor = Color(0xFF3A86FF)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB7C8FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(top = 12.dp)
    ) {
        Text(
            text = "Mẹo bảo mật",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 12.dp, bottom = 10.dp)
        )

        val tips = listOf(
            TipItem("Bảo mật otp cá nhân", "...", cardColor, Icons.Default.Security),
            TipItem("Kiểm tra số tổng đài", "...", cardColor, Icons.Default.Call),
            TipItem("Cẩn thận với link lạ", "...", cardColor, Icons.Default.Link),
            TipItem("Đặt mật khẩu mạnh", "...", cardColor, Icons.Default.Lock),
            TipItem("Bật xác thực 2 bước", "...", cardColor, Icons.Default.VerifiedUser),
            TipItem("Không cài app lạ", "...", cardColor, Icons.Default.Android)
        )

        tips.chunked(2).forEach { rowTips ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                rowTips.forEach { tip ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .height(130.dp)
                            .clickable { selectedTip = tip },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = tip.icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(34.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = tip.text,
                                fontSize = 13.sp,
                                maxLines = 2,
                                fontWeight = FontWeight.SemiBold,
                                color = textSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                lineHeight = 16.sp
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
                        tint = Color(0xFF4A9CFF),
                        modifier = Modifier
                            .size(26.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = selectedTip!!.text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                }
            },
            text = {
                Text(
                    text = selectedTip!!.detail,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFFDCE6FF)
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedTip = null }) {
                    Text("Đóng", fontWeight = FontWeight.Bold, color = Color(0xFF4A9CFF))
                }
            },
            containerColor = Color(0xFF0F1A2F),
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
