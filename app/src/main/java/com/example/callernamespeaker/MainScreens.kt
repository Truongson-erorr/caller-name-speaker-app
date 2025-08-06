package com.example.callernamespeaker

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.ui.theme.EmergencyTab
import com.example.callernamespeaker.ui.theme.HistoryTab
import com.example.callernamespeaker.ui.theme.HomeTab
import com.example.callernamespeaker.ui.theme.NotificationScreen
import com.example.callernamespeaker.ui.theme.ReportTab
import com.google.firebase.auth.FirebaseAuth
import org.checkerframework.checker.units.qual.N

@Composable
fun MainScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("home") }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)),
                color = Color(0xFF1565C0),
                contentColor = Color.White,
                shadowElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Trang chủ",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Trang chủ",
                                    fontSize = 8.sp,
                                    color = if (selectedTab == "home") Color.White else Color.LightGray
                                )
                                if (selectedTab == "home") {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(Color.White)
                                    )
                                }
                            }
                        },
                        selected = selectedTab == "home",
                        onClick = { selectedTab = "home" },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray
                        )
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Danh sách đen",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Danh sách đen",
                                    fontSize = 8.sp,
                                    color = if (selectedTab == "report") Color.White else Color.LightGray
                                )
                                if (selectedTab == "report") {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(Color.White)
                                    )
                                }
                            }
                        },
                        selected = selectedTab == "report",
                        onClick = { selectedTab = "report" },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray
                        )
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Thông báo",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Thông báo",
                                    fontSize = 8.sp,
                                    color = if (selectedTab == "notification") Color.White else Color.LightGray
                                )
                                if (selectedTab == "notification") {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(Color.White)
                                    )
                                }
                            }
                        },
                        selected = selectedTab == "notification",
                        onClick = { selectedTab = "notification" },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray
                        )
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Lịch sử",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Lịch sử",
                                    fontSize = 8.sp,
                                    color = if (selectedTab == "history") Color.White else Color.LightGray
                                )
                                if (selectedTab == "history") {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(Color.White)
                                    )
                                }
                            }
                        },
                        selected = selectedTab == "history",
                        onClick = { selectedTab = "history" },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "home" -> HomeTab(navController = navController)
                "report" -> ReportTab()
                "notification" -> NotificationScreen()
                "history" -> HistoryTab(navController, context)
            }
        }
    }
}