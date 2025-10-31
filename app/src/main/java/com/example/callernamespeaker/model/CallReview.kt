package com.example.callernamespeaker.model

data class CallReview(
    val id: String = "",
    val phoneNumber: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)