package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.callernamespeaker.model.SmsItem

@Composable
fun SmsDetailScreen(
    navController: NavController,
    threadId: String
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var messages by remember { mutableStateOf<List<SmsItem>>(emptyList()) }

    LaunchedEffect(threadId) {
        messages = getMessagesByThread(context, threadId)
    }

    DisposableEffect(threadId) {

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                messages = getMessagesByThread(context, threadId)
            }
        }

        context.contentResolver.registerContentObserver(
            Uri.parse("content://sms"),
            true,
            observer
        )

        onDispose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                )

                Text(
                    text = "Chi tiết tin nhắn",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                        }
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                items(messages) { sms ->
                    MessageBubble(sms)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

fun getMessagesByThread(
    context: Context,
    threadId: String
): List<SmsItem> {

    val smsList = mutableListOf<SmsItem>()

    val cursor = context.contentResolver.query(
        Uri.parse("content://sms"),
        null,
        "thread_id = ? AND type = 1",
        arrayOf(threadId),
        "date ASC"
    )

    cursor?.use {

        val idIndex = it.getColumnIndex("_id")
        val bodyIndex = it.getColumnIndex("body")
        val addressIndex = it.getColumnIndex("address")
        val dateIndex = it.getColumnIndex("date")

        while (it.moveToNext()) {

            val id = if (idIndex != -1) it.getString(idIndex) else ""
            val body = if (bodyIndex != -1) it.getString(bodyIndex) else ""
            val address = if (addressIndex != -1) it.getString(addressIndex) else ""
            val date = if (dateIndex != -1) it.getLong(dateIndex) else 0L

            smsList.add(
                SmsItem(
                    id = id ?: "",
                    address = address ?: "",
                    body = body ?: "",
                    date = date
                )
            )
        }
    }
    return smsList
}

@Composable
fun MessageBubble(sms: SmsItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F2937)
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 16.dp,
                bottomStart = 4.dp
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = sms.body,
                modifier = Modifier.padding(12.dp),
                color = Color.White
            )
        }
    }
}
