package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.viewmodel.BlacklistViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportTab(
    viewModel: BlacklistViewModel = viewModel()
) {
    val list = viewModel.blockedList
    val context = LocalContext.current
    var showUnblockDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(list) {
        if (list.isNotEmpty()) {
            val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)
            val editor = prefs.edit().clear()
            list.forEach {
                val cleaned = it.number.replace("+84", "0").replace(" ", "")
                editor.putBoolean(cleaned, true)
            }
            editor.apply()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBlockedNumbers()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Danh sách chặn",
                fontSize = 15.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Badge(
                    containerColor = Color(0xFFD0F5D4),
                    contentColor = Color(0xFF2E7D32)
                ) {
                    Text(text = "Tổng: ${list.size} số")
                }
                IconButton(onClick = {
                    if (list.isNotEmpty()) {
                        val cleanedList =
                            list.map { "${it.number.formatPhoneNumber()} - ${it.type}" }
                        shareBlockedList(context, cleanedList)
                    } else {
                        Toast.makeText(context, "Không có số nào để chia sẻ", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.LocalPrintshop,
                        contentDescription = "Chia sẻ danh sách",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Chưa có số nào bị chặn",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Số điện thoại bị chặn sẽ xuất hiện ở đây",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize() .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list.size) { index ->
                    val item = list[index]
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = item.number.formatPhoneNumber(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (item.type.isNotBlank()) {
                                    Text(
                                        text = item.type,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = "Đã chặn",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { showUnblockDialog = item.number },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Bỏ chặn",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        showUnblockDialog?.let { number ->
            AlertDialog(
                onDismissRequest = { showUnblockDialog = null },
                title = { Text("Xác nhận") },
                text = { Text("Bạn có chắc muốn bỏ chặn số $number?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showUnblockDialog?.let { number ->
                                viewModel.removeFromBlacklist(number) {
                                    val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)
                                    val cleaned = number.replace("+84", "0").replace(" ", "")
                                    prefs.edit().remove(cleaned).apply()
                                    Toast.makeText(context, "Bỏ chặn thành công!", Toast.LENGTH_SHORT).show()
                                    showUnblockDialog = null
                                }
                            }
                        }
                    ) {
                        Text("Đồng ý", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUnblockDialog = null }
                    ) {
                        Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

fun String.formatPhoneNumber(): String {
    return this.replace("+84", "0")
        .replace(" ", "")
        .chunked(4)
        .joinToString(" ")
}

fun shareBlockedList(context: Context, numbers: List<String>) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, numbers.joinToString("\n"))
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ danh sách chặn qua"))
}
