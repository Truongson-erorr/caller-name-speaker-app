package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class CommentViewModel : ViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    private val db = FirebaseFirestore.getInstance()

    fun fetchComments(postId: String) {
        db.collection("Posts").document(postId).collection("Comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                val list = value?.documents?.mapNotNull { it.toObject(Comment::class.java) } ?: emptyList()
                _comments.value = list
            }
    }

    fun addComment(postId: String, userName: String, content: String) {
        val comment = Comment(userName = userName, content = content)
        db.collection("Posts").document(postId)
            .collection("Comments").add(comment)
    }
}
