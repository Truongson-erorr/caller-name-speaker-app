package com.example.callernamespeaker.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF1976D2))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hoàn tất hồ sơ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        fun Modifier.formModifier() = this
            .fillMaxWidth()
            .padding(bottom = 12.dp)

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Họ và tên", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1976D2))
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.formModifier(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD),
                cursorColor = Color(0xFF1976D2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email (không bắt buộc)", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2))
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.formModifier(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD),
                cursorColor = Color(0xFF1976D2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val uid = auth.currentUser?.uid
                if (uid.isNullOrBlank() || name.isBlank()) {
                    errorMessage = "Vui lòng nhập họ tên"
                    return@Button
                }

                isLoading = true
                val userData = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "role" to "user"
                )

                db.collection("Users").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        isLoading = false
                        Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        navController.navigate("main") {
                            popUpTo("user_info") { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        isLoading = false
                        errorMessage = "Lỗi khi lưu dữ liệu: ${it.localizedMessage}"
                    }
            },
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF90CAF9)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text("HOÀN TẤT", fontSize = 14.sp)
            }
        }
    }
}
