package com.example.callernamespeaker.ui.theme

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val activity = context as android.app.Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    var showDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<EmergencyNumber?>(null) }
    var pendingCallNumber by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            100
        )
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && pendingCallNumber != null) {

                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$pendingCallNumber")
                }

                context.startActivity(callIntent)
                pendingCallNumber = null
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                    selectedContact = contact
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
                    EmergencyNumber(phone, name, false)
                )
                showDialog = false
            }
        )
    }

    if (selectedContact != null) {
        ContactActionDialog(
            contact = selectedContact!!,
            onDismiss = { selectedContact = null },
            onCallOnly = {
                callNumber(context, selectedContact!!.number)
                selectedContact = null
            },
            onSmsAndCall = {
                sendLocationAndOpenSms(
                    context,
                    selectedContact!!.number
                ) {
                    pendingCallNumber = it
                }
                selectedContact = null
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

fun callNumber(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$number")
    context.startActivity(intent)
}

fun sendLocationAndOpenSms(
    context: Context,
    phone: String,
    onSmsOpened: (String) -> Unit
) {

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as android.app.Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            100
        )
        return
    }

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.getCurrentLocation(
        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->

        val message = if (location != null) {

            val lat = location.latitude
            val lng = location.longitude

            val googleMapLink =
                "https://www.google.com/maps/search/?api=1&query=$lat,$lng"

            """
            Tôi đang gặp tình huống khẩn cấp
          
            Tọa độ: $lat, $lng
            
            Vị trí của tôi:
            $googleMapLink
            """.trimIndent()

        } else {
            "Tôi đang gặp tình huống khẩn cấp. Không thể lấy vị trí."
        }

        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phone")
            putExtra("sms_body", message)
        }
        context.startActivity(smsIntent)

        onSmsOpened(phone)
    }
}
