package com.example.callernamespeaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.telephony.SmsMessage
import android.widget.Toast
import java.util.*
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    private var tts: TextToSpeech? = null

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<*>
            for (pdu in pdus) {
                val format = bundle.getString("format")
                val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
                val phoneNumber = sms.originatingAddress
                val message = sms.messageBody

                val pattern = Pattern.compile("(http|https)://[\\w\\-\\.\\?&=/]+")
                val matcher = pattern.matcher(message)

                if (matcher.find()) {
                    val foundLink = matcher.group()

                    Toast.makeText(
                        context,
                        "Tin nhắn từ $phoneNumber chứa đường link đáng ngờ: $foundLink",
                        Toast.LENGTH_LONG
                    ).show()

                    // Lấy trạng thái bật/tắt từ SharedPreferences
                    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val isEnabled = prefs.getBoolean("sms_tts_enabled", true) // mặc định là BẬT

                    // Chỉ đọc nếu người dùng bật
                    if (isEnabled) {
                        tts = TextToSpeech(context.applicationContext) { status ->
                            if (status == TextToSpeech.SUCCESS) {
                                tts?.language = Locale("vi", "VN")
                                val textToSpeak =
                                    "Cảnh báo! Bạn nhận được tin nhắn chứa đường link từ số $phoneNumber"
                                tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        }
                    }
                }
            }
        }
    }
}
