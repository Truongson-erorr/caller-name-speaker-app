package com.example.callernamespeaker.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.BlacklistViewModel
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockPhoneScreen(
    navController: NavController,
    blacklistViewModel: BlacklistViewModel = viewModel(),
    notificationViewModel: NotificationViewModel
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)
    var blockPhoneInput by remember { mutableStateOf("") }

    val cleanedPhone = blockPhoneInput.trim().replace("+84", "0").replace(" ", "")
    val isBlocked = prefs.getBoolean(cleanedPhone, false)

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
                        .padding(start = 8.dp)
                        .size(26.dp)
                )
                Text(
                    text = "Chặn số điện thoại",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            Text(
                text = "Nếu bạn muốn chặn các cuộc gọi hoặc tin nhắn từ số điện thoại nghi ngờ, hãy nhập số vào ô bên dưới. " +
                        "Sau khi chặn, hệ thống sẽ không hiển thị thông báo hoặc cảnh báo từ số này nữa.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = blockPhoneInput,
                onValueChange = { blockPhoneInput = it },
                placeholder = {
                    Text(
                        text = "Nhập số điện thoại...",
                        color = Color(0xFF9CA3AF)
                    )
                },
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF111827),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )

            if (isBlocked && cleanedPhone.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Số này đã được chặn!",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val phone = cleanedPhone
                    if (phone.isNotBlank()) {
                        blacklistViewModel.addToBlacklist(phone, type = "unknown") {
                            Toast.makeText(context, "Đã chặn số $phone", Toast.LENGTH_SHORT).show()

                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            if (userId.isNotBlank()) {
                                notificationViewModel.addNotification(
                                    userId = userId,
                                    title = "Chặn số thành công",
                                    message = "Bạn vừa chặn số $phone"
                                )
                            }
                            blockPhoneInput = ""
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text("Chặn số này")
            }
        }
    }
}
