package com.example.callernamespeaker.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.CallEntry
import com.example.callernamespeaker.viewmodel.CommunityReviewViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallDetailScreen(
    navController: NavController,
    call: CallEntry,
    reviewViewModel: CommunityReviewViewModel = viewModel()
) {
    val context = LocalContext.current

    val backgroundDark = Color(0xFF0A0F1F)
    val cardDark = Color(0xFF112233)
    val cyanAccent = Color(0xFF4FC3F7)
    val textLight = Color(0xFFE3F2FD)

    val reviews by remember { derivedStateOf { reviewViewModel.reviews } }

    LaunchedEffect(call.number) {
        reviewViewModel.loadReviews(call.number)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết cuộc gọi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textLight
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = cyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundDark,
                    titleContentColor = textLight
                )
            )
        },
        containerColor = backgroundDark
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = cardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0D253A),
                        modifier = Modifier
                            .size(120.dp)
                            .border(2.dp, cyanAccent, CircleShape)
                            .shadow(10.dp, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = cyanAccent,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = call.number,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = textLight
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CallActionButton("Cuộc gọi", Icons.Default.Call, cyanAccent) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${call.number}"))
                            context.startActivity(intent)
                        }

                        CallActionButton("Tin nhắn", Icons.Default.Sms, cyanAccent) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${call.number}"))
                            context.startActivity(intent)
                        }

                        CallActionButton("Lưu", Icons.Default.PersonAdd, cyanAccent) {}
                        CallActionButton("Chặn", Icons.Default.Block, cyanAccent) {}
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = cardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    InfoRow("Số điện thoại", call.number)
                    InfoRow("Loại", call.type)
                    InfoRow("Ngày", call.date)
                    InfoRow("Thời lượng", "${call.duration} giây")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            CommunityReviewCard(
                reviews = reviews,
                onAddReview = { rating, comment ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val name = user?.email?.substringBefore("@")

                    reviewViewModel.addReview(
                        phoneNumber = call.number,
                        userName = name,
                        rating = rating,
                        comment = comment
                    )
                }
            )
        }
    }
}

@Composable
fun CallActionButton(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF0D253A),
            modifier = Modifier
                .size(60.dp)
                .shadow(8.dp, CircleShape),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFE3F2FD),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White
            )
        )
    }
}


