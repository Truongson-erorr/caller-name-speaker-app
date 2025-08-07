package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebsiteScreen(
    navController: NavController
) {
    var urlInput by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf<String?>(null) }
    var domain by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val blacklistedSites = listOf(
        "phishing-example.com",
        "scam-site.net",
        "fakebank.org",
        "dangerous-site.xyz"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 8.dp)
                    .size(26.dp)
            )
            Text(
                text = "Kiểm Tra Website",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        TextField(
            value = urlInput,
            onValueChange = { urlInput = it },
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Dán link website vào đây...") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val input = urlInput.text.trim().lowercase()
                val cleanDomain = input
                    .removePrefix("https://")
                    .removePrefix("http://")
                    .removePrefix("www.")
                    .substringBefore("/")
                domain = cleanDomain

                val isUnsafe = blacklistedSites.any { site ->
                    cleanDomain.contains(site)
                }

                result = if (isUnsafe) "❌ Website có thể không an toàn!" else "✅ Website an toàn."
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Kiểm tra", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        result?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (it.contains("an toàn")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Thông tin Website", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    domain?.let {
                        Text(text = "Tên miền: $it", modifier = Modifier.padding(top = 8.dp))
                    }

                    Text(
                        text = "Trạng thái: $result",
                        color = if (result!!.contains("an toàn")) Color(0xFF2E7D32) else Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (result!!.contains("không an toàn")) {
                        Text(
                            text = "⚠️ Gợi ý: Không nhập thông tin cá nhân hoặc đăng nhập vào website này.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
