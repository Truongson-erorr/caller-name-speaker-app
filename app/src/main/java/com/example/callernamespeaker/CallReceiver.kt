package com.example.callernamespeaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING && !number.isNullOrEmpty()) {
                val cleanedNumber = number.replace("+84", "0").replace(" ", "")

                if (isNumberBlocked(context, cleanedNumber)) {
                    val warningText = "Cảnh báo. Số $cleanedNumber đã bị bạn chặn"

                    Toast.makeText(context, "⚠️ $warningText", Toast.LENGTH_LONG).show()

                    val speakIntent = Intent(context, CallTTSService::class.java).apply {
                        putExtra("text_to_speak", warningText)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(speakIntent)
                    } else {
                        context.startService(speakIntent)
                    }
                    return
                }

                // Kiểm tra xem số này có bị báo cáo không
                FirebaseFirestore.getInstance()
                    .collection("Reports")
                    .document(cleanedNumber)
                    .get()
                    .addOnSuccessListener { document ->
                        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        if (!prefs.getBoolean("tts_enabled", true)) return@addOnSuccessListener

                        if (document.exists()) {
                            val count = document.getLong("reportCount") ?: 0
                            val reports = document.get("reports") as? List<Map<String, Any>>
                            val latestReason = reports?.lastOrNull()?.get("reason")?.toString() ?: "Không rõ"

                            val warningText = "Số $cleanedNumber đã bị $count người báo cáo: $latestReason"

                            Toast.makeText(context, "⚠️ $warningText", Toast.LENGTH_LONG).show()

                            val speakIntent = Intent(context, CallTTSService::class.java).apply {
                                putExtra("text_to_speak", warningText)
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(speakIntent)
                            } else {
                                context.startService(speakIntent)
                            }
                        } else {
                            // Nếu không có báo cáo → đọc tên hoặc "số lạ"
                            speakCallerNormally(context, number)
                        }
                    }
                    .addOnFailureListener {
                        // Nếu truy vấn Firestore lỗi, vẫn đọc như bình thường
                        speakCallerNormally(context, number)
                    }
            }
        }
    }

    private fun isNumberBlocked(context: Context, number: String): Boolean {
        val prefs = context.getSharedPreferences("blocked_numbers", Context.MODE_PRIVATE)
        return prefs.contains(number)
    }

    private fun getContactName(context: Context, phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return null
    }

    private fun speakCallerNormally(context: Context, phoneNumber: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("tts_enabled", true)) return

        val contactName = getContactName(context, phoneNumber)
        val nameToSpeak = contactName ?: "số lạ: ${phoneNumber.toCharArray().joinToString(" ") { it.toString() }}"

        val speakIntent = Intent(context, CallTTSService::class.java).apply {
            putExtra("text_to_speak", nameToSpeak)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(speakIntent)
        } else {
            context.startService(speakIntent)
        }
    }
}
