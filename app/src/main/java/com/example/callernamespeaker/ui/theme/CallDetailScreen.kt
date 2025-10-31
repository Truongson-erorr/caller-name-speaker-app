package com.example.callernamespeaker.ui.theme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.example.callernamespeaker.model.CallReview
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
    val iconTint = Color(0xFF1976D2)
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
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = iconTint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier
                            .size(120.dp)
                            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
                            .shadow(8.dp, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                tint = iconTint,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = call.number,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        CallActionButton("Cuộc gọi", Icons.Default.Call, iconTint) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${call.number}"))
                            context.startActivity(intent)
                        }

                        CallActionButton("Tin nhắn", Icons.Default.Sms, iconTint) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${call.number}"))
                            context.startActivity(intent)
                        }

                        CallActionButton("Lưu", Icons.Default.PersonAdd, iconTint) {
                            // TODO: thêm logic lưu danh bạ
                        }

                        CallActionButton("Chặn", Icons.Default.Block, iconTint) {
                            // TODO: thêm logic chặn số
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    InfoRow("Số điện thoại", call.number)
                    InfoRow("Loại", call.type)
                    InfoRow("Ngày", call.date)
                    InfoRow("Thời lượng", "${call.duration} giây")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            CommunityReviewCard(
                reviews = reviews,
                onAddReview = { rating, comment ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val name = user?.email?.substringBefore("@") // hoặc displayName
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier
                .size(56.dp)
                .shadow(4.dp, CircleShape),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
