package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.CallAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallAlertViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _alerts = MutableStateFlow<List<CallAlert>>(emptyList())
    val alerts: StateFlow<List<CallAlert>> = _alerts

    private val createdNotificationIds = mutableSetOf<String>()

    fun listenCallAlerts() {

        val uid = FirebaseAuth.getInstance().uid ?: return

        db.collection("call_alerts")
            .whereEqualTo("receiverId", uid)
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(CallAlert::class.java)
                } ?: emptyList()

                _alerts.value = list

                list.forEach { alert ->
                    createNotificationFromAlert(alert)
                }
            }
    }

    private fun createNotificationFromAlert(alert: CallAlert) {

        if (createdNotificationIds.contains(alert.id)) return
        createdNotificationIds.add(alert.id)

        val notification = hashMapOf(
            "userId" to alert.receiverId,
            "title" to "Cảnh báo người thân nhận cuộc gọi lạ",
            "message" to "${alert.receiverName} nhận cuộc gọi từ ${alert.callerNumber}",
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )

        db.collection("notifications")
            .add(notification)
    }
}