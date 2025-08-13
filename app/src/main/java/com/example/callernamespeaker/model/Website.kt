package com.example.callernamespeaker.model

data class Website(
    val id: String = "",
    val url: String = "",
    val category: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)