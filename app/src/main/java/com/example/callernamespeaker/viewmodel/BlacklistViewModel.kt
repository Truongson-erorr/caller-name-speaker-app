package com.example.callernamespeaker.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.example.callernamespeaker.model.BlockedNumber

class BlacklistViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var blockedList by mutableStateOf<List<BlockedNumber>>(emptyList())
        private set


    fun loadBlockedNumbers() {
        db.collection("Blockuser").get()
            .addOnSuccessListener { result ->
                blockedList = result.mapNotNull { it.toObject(BlockedNumber::class.java) }
            }
    }
}
