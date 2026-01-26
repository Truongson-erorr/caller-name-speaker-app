package com.example.callernamespeaker.model

data class CallStat(
    val phone: String = "",
    val date: String = "",
    val callCount: Int = 0,
    val lastCall: Long = 0L,
    val isGloballyBlocked: Boolean = false
)
