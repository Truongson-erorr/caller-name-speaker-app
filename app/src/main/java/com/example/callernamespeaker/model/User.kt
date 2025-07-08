package com.example.callernamespeaker.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "user" // mặc định là user
)
