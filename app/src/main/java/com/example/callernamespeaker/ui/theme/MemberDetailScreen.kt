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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Chi tiết thành viên",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { fetchMember() }) {
                if (isRefreshing) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        if (member != null) {
            val it = member!!

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDEE7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it.name.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B6EF6)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = it.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val statusColor = when (it.status.lowercase()) {
                    "accepted" -> Color(0xFF2E7D32)
                    "pending" -> Color(0xFFED6C02)
                    "rejected", "declined" -> Color(0xFFB00020)
                    else -> Color(0xFF616161)
                }

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            it.status.capitalize(),
                            color = if (it.status.lowercase() == "accepted") Color(0xFF1B5E20) else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (it.status.lowercase() == "accepted") Color(0xFFC8E6C9)
                        else statusColor.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    border = null
                )
            }
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Email", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                            val contact = if (it.email.isNotBlank()) it.email else it.phoneNumber
                            Text(contact.ifEmpty { "—" }, fontSize = 15.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF6C63FF))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Mối quan hệ", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                            Text(it.relation.ifEmpty { "—" }, fontSize = 15.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                "Ghi chú: Hệ thống sẽ tự động ghi nhận và gửi cảnh báo khi người thân của bạn nhận cuộc gọi từ số lạ hoặc không xác định, giúp bạn dễ dàng theo dõi và bảo vệ họ khỏi các cuộc gọi đáng ngờ.",
                fontSize = 12.sp,
                color = Color.Gray
            )

        } else {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 36.dp), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy thông tin thành viên.", color = Color.Gray)
            }
        }
    }
}
