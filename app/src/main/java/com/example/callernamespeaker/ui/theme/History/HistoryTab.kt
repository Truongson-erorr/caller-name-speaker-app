package com.example.callernamespeaker.ui.theme.History

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HistoryTab(
    navController: NavController,
    context: Context
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Lịch sử tra cứu", "Lịch sử cuộc gọi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1F))
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF101B2D),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFF64B5F6),
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color(0xFF64B5F6) else Color(0xFFB0C4DE)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> LookupHistoryScreen()
            1 -> CallHistoryScreen(navController, context)
        }
    }
}
