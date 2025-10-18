package com.example.callernamespeaker.ui.theme

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val otp = remember { mutableStateOf("") }

    var isCodeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = Color(0xFF2575FC),
                modifier = Modifier.size(92.dp)
            )

            Text(
                text = "Tạo tài khoản mới",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2575FC)
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Đăng ký để sử dụng tính năng chặn cuộc gọi hiệu quả",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                textAlign = TextAlign.Center
            )

            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = { Text("Họ và tên") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                enabled = !isCodeSent,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF2575FC)
                )
            )

            TextField(
                value = phone.value,
                onValueChange = { phone.value = it },
                placeholder = { Text("Số điện thoại (vd: +84987654321)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                enabled = !isCodeSent,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF2575FC)
                )
            )

            if (isCodeSent) {
                TextField(
                    value = otp.value,
                    onValueChange = { otp.value = it },
                    placeholder = { Text("Nhập mã OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2575FC)
                    )
                )
            }

            Button(
                onClick = {
                    if (!isCodeSent) {
                        if (name.value.isBlank() || phone.value.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phone.value)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as Activity)
                            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    auth.signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                saveUserToFirestore(name.value, phone.value, context, navController)
                                            }
                                        }
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    isLoading = false
                                    Toast.makeText(context, "Gửi OTP thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                                override fun onCodeSent(
                                    vid: String,
                                    token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    isLoading = false
                                    isCodeSent = true
                                    verificationId = vid
                                    Toast.makeText(context, "Đã gửi mã OTP!", Toast.LENGTH_SHORT).show()
                                }
                            })
                            .build()

                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        val credential = PhoneAuthProvider.getCredential(verificationId, otp.value)
                        isLoading = true
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    saveUserToFirestore(name.value, phone.value, context, navController)
                                } else {
                                    Toast.makeText(context, "Xác thực thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2575FC),
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        if (isCodeSent) "XÁC NHẬN OTP" else "GỬI MÃ OTP",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(fontSize = 14.sp, text = "Đã có tài khoản? ")
                Text(
                    "Đăng nhập",
                    color = Color(0xFF2575FC),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("LoginScreen")
                    }
                )
            }
        }
    }
}

private fun saveUserToFirestore(
    name: String,
    phone: String,
    context: Context,
    navController: NavController
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val data = hashMapOf(
        "uid" to uid,
        "name" to name,
        "phoneNumber" to phone,
        "role" to "user"
    )

    FirebaseFirestore.getInstance().collection("Users").document(uid)
        .set(data)
        .addOnSuccessListener {
            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            navController.navigate("main") {
                popUpTo("register") { inclusive = true }
            }
        }
}
