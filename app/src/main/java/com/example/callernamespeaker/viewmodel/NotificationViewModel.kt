package com.example.personalexpensetracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val userId: String
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        listenToNotifications(userId)
    }

    fun listenToNotifications(userId: String) {
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    fun addNotification(userId: String, title: String, message: String) {
        val notification = hashMapOf(
            "userId" to userId,
            "title" to title,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )

        FirebaseFirestore.getInstance()
            .collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                Log.d("Notification", "Notification added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Notification", "Error adding notification", e)
            }
    }

    fun deleteNotification(id: String) {
        db.collection("notifications")
            .document(id)
            .delete()
            .addOnFailureListener { it.printStackTrace() }
    }

    fun markAsRead(notificationId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = db.collection("notifications")
            .document(userId)
            .collection("user_notifications")
            .document(notificationId)

        docRef.update("isRead", true)
    }

}
