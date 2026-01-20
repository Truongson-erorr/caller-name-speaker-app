package com.example.callernamespeaker.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener { user ->
                            Toast.makeText(activity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Xác thực thất bại: ${e.message}")
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onFailure("Lỗi gửi OTP: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    onCodeSent(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(
        context: Context,
        name: String?,
        phoneNumber: String,
        verificationId: String,
        otp: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    val userData = hashMapOf(
                        "uid" to user.uid,
                        "name" to (name ?: ""),
                        "phoneNumber" to phoneNumber,
                        "role" to "user"
                    )

                    firestore.collection("Users")
                        .document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Xác thực thành công!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Không thể lưu thông tin: ${e.message}")
                        }
                } else {
                    onFailure("Không lấy được thông tin người dùng.")
                }
            }
            .addOnFailureListener { e ->
                onFailure("Mã OTP sai hoặc hết hạn: ${e.message}")
            }
    }
}
