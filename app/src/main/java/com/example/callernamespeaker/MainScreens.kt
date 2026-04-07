package com.example.callernamespeaker

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.callernamespeaker.ui.theme.Family.FamilyScreen
import com.example.callernamespeaker.ui.theme.History.HistoryTab
import com.example.callernamespeaker.ui.theme.Home.HomeTab
import com.example.callernamespeaker.ui.theme.Profile.ProfileScreen
import com.example.callernamespeaker.ui.theme.Report.ReportTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {

    var selectedTab by remember { mutableStateOf("home") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        drawerContent = {
            DrawerContent(
                selectedTab = selectedTab,
                onItemClick = {
                    selectedTab = it
                    scope.launch { drawerState.close() }
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
                            "Cẩm nang an toàn thông tin",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {

                        IconButton(
                            onClick = {
                                navController.navigate("NotificationScreen")
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Thông báo",
                                tint = Color(0xFF64B5F6)
                            )
                        }

                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
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
}

@Composable
fun DrawerContent(
    selectedTab: String,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .background(Color(0xFF1A2030)),
        drawerContainerColor = Color(0xFF1A2030)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MENU",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        DrawerItem(
            label = "Trang chủ",
            icon = Icons.Outlined.Home,
            selected = selectedTab == "home"
        ) { onItemClick("home") }

        DrawerItem(
            label = "Gia đình",
            icon = Icons.Outlined.FamilyRestroom,
            selected = selectedTab == "post"
        ) { onItemClick("post") }

        DrawerItem(
            label = "Chặn số",
            icon = Icons.Outlined.Block,
            selected = selectedTab == "report"
        ) { onItemClick("report") }

        DrawerItem(
            label = "Lịch sử",
            icon = Icons.Outlined.History,
            selected = selectedTab == "history"
        ) { onItemClick("history") }

        DrawerItem(
            label = "Tài khoản",
            icon = Icons.Outlined.Person,
            selected = selectedTab == "profile"
        ) { onItemClick("profile") }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = Color(0xFF64B5F6)
    val backgroundColor =
        if (selected) Color(0xFF24304A) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(if (selected) selectedColor else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) selectedColor else Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = label,
            color = if (selected) selectedColor else Color.White,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

