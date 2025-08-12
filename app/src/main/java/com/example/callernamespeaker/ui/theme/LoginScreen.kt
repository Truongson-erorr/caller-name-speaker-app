package com.example.callernamespeaker.ui.theme

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

data class CountryCode(val emoji: String, val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val phone = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    val countryCodes = listOf(
        CountryCode("🇻🇳", "+84", "Việt Nam"),
        CountryCode("🇺🇸", "+1", "Mỹ"),
        CountryCode("🇰🇷", "+82", "Hàn Quốc"),
        CountryCode("🇯🇵", "+81", "Nhật Bản"),
        CountryCode("🇦🇺", "+61", "Úc"),
        CountryCode("🇩🇪", "+49", "Đức")
    )
    val selectedCountry = remember { mutableStateOf(countryCodes[0]) }
    val expanded = remember { mutableStateOf(false) }

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
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF2575FC),
                modifier = Modifier.size(92.dp)
            )

            Text(
                text = "Chào mừng đến BlockSon",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2575FC)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                text = "Bảo vệ bạn khỏi các cuộc gọi lừa đảo",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 1.dp, bottom = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {

                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                        .clickable { expanded.value = true }
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "${selectedCountry.value.emoji} ${selectedCountry.value.code}",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        countryCodes.forEach { country ->
                            DropdownMenuItem(
                                text = { Text("${country.emoji} ${country.code} - ${country.name}") },
                                onClick = {
                                    selectedCountry.value = country
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    placeholder = { Text("Số điện thoại") },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2575FC),
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {  navController.navigate("ForgotPasswordScreen") }
                ) {
                    Text(
                        "Quên mật khẩu",
                        color = Color(0xFF2575FC),
                        fontSize = 14.sp
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (phone.value.length < 6) {
                    Toast.makeText(context, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                val fullPhoneNumber = selectedCountry.value.code + phone.value

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(fullPhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(context as Activity)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                        navController.navigate("main")
                                    }
                                }
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            isLoading = false
                            Toast.makeText(context, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                            isLoading = false
                            navController.navigate("otp_verification/${verificationId}/${fullPhoneNumber}")
                        }
                    })
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
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
                Text("GỬI MÃ OTP", fontWeight = FontWeight.Bold)
            }
        }
    }
}
