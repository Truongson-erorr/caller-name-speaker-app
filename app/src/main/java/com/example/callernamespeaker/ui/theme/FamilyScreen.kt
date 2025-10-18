package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen() {
    var connectedFamily by remember { mutableStateOf(listOf("Mẹ", "Ba", "Em trai")) }
    var isProtectionMode by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val alertHistory = listOf(
        Triple("Mẹ", "0901234567", "Số bị nhiều người báo cáo lừa đảo"),
        Triple("Ba", "0889990000", "Gọi nhá nhiều lần – nghi ngờ quảng cáo"),
        Triple("Em trai", "0977778888", "Số lạ từ nước ngoài, cảnh báo mức cao")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F9FF))
            .padding(16.dp)
    ) {
        Text(
            "Kết nối gia đình",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thành viên gia đình", fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Thêm thành viên", tint = Color(0xFF1565C0))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    connectedFamily.forEach { member ->
                        AssistChip(
                            onClick = {},
                            label = { Text(member) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Chế độ bảo vệ người thân", fontWeight = FontWeight.Bold)
                    Text(
                        if (isProtectionMode)
                            "Ưu tiên cảnh báo mạnh hơn cho người cao tuổi"
                        else
                            "Tắt chế độ bảo vệ đặc biệt",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Switch(checked = isProtectionMode, onCheckedChange = { isProtectionMode = it })
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text("Nhật ký cảnh báo gia đình", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alertHistory) { (member, number, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("$member nhận cuộc gọi từ $number", fontWeight = FontWeight.SemiBold)
                            Text(desc, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AddFamilyMemberDialog(
                onDismiss = { showDialog = false },
                onAdd = { name ->
                    connectedFamily = connectedFamily + name
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddFamilyMemberDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm thành viên") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Nhập tên thành viên...") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onAdd(name)
            }) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
