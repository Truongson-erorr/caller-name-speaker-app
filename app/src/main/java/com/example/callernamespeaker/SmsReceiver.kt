package com.example.callernamespeaker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.telephony.SmsMessage
import android.widget.Toast
import java.util.Locale
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var pendingText: String? = null

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle = intent.extras ?: return
        val pdus = bundle["pdus"] as Array<*>? ?: return
        val format = bundle.getString("format")

        for (pdu in pdus) {

            val sms = SmsMessage.createFromPdu(pdu as ByteArray, format)
            val phoneNumber = sms.originatingAddress ?: "Không xác định"
            val message = sms.messageBody ?: ""

            val pattern = Pattern.compile("(http|https)://[\\w\\-\\.\\?&=/]+")
            val matcher = pattern.matcher(message)

            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("sms_tts_enabled", true)

            if (!isEnabled) return
            if (matcher.find()) {

                val foundLink = matcher.group()

                Toast.makeText(
                    context,
                    "Tin nhắn chứa link: $foundLink",
                    Toast.LENGTH_LONG
                ).show()

                pendingText =
                    "Cảnh báo. Bạn nhận được tin nhắn có đường link từ số $phoneNumber"

            }

            else {
                pendingText =
                    "Bạn nhận được tin nhắn từ $phoneNumber. Nội dung: $message"
            }

            if (tts == null) {
                tts = TextToSpeech(context.applicationContext, this)
            } else {
                speakNow()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("vi", "VN")
            speakNow()
        }
    }

    private fun speakNow() {
        pendingText?.let {
            tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "SMS_TTS")
        }
    }
}