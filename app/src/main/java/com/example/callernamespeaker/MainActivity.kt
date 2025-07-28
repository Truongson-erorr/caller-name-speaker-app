package com.example.callernamespeaker

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.callernamespeaker.navigation.AppNavGraph
import com.example.callernamespeaker.ui.theme.CallerNameSpeakerTheme
import com.example.callernamespeaker.ui.theme.RegisterScreen

class MainActivity : ComponentActivity() {

    val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (!granted) {
            Toast.makeText(this, "Cần cấp đầy đủ quyền để hoạt động", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions.launch(
            arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            )
        )

        setContent {
            CallerNameSpeakerTheme {
                val navController = rememberNavController()
                val context = this
                val callList = remember { CallLogHelper.getCallHistory(context) }

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
