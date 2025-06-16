package com.example.callernamespeaker

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class CallTTSService : Service() {

    private var tts: TextToSpeech? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "tts_channel"
        val channelName = "Caller TTS"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Caller Name Speaker")
            .setContentText("Đang đọc tên người gọi...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(1, notification)
        }

        val callerName = intent?.getStringExtra("text_to_speak") ?: return START_NOT_STICKY

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Cuộc gọi đến từ: $callerName", Toast.LENGTH_LONG).show()
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    tts?.speak("Cuộc gọi đến từ $callerName", TextToSpeech.QUEUE_FLUSH, null, null)
                }, 1000)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
