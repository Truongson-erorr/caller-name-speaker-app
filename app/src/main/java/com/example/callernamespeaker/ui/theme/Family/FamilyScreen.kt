package com.example.callernamespeaker.ui.theme.Family

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                "Kết nối thành viên",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2AFC))
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(Modifier.width(6.dp))
                Text("Mời qua Email")
            }
        }

        message?.let {
            item {
                Text(it, color = Color.White)
            }
        }

        item {
            Text("Lời mời kết nối:", color = Color.White)
        }

        if (requests.isEmpty()) {
            item {
                Text("Chưa có lời mời nào.", color = Color.White)
            }
        } else {
            items(requests) { request ->
                ConnectionRequestItem(
                    request = request,
                    onAccept = { viewModel.acceptConnectionRequest(request) },
                    onReject = { viewModel.rejectConnectionRequest(request.id) }
                )
            }
        }

        item {
            Text(
                "Thành viên gia đình:",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            if (members.isEmpty()) {
                Text(
                    "Chưa có thành viên nào.",
                    color = Color.White.copy(alpha = 0.7f)
                )
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    userScrollEnabled = false,
                    modifier = Modifier.height((members.size / 2 + 1) * 140.dp)
                ) {
                    items(members.size) { i ->
                        val member = members[i]
                        FamilyMemberItem(
                            member = member,
                            onMemberClick = {
                                navController.navigate("memberDetail/${member.id}")
                            }
                        )
                    }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        label = {
                            Text(
                                "Địa chỉ Email",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
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
                    Text(
                        "Gửi lời mời",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
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