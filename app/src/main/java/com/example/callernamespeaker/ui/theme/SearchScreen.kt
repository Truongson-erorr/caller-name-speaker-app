package com.example.callernamespeaker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import androidx.compose.foundation.text.KeyboardOptions
import com.example.callernamespeaker.ui.theme.classifyPhoneNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController
) {
    val viewModel: PhoneLookupViewModel = viewModel()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)

    var phoneInput by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Tra cứu số điện thoại",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = phoneInput,
            onValueChange = { phoneInput = it },
            label = { Text("Nhập số điện thoại") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val cleaned = phoneInput.trim().replace("+84", "0").replace(" ", "")
                if (cleaned.isNotBlank()) {
                    val isBlocked = prefs.contains(cleaned)
                    val type = classifyPhoneNumber(cleaned)

                    viewModel.saveLookup(cleaned, type, isBlocked)

                    resultMessage =
                        "Số $cleaned (${type}) ${if (isBlocked) "đã bị chặn" else "chưa bị chặn"}"

                    Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kiểm tra")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (resultMessage.isNotEmpty()) {
            Text(
                text = resultMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại")
        }
    }
}
