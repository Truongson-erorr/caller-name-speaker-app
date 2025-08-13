package com.example.callernamespeaker.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.callernamespeaker.model.BlockedNumber
import com.google.firebase.auth.FirebaseAuth

class BlacklistViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var blockedList by mutableStateOf<List<BlockedNumber>>(emptyList())
        private set


    fun loadBlockedNumbers() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Blockuser")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                blockedList = result.mapNotNull { it.toObject(BlockedNumber::class.java) }
            }
    }

    fun addToBlacklist(number: String, type: String, onDone: () -> Unit = {}) {
        val cleaned = number.trim().replace("+84", "0").replace(" ", "")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val blocked = BlockedNumber(
            number = cleaned,
            type = type,
            createdAt = System.currentTimeMillis(),
            userId = userId
        )

        db.collection("Blockuser")
            .add(blocked)
            .addOnSuccessListener { onDone() }
    }

    fun removeFromBlacklist(number: String, onSuccess: () -> Unit = {}) {
        val cleaned = number.trim().replace("+84", "0").replace(" ", "")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Blockuser")
            .whereEqualTo("number", cleaned)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    db.collection("Blockuser").document(doc.id).delete()
                }
                onSuccess()
                loadBlockedNumbers()
            }
    }

    fun shareBlockedList(context: Context, numbers: List<String>) {
        val textToShare = buildString {
            append("Danh sách số bị chặn:\n\n")
            numbers.forEachIndexed { index, number ->
                append("${index + 1}. $number\n")
            }
        }

        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }

        val chooser = android.content.Intent.createChooser(intent, "Chia sẻ danh sách chặn")
        context.startActivity(chooser)
    }


}
