package com.example.callernamespeaker.ui.theme

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.*

class SmsTtsService : Service(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var message: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        message = intent?.getStringExtra("text") ?: ""
        tts = TextToSpeech(this, this)

        return START_NOT_STICKY
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            tts?.language = Locale("vi", "VN")

            tts?.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "sms_tts"
            )
        }
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}