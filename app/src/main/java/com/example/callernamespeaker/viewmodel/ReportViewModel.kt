package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
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
        val newReport = hashMapOf(
            "userId" to userId,
            "reason" to reason,
            "timestamp" to System.currentTimeMillis()
        )

        docRef.get()
            .addOnSuccessListener { snapshot ->

                val currentCount = snapshot.getLong("reportCount") ?: 0L

                docRef.set(
                    mapOf(
                        "reportCount" to currentCount + 1,
                        "lastReported" to FieldValue.serverTimestamp(),
                        "reports" to FieldValue.arrayUnion(newReport)
                    ),
                    SetOptions.merge()
                ).addOnSuccessListener {
                    onComplete()
                }.addOnFailureListener {
                    onError(it.message ?: "Lỗi ghi dữ liệu")
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Lỗi đọc dữ liệu")
            }
    }
}