package com.example.callernamespeaker.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callernamespeaker.model.CallStat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminCallStatsScreen() {
    val db = FirebaseFirestore.getInstance()
    var callStats by remember { mutableStateOf<List<CallStat>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val today = LocalDate.now().toString()

    DisposableEffect(today) {
        val listener = db.collection("CallStats")
            .whereEqualTo("date", today)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    callStats = snapshot.documents
                        .mapNotNull { it.toObject(CallStat::class.java) }
                        .sortedByDescending { it.lastCall }
                    loading = false
                }
            }

        onDispose { listener.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .padding(12.dp)
    ) {
        Text(
            text = "Theo dõi hành vi cuộc gọi",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "Tự động làm mới mỗi ngày",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (loading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(callStats) { stat ->
                    CallStatItem(stat)
                }
            }
        }
    }
}

@Composable
fun CallStatItem(stat: CallStat) {
    val isSuspicious = stat.callCount >= 10

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2030)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.phone,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = if (isSuspicious)
                        Color(0xFF2A1A1A)
                    else
                        Color(0xFF1A2F2A),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = if (isSuspicious)
                            "Nghi vấn bất thường"
                        else
                            "Hoạt động bình thường",
                        color = if (isSuspicious)
                            Color(0xFFFF6B6B)
                        else
                            Color(0xFF4DB6AC),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ngày: ${stat.date}",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Text(
                text = "Số lần gọi: ${stat.callCount}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
