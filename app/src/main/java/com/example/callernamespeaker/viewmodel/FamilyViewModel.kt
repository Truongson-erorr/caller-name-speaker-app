package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.ConnectionRequest
import com.example.callernamespeaker.model.FamilyMember
import com.google.firebase.auth.FirebaseAuth
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
        loadFamilyMembers()
        loadConnectionRequests()
    }

    /**Tải danh sách thành viên đã kết nối **/
    fun loadFamilyMembers() {
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

    /**Gửi lời mời kết nối qua email **/
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

    /**Tải danh sách lời mời đến **/
    fun loadConnectionRequests() {
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

    /**Chấp nhận lời mời **/
    fun acceptConnectionRequest(request: ConnectionRequest) {
        val me = currentUser ?: return

        // Cập nhật trạng thái
        db.collection("Family").document(me.uid)
            .collection("requests").document(request.id)
            .update("status", "accepted")

        // Thêm hai bên vào Family list
        val myMember = FamilyMember(
            id = request.fromUid,
            name = request.fromName,
            relation = "Người thân",
            phoneNumber = request.fromPhone, // là email
            status = "accepted",
            inviterId = request.fromUid,
            invitedUserId = me.uid
        )

        val theirMember = FamilyMember(
            id = me.uid,
            name = me.displayName ?: "Người dùng",
            relation = "Người thân",
            phoneNumber = me.email ?: "",
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
    }

    /**Từ chối lời mời **/
    fun rejectConnectionRequest(requestId: String) {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("requests").document(requestId)
            .update("status", "rejected")
    }

    /** Xóa thành viên **/
    fun deleteFamilyMember(memberId: String) {
        val uid = currentUser?.uid ?: return
        db.collection("Family").document(uid)
            .collection("members").document(memberId)
            .delete()
    }
}
