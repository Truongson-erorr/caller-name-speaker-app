package com.example.callernamespeaker.ui.theme

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.callernamespeaker.model.EmergencyNumber
import com.example.callernamespeaker.viewmodel.EmergencyViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun EmergencyTab(
    navController: NavController,
    viewModel: EmergencyViewModel = viewModel()
) {

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val systemContacts = listOf(
        EmergencyNumber("113", "Công an", true),
        EmergencyNumber("114", "Cứu hỏa", true),
        EmergencyNumber("115", "Cấp cứu", true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1A))
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Số khẩn cấp",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {

            item { SectionTitle("Số hệ thống") }

            items(systemContacts) { contact ->
                EmergencyCard(contact) {
                    callNumber(context, contact.number)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Liên hệ cá nhân")
            }

            items(viewModel.customContacts) { contact ->
                EmergencyCard(contact) {
                    sendLocationAndCall(context, contact.number)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thêm liên hệ khẩn cấp", color = Color.White)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showDialog) {
        AddEmergencyDialog(
            onDismiss = { showDialog = false },
            onSave = { name, phone ->
                viewModel.addContact(
                    EmergencyNumber(
                        number = phone,
                        label = name,
                        isSystem = false
                    )
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF9CA3AF),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
@Composable
fun EmergencyCard(
    contact: EmergencyNumber,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(contact.label, color = Color.White)
                Text(contact.number, color = Color.Gray)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFD32F2F)
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun AddEmergencyDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                "Thêm liên hệ khẩn cấp",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text("Tên liên hệ", color = Color.Gray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111827),
                        unfocusedContainerColor = Color(0xFF111827),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = {
                        Text("Số điện thoại", color = Color.Gray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF111827),
                        unfocusedContainerColor = Color(0xFF111827),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text("Lưu", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Hủy")
            }
        }
    )
}

fun callNumber(context: android.content.Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$number")
    context.startActivity(intent)
}

fun sendLocationAndCall(context: Context, phone: String) {

    val hasLocationPermission =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val hasSmsPermission =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

    if (!hasLocationPermission || !hasSmsPermission) {
        callNumber(context, phone)
        return
    }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->

            val message = if (location != null) {
                "Tôi đang gặp tình huống khẩn cấp.\nVị trí của tôi:\nhttps://maps.google.com/?q=${location.latitude},${location.longitude}"
            } else {
                "Tôi đang gặp tình huống khẩn cấp."
            }

            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, message, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            callNumber(context, phone)
        }
}