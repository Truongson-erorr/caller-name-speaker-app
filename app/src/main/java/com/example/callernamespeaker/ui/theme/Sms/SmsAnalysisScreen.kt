package com.example.callernamespeaker.ui.theme.Sms

import android.Manifest
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun SmsAnalysisScreen(
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var smsList by remember { mutableStateOf<List<SmsItem>>(emptyList()) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (granted) {
            smsList = getConversationList(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_SMS)
    }

    DisposableEffect(permissionGranted) {

        if (!permissionGranted) return@DisposableEffect onDispose { }

        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                smsList = getConversationList(context)
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
            modifier = Modifier
                .fillMaxSize()
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
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                )

                Text(
                    text = "Danh sách SMS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!permissionGranted) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ứng dụng cần quyền đọc SMS.",
                        color = Color.LightGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(smsList) { sms ->
                        SmsRow(sms) {
                            navController.navigate("sms_detail/${sms.id}")
                        }
                    }
                }
            }
        }
    }
}

fun getConversationList(context: Context): List<SmsItem> {

    val conversationMap = mutableMapOf<String, SmsItem>()
    val cursor = context.contentResolver.query(
        Uri.parse("content://sms"),
        null,
        null,
        null,
        "date DESC"
    )

    cursor?.use {

        val threadIndex = it.getColumnIndex("thread_id")
        val bodyIndex = it.getColumnIndex("body")
        val addressIndex = it.getColumnIndex("address")
        val dateIndex = it.getColumnIndex("date")

        while (it.moveToNext()) {

            val threadId = it.getString(threadIndex) ?: continue
            if (conversationMap.containsKey(threadId)) continue

            val body = if (bodyIndex != -1) it.getString(bodyIndex) else ""
            val address = if (addressIndex != -1) it.getString(addressIndex) else "Unknown"
            val date = if (dateIndex != -1) it.getLong(dateIndex) else 0L

            conversationMap[threadId] = SmsItem(
                id = threadId,
                address = address ?: "Unknown",
                body = body ?: "",
                date = date
            )
        }
    }
    return conversationMap.values.toList()
}

@Composable
fun SmsRow(
    sms: SmsItem,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {

        Text(
            text = sms.address,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = sms.body,
            maxLines = 1,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color(0xFF1F2937),
            thickness = 0.5.dp
        )
    }
}



