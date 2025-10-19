package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callernamespeaker.model.FamilyMember
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FamilyMemberItem(
    member: FamilyMember,
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    Box(
        modifier = Modifier
            .width(90.dp)
            .padding(8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDEE7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B6EF6)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (member.id == currentUserId) "Tôi" else member.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E2D),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = member.phoneNumber,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
