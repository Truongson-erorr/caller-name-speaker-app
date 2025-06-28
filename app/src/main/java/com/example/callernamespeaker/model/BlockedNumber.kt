package com.example.callernamespeaker.model

data class BlockedNumber(
    val number: String = "",
    val type: String = "", // ví dụ: "Di động", "Nhà", "Tùy chỉnh", ...
    val createdAt: Long = System.currentTimeMillis()
)
