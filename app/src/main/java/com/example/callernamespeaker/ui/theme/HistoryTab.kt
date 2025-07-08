package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryTab() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Lịch sử tra cứu", "Lịch sử cuộc gọi")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> LookupHistoryScreen()
            1 -> CallHistoryScreen()
        }
    }
}

