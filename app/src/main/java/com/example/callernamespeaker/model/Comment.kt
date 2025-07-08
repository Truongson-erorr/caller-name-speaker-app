package com.example.callernamespeaker.model

data class Comment(
    val id: String = "",
    val userName: String= "",
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
