package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.viewmodel.WebsiteViewModel

@Composable
fun WebsiteScreen(
    navController: NavController,
    viewModel: WebsiteViewModel = viewModel()) {
    val isLoading by viewModel.isLoading.collectAsState()
    val result by viewModel.result.collectAsState()

    var url by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Dán link website") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { viewModel.checkWebsiteSafety(url) }) {
            Text("Kiểm tra")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
                Text("AI đang phân tích website...", Modifier.padding(top = 8.dp))
            }
            result != null -> {
                Text(result ?: "", Modifier.padding(top = 8.dp))
            }
        }
    }
}
