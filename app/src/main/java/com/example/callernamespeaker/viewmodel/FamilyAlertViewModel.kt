package com.example.callernamespeaker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.FamilyAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FamilyAlertViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _alerts = MutableStateFlow<List<FamilyAlert>>(emptyList())
    val alerts: StateFlow<List<FamilyAlert>> = _alerts

    fun listenFamilyAlerts() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("Users")
            .document(uid)
            .addSnapshotListener { snapshot, _ ->

                val connections =
                    snapshot?.get("connections") as? List<String>
                        ?: emptyList()

                listenAlertsFromConnections(connections)
            }
    }

    private fun listenAlertsFromConnections(memberIds: List<String>) {

        if (memberIds.isEmpty()) return

        db.collection("FamilyAlerts")
            .whereIn("memberId", memberIds)
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(FamilyAlert::class.java)
                        ?.copy(id = it.id)
                } ?: emptyList()

                _alerts.value = list
            }
    }
}