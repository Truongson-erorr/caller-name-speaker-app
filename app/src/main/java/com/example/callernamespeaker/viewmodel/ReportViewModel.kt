package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.Report
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ReportViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun reportPhoneNumber(
        phone: String,
        userId: String,
        reason: String,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        val docRef = db.collection("Reports").document(phone)
        val newReport = Report(userId, reason)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("reportCount") ?: 0

            transaction.set(
                docRef,
                mapOf(
                    "reportCount" to currentCount + 1,
                    "lastReported" to FieldValue.serverTimestamp(),
                    "reports" to FieldValue.arrayUnion(newReport)
                ),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            onComplete()
        }.addOnFailureListener {
            onError(it.message ?: "Lỗi không xác định")
        }
    }

    fun fetchReportStats(
        phone: String,
        onResult: (Int, List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("Reports").document(phone)
            .get()
            .addOnSuccessListener { snapshot ->
                val count = snapshot.getLong("reportCount")?.toInt() ?: 0
                val reasons = (snapshot["reports"] as? List<Map<String, Any>>)
                    ?.mapNotNull { it["reason"] as? String } ?: emptyList()

                onResult(count, reasons)
            }
            .addOnFailureListener {
                onError(it.message ?: "Lỗi truy vấn")
            }
    }
}
