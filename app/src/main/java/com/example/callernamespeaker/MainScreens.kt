package com.example.callernamespeaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen() {
    var enabled by remember { mutableStateOf(true) }

    Column(Modifier.padding(16.dp)) {
        Text("Chế độ đọc tên người gọi: ${if (enabled) "Bật" else "Tắt"}")
        Switch(
            checked = enabled,
            onCheckedChange = { enabled = it }
        )
    }
}
