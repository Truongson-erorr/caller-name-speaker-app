package com.example.callernamespeaker.viewmodel

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

    fun fetchComments(postId: String) {
        db.collection("Posts").document(postId).collection("Comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                val list = value?.documents?.mapNotNull { doc ->
                    val comment = doc.toObject(Comment::class.java)
                    comment?.copy(id = doc.id)
                } ?: emptyList()
                _comments.value = list
            }
    }

    fun fetchReplies(postId: String, commentId: String, onResult: (List<Reply>) -> Unit) {
        db.collection("Posts").document(postId)
            .collection("Comments").document(commentId)
            .collection("Replies")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                val replies = value?.documents?.mapNotNull { doc ->
                    val reply = doc.toObject(Reply::class.java)
                    reply?.copy(id = doc.id)
                } ?: emptyList()
                onResult(replies)
            }
    }

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
    }

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
    }
}
