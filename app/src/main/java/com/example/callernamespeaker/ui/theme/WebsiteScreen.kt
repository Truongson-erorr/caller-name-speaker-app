package com.example.callernamespeaker.ui.theme

import WebsiteViewModel
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebsiteScreen(
    navController: NavController,
    viewModel: WebsiteViewModel = viewModel()
) {
    val context = LocalContext.current

    val urlInput = viewModel.urlInput
    val isLoading = viewModel.isLoading
    val result = viewModel.result
    val domain = viewModel.domain

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { navController.popBackStack() }
                    .padding(start = 8.dp)
                    .size(26.dp)
            )

            Text(
                text = "Kiểm Tra Website",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        TextField(
            value = urlInput,
            onValueChange = { viewModel.urlInput = it },
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
                viewModel.checkWebsiteSafety(apiKey = BuildConfig.API_KEY_GEMINI)
                if (viewModel.result != null) {
                    Toast.makeText(context, viewModel.result, Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2575FC),
                contentColor = Color.White
            )
        ) {
            Text("Kiểm tra", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF2575FC),
                    strokeWidth = 4.dp
                )
            }
        } else {
            result?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (it.contains("an toàn", ignoreCase = true))
                            Color.White else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Thông tin Website", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        domain?.let { d ->
                            Text(text = "Tên miền: $d", modifier = Modifier.padding(top = 8.dp))
                        }

                        Text(
                            text = "Trạng thái: $it",
                            color = if (it.contains("an toàn", ignoreCase = true))
                                Color(0xFF2E7D32) else Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        if (it.contains("không an toàn", ignoreCase = true)) {
                            Text(
                                text = "Gợi ý: Không nhập thông tin cá nhân hoặc đăng nhập vào website này.",
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
}
