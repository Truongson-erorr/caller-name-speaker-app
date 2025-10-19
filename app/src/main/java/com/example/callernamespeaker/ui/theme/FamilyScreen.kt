package com.example.callernamespeaker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.ConnectionRequest
import com.example.callernamespeaker.model.FamilyMember
import com.example.callernamespeaker.ui.theme.ConnectionRequestItem
import com.example.callernamespeaker.ui.theme.FamilyMemberItem
import com.example.callernamespeaker.viewmodel.FamilyViewModel

@Composable
fun FamilyScreen(
    viewModel: FamilyViewModel = viewModel()
) {
    val members by viewModel.familyMembers.collectAsState()
    val requests by viewModel.connectionRequests.collectAsState()
    val message by viewModel.message.collectAsState()

    var email by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            "Kết nối thành viên",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2575FC),
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = null
            )
            Spacer(Modifier.width(6.dp))
            Text("Mời qua Email")
        }

        if (message != null) {
            Text(message ?: "", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(24.dp))

        Text("Lời mời kết nối:", style = MaterialTheme.typography.titleMedium)
        if (requests.isEmpty()) {
            Text("Chưa có lời mời nào.", color = MaterialTheme.colorScheme.outline)
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

        Text("Thành viên gia đình:", style = MaterialTheme.typography.titleMedium)
        if (members.isEmpty()) {
            Text("Chưa có thành viên nào.", color = MaterialTheme.colorScheme.outline)
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(members.size) { i ->
                    FamilyMemberItem(member = members[i])
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            tonalElevation = 8.dp,
            title = {
                Text(
                    "📧 Mời thành viên qua Email",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E2D)
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
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(26.dp),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        label = { Text("Địa chỉ Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                        containerColor = Color(0xFF3B6EF6)
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

