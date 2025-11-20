package com.example.callernamespeaker.ui.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.callernamespeaker.CallEntry
import com.example.callernamespeaker.CallLogHelper
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

val bgDark = Color(0xFF0A0F1F)
val cardDark = Color(0xFF101B2D)
val textWhite = Color(0xFFFFFFFF)
val textSecondary = Color(0xFFB0C4DE)
val accentBlue = Color(0xFF64B5F6)

@Composable
fun CallHistoryScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    val callList = remember { CallLogHelper.getCallHistory(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(16.dp)
    ) {
        LazyColumn {
            items(callList) { call ->
                CallHistoryItem(call = call) {
                    val index = callList.indexOf(call)
                    navController.navigate("call_detail/$index")
                }
            }
        }
    }
}

@Composable
fun CallHistoryItem(
    call: CallEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bgDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = accentBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = call.number,
                    color = textWhite,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = call.type,
                    color = textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${call.date} • ${call.duration} giây",
                    color = textSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val icon = when (call.type) {
                "Gọi đến" -> Icons.Filled.CallReceived
                "Gọi đi" -> Icons.Filled.CallMade
                "Bị nhỡ" -> Icons.Filled.CallMissed
                else -> Icons.Filled.CallReceived
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when (call.type) {
                    "Bị nhỡ" -> Color(0xFFFF6B6B)
                    "Gọi đi" -> accentBlue
                    "Gọi đến" -> Color(0xFF81C784)
                    else -> accentBlue
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
