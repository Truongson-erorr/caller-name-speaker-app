package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    val familyId = currentUser?.uid ?: return

    // 🔹 Chỉ lấy dữ liệu một lần, không cần trạng thái loading
    LaunchedEffect(memberId) {
        db.collection("Family")
            .document(familyId)
            .collection("members")
            .document(memberId)
            .get()
            .addOnSuccessListener { snapshot ->
                member = snapshot.toObject(FamilyMember::class.java)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(35.dp))

        // 🔙 Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
                    .padding(start = 8.dp)
                    .size(26.dp)
            )
            Text(
                text = "Chi tiết thành viên",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }

        if (member != null) {
            val it = member!!

            // 🧑 Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDEE7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B6EF6)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🧾 Thông tin chi tiết
            Text(
                text = it.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Info(
                        label = "Email",
                        value = it.phoneNumber
                    )
                    Info(label = "Mối quan hệ", value = it.relation)
                    Info(label = "Trạng thái", value = it.status)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        } else {
            Spacer(modifier = Modifier.height(100.dp))
            Text("Không tìm thấy thông tin thành viên.", color = Color.Gray)
        }
    }
}

@Composable
fun Info(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            fontSize = 14.sp
        )
        Text(
            text = value.ifEmpty { "—" },
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
