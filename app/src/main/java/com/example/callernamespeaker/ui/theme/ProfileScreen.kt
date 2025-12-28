package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authUser = FirebaseAuth.getInstance().currentUser
    val uid = authUser?.uid

    val prefs = remember {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    var ttsEnabled by remember { mutableStateOf(prefs.getBoolean("tts_enabled", true)) }
    var smsTtsEnabled by remember { mutableStateOf(prefs.getBoolean("sms_tts_enabled", true)) }

    var autoBlockScam by remember { mutableStateOf(prefs.getBoolean("auto_block_scam", false)) }
    var warnUnknownCall by remember { mutableStateOf(prefs.getBoolean("warn_unknown_call", false)) }
    var ttsContactName by remember { mutableStateOf(prefs.getBoolean("tts_contact_name", false)) }
    var ttsRingingWarning by remember { mutableStateOf(prefs.getBoolean("tts_ringing_warning", false)) }

    var userProfile by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        if (uid == null) {
            isLoading = false
            return@LaunchedEffect
        }

        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                userProfile = doc.toObject(User::class.java)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Thông tin cá nhân",
            color = Color.White,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E2E2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.LightGray,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(
                text = userProfile?.name ?: "Người dùng",
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = userProfile?.email ?: authUser?.phoneNumber ?: "",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        SettingSwitch(
            title = "Đọc tên người gọi",
            enabled = ttsEnabled
        ) {
            ttsEnabled = it
            prefs.edit().putBoolean("tts_enabled", it).apply()
        }

        SettingSwitch(
            title = "Đọc cảnh báo SMS chứa link",
            enabled = smsTtsEnabled
        ) {
            smsTtsEnabled = it
            prefs.edit().putBoolean("sms_tts_enabled", it).apply()
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chặn & cảnh báo",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        SettingSwitch(
            title = "Chế độ dành cho người lớn tuổi",
            enabled = autoBlockScam
        ) {
            autoBlockScam = it
            prefs.edit().putBoolean("auto_block_scam", it).apply()
        }

        SettingSwitch(
            title = "Tự động chặn số lừa đảo",
            enabled = autoBlockScam
        ) {
            autoBlockScam = it
            prefs.edit().putBoolean("auto_block_scam", it).apply()
        }

        SettingSwitch(
            title = "Cảnh báo cuộc gọi từ số lạ",
            enabled = warnUnknownCall
        ) {
            warnUnknownCall = it
            prefs.edit().putBoolean("warn_unknown_call", it).apply()
        }

        SettingSwitch(
            title = "Đọc tên người gọi từ danh bạ",
            enabled = ttsContactName
        ) {
            ttsContactName = it
            prefs.edit().putBoolean("tts_contact_name", it).apply()
        }

        SettingSwitch(
            title = "Đọc cảnh báo khi đang đổ chuông",
            enabled = ttsRingingWarning
        ) {
            ttsRingingWarning = it
            prefs.edit().putBoolean("tts_ringing_warning", it).apply()
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                navController.navigate("LoginScreen") {
                    popUpTo("MainScreen") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất", color = Color.White)
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title: ${if (enabled) "Bật" else "Tắt"}",
            color = Color.White,
            fontSize = 14.sp
        )
        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF64B5F6),
                checkedTrackColor = Color(0x3348A0E0)
            )
        )
    }
}
