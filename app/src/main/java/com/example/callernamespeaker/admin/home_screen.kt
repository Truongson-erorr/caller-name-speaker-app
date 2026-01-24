package com.example.callernamespeaker.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf("dashboard") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        drawerContent = {
            AdminDrawer(
                selectedTab = selectedTab,
                onItemClick = { key ->
                    when (key) {
                        "logout" -> {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("LoginScreen") {
                                popUpTo(0) { inclusive = true }
                            }
                        }

                        else -> {
                            selectedTab = key
                            scope.launch { drawerState.close() }
                        }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A2030),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text(
                            text = "ADMIN PANEL",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Thông báo",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFF0A0F1F))
            ) {
                when (selectedTab) {
                    "dashboard" -> AdminDashboardScreen()
                    "reports" -> AdminReportsScreen()
                    "posts" -> AdminPostsScreen()
                    "users" -> AdminUsersScreen()
                }
            }
        }
    }
}

@Composable
fun AdminDrawer(
    selectedTab: String,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.7f),
        drawerContainerColor = Color(0xFF1A2030)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ADMIN MENU",
            color = Color(0xFF64B5F6),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        AdminDrawerItem(
            label = "Tổng quan",
            icon = Icons.Outlined.Dashboard,
            selected = selectedTab == "dashboard"
        ) { onItemClick("dashboard") }

        AdminDrawerItem(
            label = "Báo cáo số",
            icon = Icons.Outlined.Report,
            selected = selectedTab == "reports"
        ) { onItemClick("reports") }

        AdminDrawerItem(
            label = "Bài viết",
            icon = Icons.Outlined.Article,
            selected = selectedTab == "posts"
        ) { onItemClick("posts") }

        AdminDrawerItem(
            label = "Người dùng",
            icon = Icons.Outlined.People,
            selected = selectedTab == "users"
        ) { onItemClick("users") }

        AdminDrawerItem(
            label = "Đăng xuất",
            icon = Icons.Outlined.Logout,
            selected = false,
        ) {
            onItemClick("logout")
        }
    }
}

@Composable
fun AdminDrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(0xFF64B5F6)

    val iconColor = if (selected) accent else Color.White
    val textColor = if (selected) accent else Color.White
    val bgColor = if (selected) Color(0xFF24304A) else Color.Transparent
    val indicatorColor = if (selected) accent else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(bgColor)
            .clickable { onClick() }
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(indicatorColor)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
