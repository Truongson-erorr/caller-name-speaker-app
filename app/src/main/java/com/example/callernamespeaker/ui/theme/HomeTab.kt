package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel
import com.example.callernamespeaker.model.PhoneLookup
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.NewsPost
import com.example.callernamespeaker.viewmodel.BlacklistViewModel
import com.example.callernamespeaker.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(navController: NavController) {
    val viewModel: PhoneLookupViewModel = viewModel()
    val context = LocalContext.current

    val prefs = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    var enabled by remember {
        mutableStateOf(prefs.getBoolean("tts_enabled", true))
    }

    var showPhoneCheckDialog by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf("") }

    val reportViewModel: ReportViewModel = viewModel()
    var showReportDialog by remember { mutableStateOf(false) }
    var reportPhone by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("") }

    var showBlockDialog by remember { mutableStateOf(false) }
    var blockPhoneInput by remember { mutableStateOf("") }
    val blacklistViewModel: BlacklistViewModel = viewModel()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    LaunchedEffect(uid) {
        if (uid == null) {
            Toast.makeText(context, "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
            navController.navigate("LoginScreen") {
                popUpTo("main") { inclusive = true }
            }
        } else {
            viewModel.fetchHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Đọc tên người gọi: ${if (enabled) "Bật" else "Tắt"}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Switch(
                checked = enabled,
                onCheckedChange = {
                    enabled = it
                    prefs.edit().putBoolean("tts_enabled", enabled).apply()
                }
            )
        }

        Text(
            text = "Cẩm nang an toàn thông tin",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Image(
            painter = rememberAsyncImagePainter("https://res.cloudinary.com/dq64aidpx/image/upload/v1750863841/yvs2jacr2afus1y1spfn.jpg"),
            contentDescription = "Banner an toàn thông tin",
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Tra cứu nhanh",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Tra SDT", Icons.Default.Search, Modifier.weight(1f)) {
                showPhoneCheckDialog = true
            }
            ServiceButton("Danh tính", Icons.Default.PersonSearch, Modifier.weight(1f)) {}
            ServiceButton("Lừa đảo", Icons.Default.Report, Modifier.weight(1f)) {}
            ServiceButton("Khẩn cấp", Icons.Default.Wifi, Modifier.weight(1f)) {
                navController.navigate("EmergencyTab")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Chặn số", Icons.Default.Block, Modifier.weight(1f)) {
                showBlockDialog = true
            }
            ServiceButton("Báo cáo", Icons.Default.Flag, Modifier.weight(1f)) {
                showReportDialog = true
            }
            ServiceButton("Website", Icons.Default.Public, Modifier.weight(1f)) {
                navController.navigate("WebsiteScreen")
            }

            ServiceButton("Tra STK", Icons.Default.Money, Modifier.weight(1f)) {}
        }

        Spacer(modifier = Modifier.height(12.dp))
        NewsSection(navController)
    }

    if (showPhoneCheckDialog) {
        val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)

        AlertDialog(
            onDismissRequest = { showPhoneCheckDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val cleaned = phoneInput.trim().replace("+84", "0").replace(" ", "")
                    val isBlocked = prefs.contains(cleaned)
                    val type = classifyPhoneNumber(cleaned)

                    viewModel.saveLookup(cleaned, type, isBlocked)

                    Toast.makeText(
                        context,
                        "Số $cleaned (${type}) ${if (isBlocked) "đã bị chặn" else "chưa bị chặn"}",
                        Toast.LENGTH_LONG
                    ).show()

                    phoneInput = ""
                    showPhoneCheckDialog = false
                }) {
                    Text("Kiểm tra")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhoneCheckDialog = false }) {
                    Text("Hủy")
                }
            },
            title = { Text("Nhập số điện thoại") },
            text = {
                OutlinedTextField(
                    value = phoneInput,
                    shape = RoundedCornerShape(12.dp),
                    onValueChange = { phoneInput = it },
                    placeholder = { Text("VD: 0912345678") },
                    singleLine = true
                )
            }
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (reportPhone.isNotBlank() && reportReason.isNotBlank() && userId.isNotBlank()) {
                        reportViewModel.reportPhoneNumber(
                            phone = reportPhone.trim(),
                            userId = userId,
                            reason = reportReason.trim(),
                            onComplete = {
                                Toast.makeText(context, "Báo cáo thành công", Toast.LENGTH_SHORT).show()
                                showReportDialog = false
                                reportPhone = ""
                                reportReason = ""
                            },
                            onError = {
                                Toast.makeText(context, "Lỗi: $it", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }) {
                    Text("Báo cáo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Hủy")
                }
            },
            title = { Text("Báo cáo số điện thoại") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reportPhone,
                        onValueChange = { reportPhone = it },
                        label = { Text("Số điện thoại") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Lý do báo cáo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    if (showBlockDialog) {
        val cleanedPhone = blockPhoneInput.trim().replace("+84", "0").replace(" ", "")
        val isBlocked = prefs.getBoolean(cleanedPhone, false)

        AlertDialog(
            onDismissRequest = {
                showBlockDialog = false
                blockPhoneInput = ""
            },
            title = { Text("Chặn số điện thoại") },
            text = {
                Column {
                    OutlinedTextField(
                        value = blockPhoneInput,
                        onValueChange = { blockPhoneInput = it },
                        label = { Text("Nhập số điện thoại") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isBlocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Số này đã được chặn!",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val phone = blockPhoneInput.trim().replace("+84", "0").replace(" ", "")
                        if (phone.isNotBlank()) {
                            blacklistViewModel.addToBlacklist(phone, type = "unknown") {
                                Toast.makeText(context, "Đã chặn số $phone", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showBlockDialog = false
                        blockPhoneInput = ""
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Chặn")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showBlockDialog = false
                        blockPhoneInput = ""
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun ServiceButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE3F2FD))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Black,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}
