package com.example.callernamespeaker.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    navController: NavController,
    verificationId: String,
    phoneNumber: String
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val otpValues = remember { List(6) { mutableStateOf("") } }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
        }

        Spacer(modifier = Modifier.height(36.dp))

        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFF2A2AFC),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Xác minh OTP",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White
        )

        Text(
            "Mã đã gửi đến $phoneNumber",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            otpValues.forEach { otp ->
                TextField(
                    value = otp.value,
                    onValueChange = {
                        if (it.length <= 1 && it.all(Char::isDigit)) {
                            otp.value = it
                        }
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .height(64.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFF1A1F2C),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2A2AFC)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val otp = otpValues.joinToString("") { it.value }
                if (otp.length < 6) {
                    Toast.makeText(context, "Nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (!task.isSuccessful) {
                            Toast.makeText(context, "Sai mã OTP", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }

                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        db.collection("Users").document(uid).get()
                            .addOnSuccessListener { doc ->

                                if (!doc.exists()) {
                                    navController.navigate("UserInfoScreen") {
                                        popUpTo("otp_verification") { inclusive = true }
                                    }
                                    return@addOnSuccessListener
                                }

                                val name = doc.getString("name").orEmpty()
                                val email = doc.getString("email").orEmpty()
                                val role = doc.getString("role") ?: "user"

                                if (name.isBlank() || email.isBlank()) {
                                    navController.navigate("UserInfoScreen") {
                                        popUpTo("otp_verification") { inclusive = true }
                                    }
                                    return@addOnSuccessListener
                                }

                                if (role == "admin") {
                                    navController.navigate("AdminMainScreen") {
                                        popUpTo("otp_verification") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("MainScreen") {
                                        popUpTo("otp_verification") { inclusive = true }
                                    }
                                }
                            }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            shape = RoundedCornerShape(26.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A2AFC),
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text("XÁC MINH", fontWeight = FontWeight.Bold)
            }
        }
    }
}
