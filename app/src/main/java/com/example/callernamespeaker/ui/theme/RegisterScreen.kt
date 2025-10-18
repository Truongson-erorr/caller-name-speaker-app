package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng ký tài khoản",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        fun Modifier.formFieldModifier() = this
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
            modifier = Modifier.formFieldModifier(),
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
            placeholder = { Text("Email", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF1976D2))
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.formFieldModifier(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD),
                cursorColor = Color(0xFF1976D2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Mật khẩu", fontSize = 13.sp) },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1976D2))
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.formFieldModifier(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE3F2FD),
                cursorColor = Color(0xFF1976D2),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Xác nhận mật khẩu", fontSize = 13.sp) },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF1976D2))
            },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF1976D2)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.formFieldModifier(),
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
                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "Vui lòng nhập đầy đủ thông tin"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "Mật khẩu không khớp"
                    return@Button
                }

                isLoading = true
                errorMessage = null
                viewModel.registerUser(
                    name = name,
                    email = email,
                    password = password,
                    context = context,
                    onSuccess = {
                        isLoading = false
                        navController.navigate("LoginScreen") {
                            popUpTo("RegisterScreen") { inclusive = true }
                        }
                    },
                    onFailure = {
                        isLoading = false
                        errorMessage = it
                    }
                )
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
                Text("Đăng ký", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = { navController.navigate("LoginScreen") }) {
            Text(
                "Đã có tài khoản? Đăng nhập",
                fontSize = 12.sp,
                color = Color(0xFF1976D2)
            )
        }
    }
}
