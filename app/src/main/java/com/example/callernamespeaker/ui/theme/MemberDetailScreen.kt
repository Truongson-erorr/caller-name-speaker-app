package com.example.callernamespeaker.ui.theme

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
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color.White,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Chi tiết thành viên",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
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
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        tint = Color.White,
                        contentDescription = "Refresh"
                    )
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
                    text = it.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64B5F6)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = it.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // STATUS CHIP (đổi tone dark)
            val statusColor = when (it.status.lowercase()) {
                "accepted" -> Color(0xFF81C784)
                "pending" -> Color(0xFFFFB74D)
                "rejected", "declined" -> Color(0xFFFF8A80)
                else -> Color(0xFFB0C4DE)
            }

            AssistChip(
                onClick = {},
                label = {
                    Text(it.status.replaceFirstChar { it.uppercase() },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = statusColor.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(26.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 🔹 CARD THÔNG TIN – giống tone LookupHistoryItem
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101B2D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Email", color = Color(0xFFB0C4DE))
                            val contact = if (it.email.isNotBlank()) it.email else it.phoneNumber
                            Text(contact.ifEmpty { "—" }, color = Color.White)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Badge,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Mối quan hệ", color = Color(0xFFB0C4DE))
                            Text(it.relation.ifEmpty { "—" }, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Ghi chú: Hệ thống sẽ cảnh báo khi người thân nhận cuộc gọi từ số lạ.",
                fontSize = 12.sp,
                color = Color(0xFFB0C4DE)
            )

        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin thành viên.", color = Color(0xFFB0C4DE))
            }
        }
    }
}

