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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    navController: NavController,
    verificationId: String,
    phoneNumber: String
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val otpValues = remember { List(6) { mutableStateOf("") } }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A)) // Nền tối
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFF2A2AFC),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Xác minh OTP",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Mã đã gửi đến $phoneNumber",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            otpValues.forEach { otpChar ->
                TextField(
                    value = otpChar.value,
                    onValueChange = {
                        if (it.length <= 1 && it.all { c -> c.isDigit() }) {
                            otpChar.value = it
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
                        containerColor = Color(0xFF1A1F2C), // nền tối ô OTP
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2A2AFC),
                        focusedTextColor = Color.White,
                    ),
                    placeholder = { Text("", color = Color.Gray) }
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val otp = otpValues.joinToString("") { it.value }

                if (otp.length < 6) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ 6 số OTP", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            navController.navigate("main") {
                                popUpTo("otp_verification") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Sai mã OTP", Toast.LENGTH_SHORT).show()
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
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("XÁC MINH", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
