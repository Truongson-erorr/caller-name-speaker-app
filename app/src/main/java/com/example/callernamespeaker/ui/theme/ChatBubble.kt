package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.callernamespeaker.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor = if (message.isUser) Color(0xFF3B82F6) else Color(0xFF1E293B)
    val textColor = if (message.isUser) Color.White else Color(0xFFE5E7EB)
    val timeColor = Color(0xFF9CA3AF)

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeText = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                color = textColor
            )
        }
        Text(
            text = timeText,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = timeColor,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}
