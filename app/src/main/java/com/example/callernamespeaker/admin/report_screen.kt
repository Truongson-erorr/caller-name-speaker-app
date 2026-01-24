package com.example.callernamespeaker.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.ReportGroup
import com.example.callernamespeaker.viewmodel.AdminReportsViewModel
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AdminReportsScreen(
    viewModel: AdminReportsViewModel = viewModel()
) {
    val reports by viewModel.reports.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReports()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .padding(16.dp)
    ) {

        Text(
            text = "Báo cáo số điện thoại",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(reports) { item ->
                ReportItem(item)
            }
        }
    }
}

@Composable
fun ReportItem(item: ReportGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2030)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.phone,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0x33FF7043),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${item.reportCount} báo cáo",
                        color = Color(0xFFFF7043),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Gần nhất: ${formatTime(item.lastReported)}",
                color = Color(0xFF90CAF9),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFF2C3A58))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chi tiết báo cáo",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            item.reports.forEachIndexed { index, report ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "${index + 1}. ${report.reason}",
                        color = Color(0xFFB0BEC5),
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Thời gian: ${formatTime(report.timestamp)}",
                        color = Color(0xFF78909C),
                        fontSize = 11.sp
                    )
                }

                if (index != item.reports.lastIndex) {
                    Divider(
                        color = Color(0xFF24304A),
                        thickness = 0.6.dp
                    )
                }
            }
        }
    }
}

fun formatTime(millis: Long?): String {
    if (millis == null) return "Chưa có"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
    return sdf.format(Date(millis))
}
