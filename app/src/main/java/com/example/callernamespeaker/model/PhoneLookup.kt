package com.example.callernamespeaker.model

data class PhoneLookup(
    val number: String = "",
    val type: String = "",
    val isBlocked: Boolean = false,
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
