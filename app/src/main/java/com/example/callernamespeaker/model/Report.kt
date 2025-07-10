package com.example.callernamespeaker.model

data class Report(
    val userId: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


