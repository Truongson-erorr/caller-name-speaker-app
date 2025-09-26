package com.example.callernamespeaker.model

import androidx.compose.ui.graphics.Color

data class TipItem(
    val text: String,
    val detail: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)