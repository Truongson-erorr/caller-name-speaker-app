package com.example.callernamespeaker.ui.theme

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import com.example.callernamespeaker.viewmodel.ReportViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    reportViewModel: ReportViewModel = viewModel(),
    notificationViewModel: NotificationViewModel
) {
    val context = LocalContext.current

    var reportPhone by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(35.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        .padding(start = 8.dp)
                        .size(26.dp)
                )
                Text(
                    text = "Báo cáo số điện thoại",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }

            Text(
                text = "Nếu bạn nhận được cuộc gọi hoặc tin nhắn nghi ngờ là lừa đảo, hãy nhập số điện thoại và lý do vào bên dưới. " +
                        "Thông tin sẽ giúp hệ thống cải thiện khả năng phát hiện số nguy hiểm. " +
                        "Ví dụ: bạn có thể nhập số 090xxxxxxx và lý do như 'Giả mạo ngân hàng', 'Gọi quảng cáo liên tục' v.v.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reportPhone,
                onValueChange = { reportPhone = it },
                label = { Text("Số điện thoại") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reportReason,
                onValueChange = { reportReason = it },
                label = { Text("Lý do báo cáo") },
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (reportPhone.isNotBlank() && reportReason.isNotBlank() && userId.isNotBlank()) {
                        reportViewModel.reportPhoneNumber(
                            phone = reportPhone.trim(),
                            userId = userId,
                            reason = reportReason.trim(),
                            onComplete = {
                                Toast.makeText(context, "Báo cáo thành công", Toast.LENGTH_SHORT).show()

                                notificationViewModel.addNotification(
                                    userId = userId,
                                    title = "Báo cáo số điện thoại",
                                    message = "Bạn vừa báo cáo số ${reportPhone.trim()} với lý do: ${reportReason.trim()}"
                                )

                                reportPhone = ""
                                reportReason = ""
                                navController.popBackStack()
                            },
                            onError = {
                                Toast.makeText(context, "Lỗi: $it", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gửi báo cáo")
            }
        }
    }
}
