package com.example.callernamespeaker.model

data class CallAlert(
    val id: String = "",
    val callerNumber: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val time: Long = 0
)