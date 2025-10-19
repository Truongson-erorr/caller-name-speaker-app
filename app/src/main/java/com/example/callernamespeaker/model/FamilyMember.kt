package com.example.callernamespeaker.model

data class FamilyMember(
    val id: String = "",
    val name: String = "",
    val relation: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val status: String = "pending",
    val inviterId: String = "",
    val invitedUserId: String = ""
)
