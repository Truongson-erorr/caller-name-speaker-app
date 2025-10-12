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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel
import com.google.firebase.auth.FirebaseAuth

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

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    var smsTtsEnabled by remember { mutableStateOf(prefs.getBoolean("sms_tts_enabled", true)) }

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
                navController.navigate("SearchScreen")
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
                navController.navigate("block_phone")
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
