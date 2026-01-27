package com.example.callernamespeaker.model

import com.google.firebase.Timestamp

data class CallStat(
    val phone: String = "",
    val date: String = "",
    val callCount: Long = 0,
    val lastCall: Timestamp? = null,
    val isGloballyBlocked: Boolean = false
)
