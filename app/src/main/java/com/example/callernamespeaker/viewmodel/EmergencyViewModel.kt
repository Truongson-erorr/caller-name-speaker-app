package com.example.callernamespeaker.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.EmergencyNumber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmergencyViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _customContacts = mutableStateListOf<EmergencyNumber>()
    val customContacts: List<EmergencyNumber> = _customContacts

    init {
        loadContacts()
    }

    fun addContact(contact: EmergencyNumber) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("contacts")
            .document(userId)
            .collection("emergency_contacts")
            .add(contact)
            .addOnSuccessListener {
                _customContacts.add(contact)
            }
    }

    private fun loadContacts() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("contacts")
            .document(userId)
            .collection("emergency_contacts")
            .get()
            .addOnSuccessListener { result ->
                _customContacts.clear()
                for (doc in result) {
                    val contact = doc.toObject(EmergencyNumber::class.java)
                    _customContacts.add(contact)
                }
            }
    }
}