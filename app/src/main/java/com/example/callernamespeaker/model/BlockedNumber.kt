package com.example.callernamespeaker.model

data class BlockedNumber(
    val number: String = "",
    val type: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
