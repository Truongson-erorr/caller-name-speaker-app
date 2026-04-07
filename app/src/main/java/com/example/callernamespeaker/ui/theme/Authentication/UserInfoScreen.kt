package com.example.callernamespeaker.ui.theme.Authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
            .padding(24.dp)
    ) {

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Hoàn tất hồ sơ",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            "Vui lòng nhập thông tin để tiếp tục",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Họ và tên", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Person, null, tint = Color(0xFF2A2AFC))
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF1A1F2C),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF2A2AFC),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Email, null, tint = Color(0xFF2A2AFC))
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF1A1F2C),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF2A2AFC),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray
            )
        )

        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                val uid = auth.currentUser?.uid
                if (uid.isNullOrBlank()) {
                    error = "Không tìm thấy người dùng"
                    return@Button
                }
                if (name.isBlank() || email.isBlank()) {
                    error = "Vui lòng nhập đầy đủ thông tin"
                    return@Button
                }

                isLoading = true
                error = null

                val data = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "phoneNumber" to auth.currentUser?.phoneNumber,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("Users").document(uid)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        isLoading = false
                        Toast.makeText(context, "Hoàn tất hồ sơ", Toast.LENGTH_SHORT).show()
                        navController.navigate("MainScreen") {
                            popUpTo("UserInfoScreen") { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        isLoading = false
                        error = it.localizedMessage
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A2AFC),
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    "HOÀN TẤT",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
