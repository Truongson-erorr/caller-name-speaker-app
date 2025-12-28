package com.example.callernamespeaker

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.ui.screens.FamilyScreen
import com.example.callernamespeaker.ui.theme.HistoryTab
import com.example.callernamespeaker.ui.theme.HomeTab
import com.example.callernamespeaker.ui.theme.ProfileScreen
import com.example.callernamespeaker.ui.theme.ReportTab

@Composable
fun MainScreen(navController: NavController) {

    var selectedTab by remember { mutableStateOf("home") }
    val context = LocalContext.current

    val selectedColor = Color(0xFF64B5F6)
    val unselectedColor = Color.White

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A2030)
            ) {
                @Composable
                fun NavItem(
                    selected: Boolean,
                    selectedIcon: ImageVector,
                    unselectedIcon: ImageVector,
                    label: String,
                    key: String
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) selectedIcon else unselectedIcon,
                                contentDescription = label,
                                tint = if (selected) selectedColor else unselectedColor,
                                modifier = Modifier.size(24.dp)
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

                NavItem(
                    selected = selectedTab == "home",
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    label = "Trang chủ",
                    key = "home"
                )

                NavItem(
                    selected = selectedTab == "post",
                    selectedIcon = Icons.Filled.FamilyRestroom,
                    unselectedIcon = Icons.Outlined.FamilyRestroom,
                    label = "Gia đình",
                    key = "post"
                )

                NavItem(
                    selected = selectedTab == "report",
                    selectedIcon = Icons.Filled.Block,
                    unselectedIcon = Icons.Outlined.Block,
                    label = "Chặn số",
                    key = "report"
                )

                NavItem(
                    selected = selectedTab == "history",
                    selectedIcon = Icons.Filled.History,
                    unselectedIcon = Icons.Outlined.History,
                    label = "Lịch sử",
                    key = "history"
                )

                NavItem(
                    selected = selectedTab == "profile",
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    label = "Tài khoản",
                    key = "profile"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                "home" -> HomeTab(navController)
                "post" -> FamilyScreen(navController)
                "report" -> ReportTab()
                "history" -> HistoryTab(navController, context)
                "profile" -> ProfileScreen(navController)
            }
        }
    }
}
