package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.PhoneLookup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.mutableStateListOf

class PhoneLookupViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val history = mutableStateListOf<PhoneLookup>()

    init {
        fetchHistory()
    }

    fun fetchHistory() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("PhoneLookup")
            .get()
            .addOnSuccessListener { result ->
                history.clear()
                history.addAll(
                    result.map { it.toObject(PhoneLookup::class.java) }
                        .filter { it.userId == uid }
                        .sortedByDescending { it.timestamp }
                )
            }
    }

    fun saveLookup(number: String, type: String, isBlocked: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val lookup = PhoneLookup(
            number = number,
            type = type,
            isBlocked = isBlocked,
            userId = uid,
            timestamp = System.currentTimeMillis()
        )

        db.collection("PhoneLookup")
            .add(lookup)
            .addOnSuccessListener {
                fetchHistory()
            }
    }

    fun getMostRecentReportReason(phone: String, callback: (String?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("Reports")
            .document(phone)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val reports = doc.get("reports") as? List<Map<String, Any>>
                    val latestReport = reports?.maxByOrNull {
                        it["timestamp"] as? Long ?: 0L
                    }
                    val reason = latestReport?.get("reason") as? String
                    callback(reason)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }



}
