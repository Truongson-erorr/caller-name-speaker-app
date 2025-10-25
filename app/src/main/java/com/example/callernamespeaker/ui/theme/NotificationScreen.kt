package com.example.callernamespeaker.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.Notification
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NotificationScreen() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(userId) as T
            }
        }
    )
    val notifications by notificationViewModel.notifications.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            notificationViewModel.listenToNotifications(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Thông báo",
            fontSize = 15.sp,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (
            notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Không có thông báo")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onMarkAsRead = { notificationViewModel.markAsRead(notification.id) },
                        onDelete = { notificationViewModel.deleteNotification(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val textWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onMarkAsRead() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9E7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!notification.isRead) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Chưa đọc",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 8.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = textWeight,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontWeight = textWeight,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTime(notification.timestamp),
                    fontWeight = textWeight,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xác nhận xoá") },
            text = { Text("Bạn có chắc chắn muốn xoá thông báo này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Xoá", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Huỷ")
                }
            }
        )
    }
}

fun formatTime(timestamp: Long): String {
    return DateFormat.format("HH:mm dd/MM/yyyy", timestamp).toString()
}
