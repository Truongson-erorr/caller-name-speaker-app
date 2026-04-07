package com.example.callernamespeaker.ui.theme.Family

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.model.FamilyMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    navController: NavController,
    memberId: String
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var member by remember { mutableStateOf<FamilyMember?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    var showNicknameDialog by remember { mutableStateOf(false) }
    var nicknameInput by remember { mutableStateOf("") }

    val familyId = currentUser?.uid ?: return

    fun fetchMember() {
        isRefreshing = true
        db.collection("Family")
            .document(familyId)
            .collection("members")
            .document(memberId)
            .get()
            .addOnSuccessListener { snapshot ->
                member = snapshot.toObject(FamilyMember::class.java)
            }
            .addOnCompleteListener { isRefreshing = false }
    }

    LaunchedEffect(memberId) {
        fetchMember()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                text = "Chi tiết thành viên",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { fetchMember() }) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF64B5F6)
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            }
        }

        if (member != null) {
            val it = member!!

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF101B2D)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (it.nickname.ifBlank { it.name })
                        .firstOrNull()?.uppercase() ?: "?",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64B5F6)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (it.nickname.isNotBlank()) it.nickname else it.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        nicknameInput = it.nickname
                        showNicknameDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Edit nickname",
                        tint = Color(0xFF2A2AFC)
                    )
                }
            }

            if (it.nickname.isNotBlank()) {
                Text(
                    text = it.name,
                    color = Color(0xFFB0C4DE),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            val statusColor = when (it.status.lowercase()) {
                "accepted" -> Color(0xFF00E676)
                "pending" -> Color(0xFFFFB74D)
                "rejected", "declined" -> Color(0xFFFF8A80)
                else -> Color(0xFFB0C4DE)
            }

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        it.status.replaceFirstChar { c -> c.uppercase() },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = statusColor.copy(alpha = 0.25f)
                )
            )
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(Icons.Default.Email, "Liên hệ", it.email.ifBlank { it.phoneNumber })
                    InfoRow(Icons.Default.Badge, "Mối quan hệ", it.relation)
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Ghi chú: Hệ thống sẽ cảnh báo khi người thân nhận cuộc gọi từ số lạ.",
                fontSize = 14.sp,
                color = Color(0xFFB0C4DE)
            )
        }
    }

    if (showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showNicknameDialog = false },
            containerColor = Color(0xFF101B2D),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFB0C4DE),
            title = {
                Text(
                    "Đặt biệt danh",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                TextField(
                    value = nicknameInput,
                    onValueChange = { nicknameInput = it },
                    placeholder = {
                        Text(
                            "Ví dụ: Bố, Mẹ, Ông nội...",
                            color = Color(0xFF7F8FB3)
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF64B5F6)
                    )
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        val nickname = nicknameInput.trim()
                        db.collection("Family")
                            .document(familyId)
                            .collection("members")
                            .document(memberId)
                            .update("nickname", nickname)

                        member = member?.copy(nickname = nickname)
                        showNicknameDialog = false
                    }
                ) {
                    Text(
                        "Lưu",
                        color = Color(0xFF2A2AFC),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicknameDialog = false }) {
                    Text(
                        "Hủy",
                        color = Color(0xFFB0C4DE)
                    )
                }
            }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF64B5F6)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(label, color = Color(0xFFB0C4DE))
            Text(value.ifBlank { "—" }, color = Color.White)
        }
    }
}
