package com.example.callernamespeaker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

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
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Caller Name Speaker")
            .setContentText("Đang đọc thông tin cuộc gọi...")
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

        val phoneNumber =
            intent?.getStringExtra("phone_number") ?: return START_NOT_STICKY

        val displayName =
            intent.getStringExtra("display_name") ?: phoneNumber

        val isInContacts =
            intent.getBooleanExtra("is_in_contacts", false)

        val prefs = getSharedPreferences("call_tts_prefs", Context.MODE_PRIVATE)

        var callCount = 1

        if (!isInContacts) {

            sendCallAlert(phoneNumber)

            val keyCount = "call_count_$phoneNumber"
            val keyTime = "last_call_time_$phoneNumber"

            val now = System.currentTimeMillis()
            val lastTime = prefs.getLong(keyTime, 0L)

            callCount =
                if (now - lastTime > 24 * 60 * 60 * 1000) {
                    1
                } else {
                    prefs.getInt(keyCount, 0) + 1
                }

            prefs.edit()
                .putInt(keyCount, callCount)
                .putLong(keyTime, now)
                .apply()
        }

        val speakText =
            if (isInContacts) {
                "Cuộc gọi đến từ $displayName"
            } else {
                when (callCount) {
                    1 -> "Cuộc gọi đến từ số lạ $displayName"
                    2 -> "Số lạ $displayName gọi lần thứ hai"
                    else -> "Số lạ $displayName gọi nhiều lần, hãy cẩn thận"
                }
            }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("vi", "VN")

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        if (isInContacts) speakText else "[$callCount lần] $speakText",
                        Toast.LENGTH_LONG
                    ).show()
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    tts?.speak(
                        speakText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "CALL_TTS"
                    )
                }, 800)
            }
        }

        return START_NOT_STICKY
    }

    private fun sendCallAlert(number: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().uid ?: return

        db.collection("Users").document(currentUid).get()
            .addOnSuccessListener { userDoc ->
                val connections = userDoc.get("connections") as? List<String> ?: emptyList()

                val timestamp = System.currentTimeMillis()

                connections.forEach { uid ->
                    val data = hashMapOf(
                        "callerNumber" to number,
                        "receiverId" to uid,
                        "fromUid" to currentUid,
                        "time" to timestamp
                    )
                    db.collection("call_alerts").add(data)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}