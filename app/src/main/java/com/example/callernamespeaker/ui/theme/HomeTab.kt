package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.callernamespeaker.viewmodel.BlacklistViewModel
import com.example.callernamespeaker.viewmodel.ReportViewModel
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import androidx.compose.foundation.verticalScroll
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

    var showBlockDialog by remember { mutableStateOf(false) }
    var blockPhoneInput by remember { mutableStateOf("") }
    val blacklistViewModel: BlacklistViewModel = viewModel()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    var smsTtsEnabled by remember { mutableStateOf(prefs.getBoolean("sms_tts_enabled", true)) }

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                return NotificationViewModel(currentUserId) as T
            }
        }
    )
    val scrollState = rememberScrollState()

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
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Đọc cảnh báo tin nhắn chứa link: ${if (smsTtsEnabled) "Bật" else "Tắt"}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Switch(
                checked = smsTtsEnabled,
                onCheckedChange = {
                    smsTtsEnabled = it
                    prefs.edit().putBoolean("sms_tts_enabled", it).apply()
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
        BannerCarousel()

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
                navController.navigate("report")
            }
            ServiceButton("Website", Icons.Default.Public, Modifier.weight(1f)) {
                navController.navigate("WebsiteScreen")
            }
            ServiceButton("Giải đáp AI", Icons.Default.Chat, Modifier.weight(1f)) {
                navController.navigate("ChatScreen")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        NewsSection(navController)
        Spacer(modifier = Modifier.height(12.dp))
        SecuritytipScreen()
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

                                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                if (userId.isNotBlank()) {
                                    notificationViewModel.addNotification(
                                        userId = userId,
                                        title = "Chặn số thành công",
                                        message = "Bạn vừa chặn số $phone"
                                    )
                                }
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
