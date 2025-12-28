package com.example.callernamespeaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.IdentityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsAnalysisScreen(
    navController: NavController,
    viewModel: IdentityViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.result.collectAsState()
    val riskLevel by viewModel.riskLevel.collectAsState()

    var smsContent by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        .size(26.dp)
                )

                Text(
                    text = "Phân tích tin nhắn SMS",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            Text(
                text = "Hãy dán nội dung tin nhắn SMS bạn muốn kiểm tra vào ô bên dưới, sau đó nhấn nút \"Phân tích\". " +
                        "Hệ thống AI sẽ đánh giá mức độ an toàn và phát hiện các tin nhắn lừa đảo, giả mạo ngân hàng hoặc chứa liên kết độc hại.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "⚠️ Không cung cấp mã OTP, mật khẩu hay thông tin cá nhân qua SMS. " +
                        "Không nhấp vào liên kết lạ để tránh bị chiếm đoạt tài khoản.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = smsContent,
                onValueChange = { smsContent = it },
                placeholder = {
                    Text(
                        "Dán nội dung tin nhắn cần phân tích...",
                        color = Color.Gray
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF111827),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (smsContent.isNotBlank()) {
                        viewModel.analyzeIdentity(smsContent)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2AFC),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Phân tích")
            }
            Spacer(modifier = Modifier.height(24.dp))

            result?.let { analysis ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A202C)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Kết quả phân tích",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Mức độ rủi ro: $riskLevel",
                            fontWeight = FontWeight.SemiBold,
                            color = when (riskLevel) {
                                "Cao" -> Color.Red
                                "Trung bình" -> Color(0xFFFF9800)
                                else -> Color(0xFF2E7D32)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = analysis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "AI đang phân tích tin nhắn...",
                        color = Color.White
                    )
                }
            }
        }
    }
}
