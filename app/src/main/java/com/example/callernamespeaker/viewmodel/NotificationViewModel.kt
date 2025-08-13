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
        FirebaseFirestore.getInstance()
            .collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("NotificationVM", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        Notification(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0,
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    }
                    _notifications.value = list
                }
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
        FirebaseFirestore.getInstance()
            .collection("notifications")
            .document(id)
            .delete()
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
