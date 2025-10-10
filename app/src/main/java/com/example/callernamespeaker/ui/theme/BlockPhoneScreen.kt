package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.BlacklistViewModel
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockPhoneScreen(
    navController: NavController,
    blacklistViewModel: BlacklistViewModel,
    notificationViewModel: NotificationViewModel
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)

    var blockPhoneInput by remember { mutableStateOf("") }

    val cleanedPhone = blockPhoneInput.trim().replace("+84", "0").replace(" ", "")
    val isBlocked = prefs.getBoolean(cleanedPhone, false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chặn số điện thoại") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = blockPhoneInput,
                onValueChange = { blockPhoneInput = it },
                label = { Text("Nhập số điện thoại") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            if (isBlocked && cleanedPhone.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⚠️ Số này đã được chặn!",
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
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Chặn số này", color = Color.White)
            }
        }
    }
}
