package com.example.callernamespeaker.ui.theme.History

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
            .background(Color(0xFF0A0F1F))
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Lịch sử tra cứu số điện thoại",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (viewModel.history.isEmpty()) {
            Text(
                text = "Chưa có dữ liệu tra cứu.",
                color = Color(0xFFB0C4DE),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                viewModel.history.forEach { item ->
                    LookupHistoryItem(item)
                }
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2D)), // card xanh đen nhạt
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF64B5F6),
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = item.number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = item.type,
                    color = when {
                        item.type.contains("tổng đài", true) -> Color(0xFF64B5F6)
                        item.type.contains("cá nhân", true) -> Color(0xFFFFB74D)
                        item.type.contains("quốc tế", true) -> Color(0xFF81C784)
                        else -> Color(0xFFFF8A80)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(
                            when {
                                item.type.contains("tổng đài", true) -> Color(0x332197F3)
                                item.type.contains("cá nhân", true) -> Color(0x33FFA726)
                                item.type.contains("quốc tế", true) -> Color(0x332ECC71)
                                else -> Color(0x33E57373)
                            },
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            if (!isActuallyBlocked) {
                Button(
                    onClick = {
                        blacklistViewModel.addToBlacklist(item.number, item.type) {
                            val cleaned = item.number.replace("+84", "0").replace(" ", "")
                            prefs.edit().putBoolean(cleaned, true).apply()
                            Toast.makeText(context, "Đã thêm vào danh sách chặn", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text("Chặn", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text(
                    text = "Đã chặn",
                    color = Color(0xFFB0C4DE),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

fun getTypeIcon(type: String): ImageVector {
    return when {
        type.contains("tổng đài", ignoreCase = true) -> Icons.Default.CellTower
        type.contains("cá nhân", ignoreCase = true) -> Icons.Default.Person
        type.contains("quốc tế", ignoreCase = true) -> Icons.Default.Public
        else -> Icons.Default.Help
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
