package com.example.callernamespeaker.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.ui.theme.ConnectionRequestItem
import com.example.callernamespeaker.ui.theme.FamilyMemberItem
import com.example.callernamespeaker.viewmodel.CallAlertViewModel
import com.example.callernamespeaker.viewmodel.FamilyViewModel

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen(
    navController: NavController,
    viewModel: FamilyViewModel = viewModel()
) {
    val members by viewModel.familyMembers.collectAsState()
    val requests by viewModel.connectionRequests.collectAsState()
    val message by viewModel.message.collectAsState()

    var email by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val callAlertViewModel: CallAlertViewModel = viewModel()
    val alerts by callAlertViewModel.alerts.collectAsState()

    LaunchedEffect(Unit) {
        callAlertViewModel.listenCallAlerts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .padding(16.dp)
    ) {
        Text(
            "Kết nối thành viên",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A2AFC),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Mời qua Email")
        }

        if (message != null) {
            Text(message ?: "", color = Color.White, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(24.dp))

        Text("Lời mời kết nối:", color = Color.White, style = MaterialTheme.typography.titleMedium)
        if (requests.isEmpty()) {
            Text("Chưa có lời mời nào.", color = Color.White)
        } else {
            LazyColumn {
                items(requests.size) { i ->
                    ConnectionRequestItem(
                        request = requests[i],
                        onAccept = { viewModel.acceptConnectionRequest(requests[i]) },
                        onReject = { viewModel.rejectConnectionRequest(requests[i].id) }
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        if (alerts.isNotEmpty()) {
            Text(
                "Cảnh báo cuộc gọi lạ từ người thân:",
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val groupedAlerts = alerts.groupBy { it.callerNumber }

            LazyColumn {
                items(groupedAlerts.entries.toList()) { (callerNumber, alertList) ->
                    val alert = alertList.first()
                    val callCount = alertList.size

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2D)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Người thân ${alert.receiverName} nhận cuộc gọi lạ từ:",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$callerNumber",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Thời gian: ${
                                        java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(java.util.Date(alert.time))
                                    }",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            }

                            if (callCount > 1) {
                                Box(
                                    modifier = Modifier
                                        .height(28.dp)
                                        .padding(horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$callCount lần",
                                        color = Color(0xFFD32F2F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        Text(
            "Thành viên gia đình:",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(10.dp))

        if (members.isEmpty()) {
            Text("Chưa có thành viên nào.", color = Color.White.copy(alpha = 0.7f))
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(members.size) { i ->
                    FamilyMemberItem(member = members[i],
                        onMemberClick = { selected ->
                            navController.navigate("memberDetail/${selected.id}")
                        })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFF1E1E2D),
            tonalElevation = 8.dp,
            title = {
                Text(
                    "Mời thành viên qua Email",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nhập địa chỉ email người mà bạn muốn mời vào nhóm gia đình:",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(26.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                        label = { Text("Địa chỉ Email", color = Color.White.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = Color.White,
                            focusedBorderColor = Color(0xFF2A2AFC),
                            unfocusedBorderColor = Color.Gray,
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            viewModel.sendConnectionRequest(email)
                            showDialog = false
                            email = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2AFC)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Gửi lời mời", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Hủy", color = Color.Gray)
                }
            }
        )
    }
}


