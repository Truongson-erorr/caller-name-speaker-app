package com.example.callernamespeaker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.ui.screens.NotificationScreen
import com.example.callernamespeaker.ui.theme.FamilyScreen
import com.example.callernamespeaker.ui.theme.HistoryTab
import com.example.callernamespeaker.ui.theme.HomeTab
import com.example.callernamespeaker.ui.theme.ReportTab

@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf("home") }
    val context = LocalContext.current

    val selectedColor = Color(0xFF1565C0)
    val unselectedColor = Color.White
    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                color = Color(0xFF1565C0),
                contentColor = Color.White,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color.DarkGray,
                    contentColor = selectedColor
                ) {
                    @Composable
                    fun NavItem(
                        selected: Boolean,
                        icon: ImageVector,
                        label: String,
                        key: String
                    ) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) selectedColor else unselectedColor,
                                    modifier = Modifier.size(21.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = if (selected) selectedColor else unselectedColor
                                )
                            },
                            selected = selected,
                            onClick = { selectedTab = key },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }

                    NavItem(selected = selectedTab == "home", icon = Icons.Default.Home, label = "Trang chủ", key = "home")
                    NavItem(selected = selectedTab == "post", icon = Icons.Default.FamilyRestroom, label = "Gia đình", key = "post")
                    NavItem(selected = selectedTab == "report", icon = Icons.Default.List, label = "chặn số", key = "report")
                    NavItem(selected = selectedTab == "notification", icon = Icons.Default.Notifications, label = "Thông báo", key = "notification")
                    NavItem(selected = selectedTab == "history", icon = Icons.Default.History, label = "Lịch sử", key = "history")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "home" -> HomeTab(navController = navController)
                "post" -> FamilyScreen()
                "report" -> ReportTab()
                "notification" -> NotificationScreen()
                "history" -> HistoryTab(navController, context)
            }
        }
    }
}