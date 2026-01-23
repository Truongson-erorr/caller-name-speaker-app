package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.NewsPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminPostsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableStateFlow<List<NewsPost>>(emptyList())
    val posts: StateFlow<List<NewsPost>> = _posts

    fun loadPosts() {
        db.collection("Posts")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    _posts.value = value.documents.map { doc ->
                        NewsPost(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            shortDescription = doc.getString("shortDescription") ?: "",
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            date = doc.getTimestamp("date")
                                ?.toDate()
                                ?.let {
                                    java.text.SimpleDateFormat("dd/MM/yyyy").format(it)
                                } ?: "",
                            isVisible = doc.getBoolean("isVisible") ?: true
                        )
                    }
                }
            }
    }

    fun deletePost(postId: String) {
        db.collection("Posts").document(postId).delete()
    }

    fun togglePostVisibility(postId: String, isVisible: Boolean) {
        db.collection("Posts")
            .document(postId)
            .update("isVisible", isVisible)
    }
}
