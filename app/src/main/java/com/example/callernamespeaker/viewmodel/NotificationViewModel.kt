package com.example.personalexpensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        listenToNotifications()
    }

    private fun listenToNotifications() {
        db.collection("notifications")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                val list = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _notifications.value = list
            }
    }

    fun addNotification(title: String, message: String) {
        viewModelScope.launch {
            val newNotification = Notification(
                title = title,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            db.collection("notifications")
                .add(newNotification)
                .addOnFailureListener { it.printStackTrace() }
        }
    }

    fun deleteNotification(id: String) {
        db.collection("notifications")
            .document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
    }
}
