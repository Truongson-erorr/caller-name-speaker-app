package com.example.callernamespeaker.model

data class ReportGroup(
    val phone: String,
    val reportCount: Int,
    val lastReported: Long?,
    val reports: List<Report>
)