package com.example.callernamespeaker.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,

    val phoneNumber: String = "",
    val callerName: String = ""
)