package com.example.callernamespeaker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.Comment
import com.example.callernamespeaker.model.Reply
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _replies = MutableStateFlow<Map<String, List<Reply>>>(emptyMap())
    val replies: StateFlow<Map<String, List<Reply>>> = _replies

    /** 🔹 Lấy tất cả comment của bài viết */
    fun fetchComments(postId: String) {
        db.collection("Posts").document(postId).collection("Comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("CommentVM", "Lỗi khi fetch comment", error)
                    return@addSnapshotListener
                }
                val list = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _comments.value = list
            }
    }

    /** 🔹 Lấy các reply của 1 comment */
    fun fetchReplies(postId: String, commentId: String) {
        db.collection("Posts").document(postId)
            .collection("Comments").document(commentId)
            .collection("Replies")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("CommentVM", "Lỗi khi fetch reply", error)
                    return@addSnapshotListener
                }
                val repliesList = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Reply::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _replies.value = _replies.value.toMutableMap().apply {
                    this[commentId] = repliesList
                }
            }
    }

    /** 🔹 Thêm comment mới */
    fun addComment(postId: String, userName: String, content: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val commentRef = db.collection("Posts").document(postId)
            .collection("Comments").document()

        val comment = Comment(
            id = commentRef.id,
            userName = userName,
            postId = postId,
            userId = currentUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        commentRef.set(comment)
            .addOnFailureListener { e -> Log.e("CommentVM", "Lỗi khi thêm comment", e) }
    }

    /** 🔹 Thêm reply cho comment */
    fun addReply(postId: String, commentId: String, userName: String, content: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        val reply = Reply(
            userName = userName,
            userId = currentUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        db.collection("Posts").document(postId)
            .collection("Comments").document(commentId)
            .collection("Replies")
            .add(reply)
            .addOnFailureListener { e -> Log.e("CommentVM", "Lỗi khi thêm reply", e) }
    }
}
