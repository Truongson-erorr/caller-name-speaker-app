package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.Report
import com.example.callernamespeaker.model.ReportGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminReportsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _reports = MutableStateFlow<List<ReportGroup>>(emptyList())
    val reports: StateFlow<List<ReportGroup>> = _reports

    fun loadReports() {
        db.collection("Reports")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    _reports.value = value.documents.map { doc ->

                        val phone = doc.id
                        val reportCount = doc.getLong("reportCount")?.toInt() ?: 0
                        val lastReported =
                            doc.getTimestamp("lastReported")?.toDate()?.time

                        val reportsList =
                            (doc["reports"] as? List<Map<String, Any>>)
                                ?.map {
                                    Report(
                                        userId = it["userId"] as? String ?: "",
                                        reason = it["reason"] as? String ?: "",
                                        timestamp = (it["timestamp"] as? Long)
                                            ?: System.currentTimeMillis()
                                    )
                                } ?: emptyList()

                        ReportGroup(
                            phone = phone,
                            reportCount = reportCount,
                            lastReported = lastReported,
                            reports = reportsList
                        )
                    }
                }
            }
    }
}
