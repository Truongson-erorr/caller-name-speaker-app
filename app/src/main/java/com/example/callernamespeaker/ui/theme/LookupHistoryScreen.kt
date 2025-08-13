package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.PhoneLookup
import com.example.callernamespeaker.viewmodel.BlacklistViewModel
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel

@Composable
fun LookupHistoryScreen(
    viewModel: PhoneLookupViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchHistory()
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
    ) {
        Text(
            text = "Lịch sử tra cứu số điện thoại",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.history.isEmpty()) {
            Text(
                text = "Chưa có dữ liệu tra cứu.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.history.forEach { item ->
                    LookupHistoryItem(item)
                }
            }
        }
    }
}

fun classifyPhoneNumber(number: String): String {
    return when {
        number.startsWith("1900") || number.startsWith("1800") -> "Số tổng đài / cơ quan"
        number.startsWith("09") || number.startsWith("08") || number.startsWith("03") -> "Số cá nhân nội địa"
        number.startsWith("+") -> "Số quốc tế"
        else -> "Không xác định"
    }
}

fun getTypeIcon(type: String): ImageVector {
    return when {
        type.contains("tổng đài") -> Icons.Default.CellTower
        type.contains("cá nhân") -> Icons.Default.Person
        type.contains("quốc tế") -> Icons.Default.Public
        else -> Icons.Default.Help
    }
}

fun getTypeBackgroundColor(type: String): Color {
    return when {
        type.contains("tổng đài", ignoreCase = true) || type.contains("cơ quan", ignoreCase = true) -> Color(0xFFBBDEFB) // xanh nước nhạt
        type.contains("cá nhân", ignoreCase = true) -> Color(0xFFFFE0B2) // cam nhạt
        type.contains("quốc tế", ignoreCase = true) -> Color(0xFFC8E6C9) // xanh lá nhạt
        else -> Color(0xFFFFCDD2)
    }
}

fun getTypeTextColor(type: String): Color {
    return when {
        type.contains("tổng đài", ignoreCase = true) || type.contains("cơ quan", ignoreCase = true) -> Color(0xFF1565C0)
        type.contains("cá nhân", ignoreCase = true) -> Color(0xFFEF6C00)
        type.contains("quốc tế", ignoreCase = true) -> Color(0xFF2E7D32)
        else -> Color(0xFFC62828)
    }
}

@Composable
fun LookupHistoryItem(item: PhoneLookup) {
    val icon = getTypeIcon(item.type)
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)

    val blacklistViewModel: BlacklistViewModel = viewModel()
    val isActuallyBlocked = remember(item.number) {
        val cleaned = item.number.trim().replace("+84", "0").replace(" ", "")
        prefs.contains(cleaned) || item.isBlocked
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Loại",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.number,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = item.type,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = getTypeTextColor(item.type),
                    modifier = Modifier
                        .background(
                            color = getTypeBackgroundColor(item.type),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            if (!isActuallyBlocked) {
                Button(
                    onClick = {
                        blacklistViewModel.addToBlacklist(item.number, item.type) {
                            val cleaned = item.number.trim().replace("+84", "0").replace(" ", "")
                            prefs.edit().putBoolean(cleaned, true).apply()
                            Toast.makeText(context, "Đã thêm vào danh sách chặn", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(text = "Chặn", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text(
                    text = "Đã chặn",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

