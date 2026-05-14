package com.example.callernamespeaker.model

data class FamilyAlert(
    val id: String = "",
    val familyId: String = "",
    val memberId: String = "",
    val phoneNumber: String = "",
    val type: String = "",
    val time: Long = 0L,
    val read: Boolean = false
)