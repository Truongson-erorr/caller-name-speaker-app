package com.example.callernamespeaker.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.PhoneLookupViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeTab(navController: NavController) {
    val viewModel: PhoneLookupViewModel = viewModel()
    val context = LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    val scrollState = rememberScrollState()

    LaunchedEffect(uid) {
        if (uid == null) {
            Toast.makeText(context, "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
            navController.navigate("LoginScreen") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            viewModel.fetchHistory()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
            .padding(10.dp)
            .verticalScroll(scrollState)
    ) {
        BannerCarousel()
        Text(
            "Tra cứu nhanh",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Tra SDT", Icons.Default.Search, Modifier.weight(1f)) {
                navController.navigate("SearchScreen")
            }
            ServiceButton("Sms", Icons.Default.PersonSearch, Modifier.weight(1f)) {
                navController.navigate("SmsIntroScreen")
            }
            ServiceButton("Lừa đảo", Icons.Default.Report, Modifier.weight(1f)) {}
            ServiceButton("Khẩn cấp", Icons.Default.Wifi, Modifier.weight(1f)) {
                navController.navigate("EmergencyTab")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ServiceButton("Chặn số", Icons.Default.Block, Modifier.weight(1f)) {
                navController.navigate("block_phone")
            }
            ServiceButton("Báo cáo", Icons.Default.Flag, Modifier.weight(1f)) {
                navController.navigate("report")
            }
            ServiceButton("Website", Icons.Default.Public, Modifier.weight(1f)) {
                navController.navigate("WebsiteScreen")
            }
            ServiceButton("Giải đáp AI", Icons.Default.ChatBubbleOutline, Modifier.weight(1f)) {
                navController.navigate("ChatScreen")
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        NewsSection(navController)

        Spacer(modifier = Modifier.height(14.dp))
        SecuritytipScreen()
    }
}

@Composable
fun ServiceButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF101B2D))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF64B5F6),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}
