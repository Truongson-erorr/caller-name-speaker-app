package com.example.callernamespeaker.model

data class Reply(
    val id: String = "",
    val userName: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
