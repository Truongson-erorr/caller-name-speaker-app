package com.example.callernamespeaker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.ConnectionRequest
import com.example.callernamespeaker.model.FamilyMember
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kết nối gia đình", style = MaterialTheme.typography.headlineSmall)

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

        Text("📩 Lời mời kết nối đến:", style = MaterialTheme.typography.titleMedium)
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

        Text("👥 Thành viên gia đình:", style = MaterialTheme.typography.titleMedium)
        if (members.isEmpty()) {
            Text("Chưa có thành viên nào.", color = MaterialTheme.colorScheme.outline)
        } else {
            LazyColumn {
                items(members.size) { i ->
                    FamilyMemberItem(members[i], onDelete = { viewModel.deleteFamilyMember(members[i].id) })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Mời thành viên qua Email") },
            text = {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    label = { Text("Nhập email người cần mời") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (email.isNotBlank()) {
                        viewModel.sendConnectionRequest(email)
                        showDialog = false
                        email = ""
                    }
                }) {
                    Text("Gửi lời mời")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun ConnectionRequestItem(request: ConnectionRequest, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Từ: ${request.fromName} (${request.fromPhone})")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onAccept) { Text("Chấp nhận") }
                TextButton(onClick = onReject) { Text("Từ chối", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun FamilyMemberItem(member: FamilyMember, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(member.name, style = MaterialTheme.typography.titleMedium)
                Text("Email: ${member.phoneNumber}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
