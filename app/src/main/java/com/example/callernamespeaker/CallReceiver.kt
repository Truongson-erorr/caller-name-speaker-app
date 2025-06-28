package com.example.callernamespeaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            if (state == TelephonyManager.EXTRA_STATE_RINGING && !number.isNullOrEmpty()) {
                val cleanedNumber = number.replace("+84", "0").replace(" ", "")

                if (isNumberBlocked(context, cleanedNumber)) {
                    val warningText = "Cảnh báo. Số $cleanedNumber đã bị bạn chặn"

                    // Hiển thị toast như cũ
                    Toast.makeText(
                        context,
                        "⚠️ $warningText",
                        Toast.LENGTH_LONG
                    ).show()

                    // Gửi intent để đọc cảnh báo bằng TTS
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

                // Nếu không bị chặn thì tiếp tục đọc tên
                val contactName = getContactName(context, number)
                val nameToSpeak = contactName ?: "số lạ: ${number.toCharArray().joinToString(" ") { it.toString() }}"

                val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                if (!prefs.getBoolean("tts_enabled", true)) return

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
}
