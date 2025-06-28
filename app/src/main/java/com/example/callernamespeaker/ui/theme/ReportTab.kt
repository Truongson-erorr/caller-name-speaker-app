package com.example.callernamespeaker.ui.theme

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.viewmodel.BlacklistViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportTab(viewModel: BlacklistViewModel = viewModel()) {
    val list = viewModel.blockedList
    val context = LocalContext.current
    var showUnblockDialog by remember { mutableStateOf<String?>(null) }

    // Lưu số bị chặn vào SharedPreferences sau khi load
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

    // Load từ Firestore
    LaunchedEffect(Unit) {
        viewModel.loadBlockedNumbers()
    }

    showUnblockDialog?.let { number ->
        AlertDialog(
            onDismissRequest = { showUnblockDialog = null },
            title = { Text("Xác nhận") },
            text = { Text("Bạn có chắc muốn bỏ chặn số $number?") },
            confirmButton = {
                TextButton(
                    onClick = {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Danh sách chặn",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(text = "Tổng: ${list.size} số")
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
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(max = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(list.size) { index ->
                    val item = list[index]
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.number.formatPhoneNumber(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
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
                                onClick = { showUnblockDialog = item.number }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Bỏ chặn",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun String.formatPhoneNumber(): String {
    return this.replace("+84", "0")
        .replace(" ", "")
        .chunked(4)
        .joinToString(" ")
}
