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

@Composable
fun WebsiteScreen(
    navController: NavController
) {
    var urlInput by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf<String?>(null) }
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
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
        {
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

        OutlinedTextField(
            value = urlInput,
            shape = RoundedCornerShape(16.dp),
            onValueChange = { urlInput = it },
            label = { Text("Nhập link website") },
            placeholder = { Text("https://example.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val normalizedUrl = urlInput.text.trim().lowercase()

                // Kiểm tra xem URL có trong blacklist không
                val isUnsafe = blacklistedSites.any { site ->
                    normalizedUrl.contains(site)
                }

                result = if (isUnsafe) "❌ Website có thể không an toàn!" else "✅ Website an toàn."
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Kiểm tra")
        }

        result?.let {
            Text(
                text = it,
                color = if (it.contains("an toàn")) Color(0xFF2E7D32) else Color.Red,
                fontSize = 18.sp
            )
        }
    }
}
