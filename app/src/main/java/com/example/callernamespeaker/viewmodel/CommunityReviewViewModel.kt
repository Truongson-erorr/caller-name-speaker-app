package com.example.callernamespeaker.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.CallReview
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlin.random.Random

class CommunityReviewViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _reviews = mutableStateListOf<CallReview>()
    val reviews: List<CallReview> get() = _reviews

    fun loadReviews(phoneNumber: String) {
        db.collection("callReviews")
            .whereEqualTo("phoneNumber", phoneNumber)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _reviews.clear()
                    _reviews.addAll(snapshot.toObjects(CallReview::class.java))
                }
            }
    }

    fun addReview(phoneNumber: String, userName: String?, rating: Int, comment: String) {
        val displayName = userName?.takeIf { it.isNotBlank() }
            ?: "Người tham gia ${Random.nextInt(100, 999)}"

        val reviewId = db.collection("callReviews").document().id
        val review = CallReview(
            id = reviewId,
            phoneNumber = phoneNumber,
            userName = displayName,
            rating = rating,
            comment = comment,
            timestamp = Timestamp.now()
        )
        db.collection("callReviews").document(reviewId).set(review)
    }
}
