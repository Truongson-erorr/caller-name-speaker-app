package com.example.callernamespeaker.ui.theme.Notifications

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.model.FamilyMember
import com.example.callernamespeaker.viewmodel.FamilyAlertViewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    members: List<FamilyMember>,
    alertViewModel: FamilyAlertViewModel = viewModel()
) {
    val alerts by alertViewModel.alerts.collectAsState()

    LaunchedEffect(Unit) {
        alertViewModel.listenFamilyAlerts()
    }

    val memberMap = remember(members) {
        members.associateBy { it.id }
    }

    val sortedAlerts = remember(alerts) {
        alerts.sortedByDescending { it.time }
    }

    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Thông báo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1A2030)
            )
        )

        if (sortedAlerts.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có cảnh báo",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

        } else {
            Spacer(Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(sortedAlerts) { alert ->

                    val member = memberMap[alert.memberId]

                    val displayName = member?.nickname?.takeIf { it.isNotBlank() }
                        ?: member?.name
                        ?: "Người thân"

                    val isExpanded = expandedMap[alert.id] ?: false

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF111C2E)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .clickable {
                                expandedMap[alert.id] = !isExpanded
                            }
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {

                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(26.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Column(modifier = Modifier.weight(1f)) {

                                        Text(
                                            text = displayName,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = "Nhận cuộc gọi từ số lạ",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 13.sp
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isExpanded)
                                            Icons.Default.ArrowDropUp
                                        else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }

                                if (isExpanded) {
                                    Spacer(Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF0F172A),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(10.dp)
                                    ) {

                                        Column {

                                            Text(
                                                text = "Số điện thoại",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp
                                            )

                                            Text(
                                                text = alert.phoneNumber,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(Modifier.height(8.dp))

                                            Text(
                                                text = "Thời gian",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp
                                            )

                                            Text(
                                                text = SimpleDateFormat(
                                                    "HH:mm dd/MM/yyyy",
                                                    Locale.getDefault()
                                                ).format(alert.time),
                                                color = Color(0xFFFBBF24),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}