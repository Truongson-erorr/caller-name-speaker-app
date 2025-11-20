package com.example.callernamespeaker.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EmergencyTab(navController: NavController) {
    val context = LocalContext.current

    val emergencyNumbers = listOf(
        EmergencyNumber("113", "Công an", Icons.Default.Security),
        EmergencyNumber("114", "Cứu hỏa", Icons.Default.LocalFireDepartment),
        EmergencyNumber("115", "Cấp cứu", Icons.Default.HealthAndSafety),
        EmergencyNumber("111", "Bảo vệ trẻ em", Icons.Default.ChildCare),
        EmergencyNumber("112", "Tổng đài quốc gia", Icons.Default.Emergency)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(55.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 8.dp)
                    .size(26.dp)
            )
            Text(
                text = "Số khẩn cấp tại Việt Nam",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF374151),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chỉ gọi trong trường hợp thật sự khẩn cấp",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White
                    )
                )
            }
        }

        emergencyNumbers.forEach { number ->
            EmergencyNumberCard(number = number) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${number.number}")
                context.startActivity(intent)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = "Tất cả cuộc gọi này đều miễn phí.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp)
        )
    }
}

@Composable
fun EmergencyNumberCard(number: EmergencyNumber, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = number.icon,
                contentDescription = null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = number.label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "Sẵn sàng hỗ trợ 24/7",
                    fontSize = 12.sp,
                    color = Color(0xFF22C55E),
                    modifier = Modifier
                        .background(Color(0xFF164E2B), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD32F2F),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = number.number,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

data class EmergencyNumber(
    val number: String,
    val label: String,
    val icon: ImageVector
)
