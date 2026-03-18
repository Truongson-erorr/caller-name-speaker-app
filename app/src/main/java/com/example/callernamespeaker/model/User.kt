package com.example.callernamespeaker.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "user",
    val connections: List<String> = emptyList()
)