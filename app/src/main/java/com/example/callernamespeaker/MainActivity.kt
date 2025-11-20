package com.example.callernamespeaker

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.callernamespeaker.navigation.AppNavGraph
import com.example.callernamespeaker.ui.theme.CallerNameSpeakerTheme

class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CallerNameSpeakerTheme {
                val navController = rememberNavController()
                var callList by remember { mutableStateOf<List<CallEntry>>(emptyList()) }

                // ⚡ Đăng ký xin quyền
                val requestPermissions = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.all { it.value }
                    if (granted) {
                        callList = CallLogHelper.getCallHistory(this)
                    } else {
                        Toast.makeText(
                            this,
                            "Cần cấp đầy đủ quyền để hoạt động",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                LaunchedEffect(Unit) {
                    requestPermissions.launch(requiredPermissions)
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(
                        navController = navController,
                        callList = callList
                    )
                }
            }
        }
    }
}
