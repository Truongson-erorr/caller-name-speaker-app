package com.example.callernamespeaker.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.callernamespeaker.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(
        name: String,
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: return@addOnSuccessListener

                val userMap = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "role" to "user"
                )

                firestore.collection("Users")
                    .document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure("Không thể lưu người dùng: ${e.message}")
                    }
            }
            .addOnFailureListener {
                onFailure("Lỗi đăng ký: ${it.message}")
            }
    }

}
