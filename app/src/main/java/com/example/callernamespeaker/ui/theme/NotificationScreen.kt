package com.example.callernamespeaker.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Circle
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
import androidx.navigation.NavController
import com.example.callernamespeaker.model.Notification
import com.example.personalexpensetracker.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NotificationScreen(
    navController: NavController
) {
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
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
                    .padding(start = 8.dp)
                    .size(26.dp)
            )
            Text(
                text = "Thông báo",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Không có thông báo")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(notifications) { index, notification ->
                    NotificationItem(
                        notification = notification,
                        onMarkAsRead = { notificationViewModel.markAsRead(notification.id) },
                        onDelete = { notificationViewModel.deleteNotification(notification.id) }
                    )
                    if (index < notifications.lastIndex) {

                    }
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
    val backgroundColor =
        if (!notification.isRead) Color(0xFFD2E8FF) else Color.White
    val textWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onMarkAsRead() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Thông báo",
                tint = if (!notification.isRead) Color(0xFF1976D2) else Color.Gray,
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 12.dp)
            )

            if (!notification.isRead) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Chưa đọc",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = textWeight,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    fontWeight = textWeight,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTime(notification.timestamp),
                    fontWeight = textWeight,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

fun formatTime(timestamp: Long): String {
    return DateFormat.format("HH:mm dd/MM/yyyy", timestamp).toString()
}
