package com.example.callernamespeaker.model

data class ConnectionRequest(
    val id: String = "",
    val fromUid: String = "",
    val fromName: String = "",
    val fromPhone: String = "",
    val status: String = "pending"
)
