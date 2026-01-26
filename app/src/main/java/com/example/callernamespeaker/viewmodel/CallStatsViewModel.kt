package com.example.callernamespeaker.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDate

class CallStatsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    fun recordIncomingCall(phone: String) {
        val today = LocalDate.now().toString()
        val docId = "${phone}_$today"
        val docRef = db.collection("CallStats").document(docId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCount = snapshot.getLong("callCount") ?: 0

            transaction.set(
                docRef,
                mapOf(
                    "phone" to phone,
                    "date" to today,
                    "callCount" to currentCount + 1,
                    "lastCall" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
        }
    }
}
