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
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Đọc tên người gọi: ${if (enabled) "Bật" else "Tắt"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Image(
            painter = rememberAsyncImagePainter("https://res.cloudinary.com/dq64aidpx/image/upload/v1750863841/yvs2jacr2afus1y1spfn.jpg"),
            contentDescription = "Banner an toàn thông tin",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Tra cứu nhanh",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Tra SDT", Icons.Default.Search, Modifier.weight(1f)) {
                showPhoneCheckDialog = true
            }
            ServiceButton("Danh tính", Icons.Default.PersonSearch, Modifier.weight(1f)) {}
            ServiceButton("Lừa đảo", Icons.Default.Report, Modifier.weight(1f)) {}
            ServiceButton("Mạng", Icons.Default.Wifi, Modifier.weight(1f)) {}
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Chặn số", Icons.Default.Block, Modifier.weight(1f)) {}
            ServiceButton("Báo cáo", Icons.Default.Flag, Modifier.weight(1f)) {}
            ServiceButton("Website", Icons.Default.Public, Modifier.weight(1f)) {}
            ServiceButton("Tra STK", Icons.Default.Money, Modifier.weight(1f)) {}
        }

        Spacer(modifier = Modifier.height(15.dp))
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
                    onValueChange = { phoneInput = it },
                    placeholder = { Text("VD: 0912345678") },
                    singleLine = true
                )
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
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE3F2FD))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black,
                fontSize = 13.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
