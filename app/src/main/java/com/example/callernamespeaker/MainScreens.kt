package com.example.callernamespeaker

import androidx.compose.foundation.background
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
import com.example.callernamespeaker.ui.screens.FamilyScreen
import com.example.callernamespeaker.ui.theme.*
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
                                navController.navigate("NotificationScreen")
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Thông báo",
                                tint = Color(0xFF64B5F6)
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
            color = Color(0xFF2A2AFC),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        DrawerItem("Trang chủ", Icons.Outlined.Home, selectedTab == "home") {
            onItemClick("home")
        }
        DrawerItem("Gia đình", Icons.Outlined.FamilyRestroom, selectedTab == "post") {
            onItemClick("post")
        }
        DrawerItem("Chặn số", Icons.Outlined.Block, selectedTab == "report") {
            onItemClick("report")
        }
        DrawerItem("Lịch sử", Icons.Outlined.History, selectedTab == "history") {
            onItemClick("history")
        }
        DrawerItem("Tài khoản", Icons.Outlined.Person, selectedTab == "profile") {
            onItemClick("profile")
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor = Color(0xFF2A2AFC)
    val unselectedColor = Color.White

    NavigationDrawerItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) selectedColor else unselectedColor
            )
        },
        label = {
            Text(
                text = label,
                color = if (selected) selectedColor else unselectedColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color.Transparent,
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
