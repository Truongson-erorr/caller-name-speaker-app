package com.example.callernamespeaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.callernamespeaker.viewmodel.CallStatsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class CallReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        if (state != TelephonyManager.EXTRA_STATE_RINGING || number.isNullOrEmpty()) return

        val cleanedNumber = number.replace("+84", "0").replace(" ", "")

        CallStatsViewModel().recordIncomingCall(cleanedNumber)

        if (isNumberBlocked(context, cleanedNumber)) {
            speakTTS(context, cleanedNumber,
                "Cảnh báo. Số $cleanedNumber đã bị bạn chặn")
            return
        }

        val today = LocalDate.now().toString()
        val statsDocId = "${cleanedNumber}_$today"

        val db = FirebaseFirestore.getInstance()

        db.collection("CallStats")
            .document(statsDocId)
            .get()
            .addOnSuccessListener { statDoc ->

                val callCount = statDoc.getLong("callCount") ?: 0

                db.collection("Reports")
                    .document(cleanedNumber)
                    .get()
                    .addOnSuccessListener { reportDoc ->

                        val reportCount =
                            if (reportDoc.exists())
                                reportDoc.getLong("count") ?: 1
                            else 0

                        val contactName = getContactName(context, cleanedNumber)
                        val isInContacts = contactName != null

                        val spamScore = calculateSpamScore(
                            callCount,
                            reportCount,
                            isInContacts
                        )

                        if (spamScore >= 10) {

                            val warningText =
                                "Cảnh báo. Số $cleanedNumber có nguy cơ spam cao (Điểm spam: $spamScore)"

                            Toast.makeText(
                                context,
                                "$warningText",
                                Toast.LENGTH_LONG
                            ).show()

                            speakTTS(context, cleanedNumber, warningText)

                        } else {
                            speakCallerNormally(context, cleanedNumber)
                        }
                    }
                    .addOnFailureListener {
                        speakCallerNormally(context, cleanedNumber)
                    }
            }
            .addOnFailureListener {
                speakCallerNormally(context, cleanedNumber)
            }
    }

    private fun calculateSpamScore(
        callCount: Long,
        reportCount: Long,
        isInContacts: Boolean
    ): Int {

        var score = 0

        // 1. Tần suất gọi
        if (callCount >= 10) score += 5
        else if (callCount >= 5) score += 3

        // 2. Report cộng đồng
        score += (reportCount * 3).toInt()

        // 3. Không có trong danh bạ
        if (!isInContacts) score += 2

        return score
    }

    private fun speakTTS(context: Context, phone: String, text: String) {
        val intent = Intent(context, CallTTSService::class.java).apply {
            putExtra("phone_number", phone)
            putExtra("display_name", text)
            putExtra("is_in_contacts", true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }

    private fun isNumberBlocked(context: Context, number: String): Boolean {
        val prefs = context.getSharedPreferences(
            "blocked_numbers",
            Context.MODE_PRIVATE
        )
        return prefs.contains(number)
    }

    private fun speakCallerNormally(context: Context, phoneNumber: String) {

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("tts_enabled", true)) return

        val contactName = getContactName(context, phoneNumber)
        val displayName = contactName ?: phoneNumber
        val isInContacts = contactName != null

        val intent = Intent(context, CallTTSService::class.java).apply {
            putExtra("phone_number", phoneNumber)
            putExtra("display_name", displayName)
            putExtra("is_in_contacts", isInContacts)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }

    private fun getContactName(context: Context, phoneNumber: String): String? {

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        val cursor = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) return it.getString(0)
        }
        return null
    }
}