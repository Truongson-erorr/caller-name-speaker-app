package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val phone = remember { mutableStateOf("") }
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
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
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            imageVector = Icons.Default.LockReset,
            contentDescription = null,
            tint = Color(0xFF2575FC),
            modifier = Modifier.size(92.dp)
        )

        Text(
            text = "Quên mật khẩu",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2575FC)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(
            text = "Nhập số điện thoại để nhận mã khôi phục",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

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
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
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
                Text("GỬI MÃ KHÔI PHỤC", fontWeight = FontWeight.Bold)
            }
        }
    }
}
