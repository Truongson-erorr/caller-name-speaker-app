package com.example.callernamespeaker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.ConnectionRequest
import com.example.callernamespeaker.model.FamilyMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class FamilyViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private val _familyMembers = MutableStateFlow<List<FamilyMember>>(emptyList())
    val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers

    private val _connectionRequests = MutableStateFlow<List<ConnectionRequest>>(emptyList())
    val connectionRequests: StateFlow<List<ConnectionRequest>> = _connectionRequests

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        ensureCurrentUserInFamily()
        loadFamilyMembers()
        loadConnectionRequests()
    }

    private fun ensureCurrentUserInFamily() {
        val user = currentUser ?: return
        val userUid = user.uid

        val userDocRef = db.collection("Users").document(userUid)
        val familyRef = db.collection("Family").document(userUid)
            .collection("members")
            .document(userUid)

        userDocRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) return@addOnSuccessListener

            val name = doc.getString("name") ?: user.displayName ?: "Tôi"
            val email = doc.getString("email") ?: user.email ?: "Không có email"
            val phone = doc.getString("phoneNumber") ?: "Không có số điện thoại"

            val member = FamilyMember(
                id = userUid,
                name = name,
                nickname = "Tôi",
                phoneNumber = phone,
                email = email,
                relation = "Tôi",
                status = "accepted",
                inviterId = userUid,
                invitedUserId = userUid
            )

            familyRef.get().addOnSuccessListener { memberDoc ->
                if (!memberDoc.exists()) {
                    familyRef.set(member)
                }
            }
        }
    }

    private fun loadFamilyMembers() {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("members")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject(FamilyMember::class.java) }
                    _familyMembers.value = list
                }
            }
    }

    fun sendConnectionRequest(targetEmail: String) {
        val sender = currentUser ?: return

        db.collection("Users").document(sender.uid)
            .get()
            .addOnSuccessListener { senderDoc ->
                val senderName = senderDoc.getString("name") ?: "Người dùng"
                val senderEmail = senderDoc.getString("email") ?: sender.email ?: ""

                db.collection("Users")
                    .whereEqualTo("email", targetEmail)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            _message.value = "Không tìm thấy người dùng có email này."
                            return@addOnSuccessListener
                        }

                        val targetUser = snapshot.documents.first()
                        val targetUid = targetUser.id

                        val request = ConnectionRequest(
                            id = UUID.randomUUID().toString(),
                            fromUid = sender.uid,
                            fromName = senderName,
                            fromPhone = senderEmail,
                            status = "pending"
                        )

                        db.collection("Family")
                            .document(targetUid)
                            .collection("requests")
                            .document(request.id)
                            .set(request)
                            .addOnSuccessListener {
                                _message.value = "Đã gửi lời mời kết nối!"
                            }
                            .addOnFailureListener {
                                _message.value = "Gửi lời mời thất bại: ${it.localizedMessage}"
                            }
                    }
            }
    }

    private fun loadConnectionRequests() {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("requests")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull {
                        val req = it.toObject(ConnectionRequest::class.java)
                        req?.copy(id = it.id)
                    }
                    _connectionRequests.value = list
                }
            }
    }

    fun acceptConnectionRequest(request: ConnectionRequest) {
        val me = currentUser ?: return
        val db = FirebaseFirestore.getInstance()

        val requestRef = db.collection("Family").document(me.uid)
            .collection("requests").document(request.id)
        requestRef.update("status", "accepted")
            .addOnSuccessListener {
                _connectionRequests.value = _connectionRequests.value.filter { it.id != request.id }
            }
            .addOnFailureListener { e ->
                _message.value = "Không thể cập nhật request: ${e.message}"
            }

        val meRef = db.collection("Users").document(me.uid)
        val requesterRef = db.collection("Users").document(request.fromUid)

        meRef.update("connections", FieldValue.arrayUnion(request.fromUid))
            .addOnSuccessListener {
                Log.d("FamilyViewModel", "✅ Added requester to my connections")
            }
            .addOnFailureListener { e ->
                Log.e("FamilyViewModel", "❌ Failed to add requester: ${e.message}")
            }

        requesterRef.update("connections", FieldValue.arrayUnion(me.uid))
            .addOnSuccessListener {
                Log.d("FamilyViewModel", "✅ Added me to requester connections")
            }
            .addOnFailureListener { e ->
                Log.e("FamilyViewModel", "❌ Failed to update requester connections: ${e.message}")
            }

        db.collection("Users").document(me.uid).get()
            .addOnSuccessListener { meDoc ->
                val myName = meDoc.getString("name") ?: "Người dùng"
                val myEmail = meDoc.getString("email") ?: me.email ?: ""

                val myMember = FamilyMember(
                    id = request.fromUid,
                    name = request.fromName,
                    relation = "Người thân",
                    phoneNumber = request.fromPhone,
                    status = "accepted",
                    inviterId = request.fromUid,
                    invitedUserId = me.uid
                )

                val theirMember = FamilyMember(
                    id = me.uid,
                    name = myName,
                    relation = "Người thân",
                    phoneNumber = myEmail,
                    status = "accepted",
                    inviterId = me.uid,
                    invitedUserId = request.fromUid
                )

                db.collection("Family").document(me.uid)
                    .collection("members").document(request.fromUid)
                    .set(myMember)

                db.collection("Family").document(request.fromUid)
                    .collection("members").document(me.uid)
                    .set(theirMember)

                _familyMembers.value = (_familyMembers.value + myMember).distinctBy { it.id }
            }
            .addOnFailureListener { e ->
                _message.value = "Không thể lấy thông tin người dùng: ${e.message}"
            }
    }

    fun rejectConnectionRequest(requestId: String) {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("requests").document(requestId)
            .update("status", "rejected")
    }

    fun deleteFamilyMember(memberId: String) {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("members").document(memberId)
            .delete()
    }

}
