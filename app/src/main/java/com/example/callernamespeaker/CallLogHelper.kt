package com.example.callernamespeaker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CallLog
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

data class CallEntry(
    val number: String,
    val type: String,
    val date: String,
    val duration: String
)

object CallLogHelper {

    fun getCallHistory(context: Context?): List<CallEntry> {
        val callList = mutableListOf<CallEntry>()

        if (context == null) return callList

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return callList
        }

        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )

            cursor?.use {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

                while (it.moveToNext()) {
                    val number = it.getString(numberIndex) ?: "Không rõ"
                    val type = when (it.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "Gọi đến"
                        CallLog.Calls.OUTGOING_TYPE -> "Gọi đi"
                        CallLog.Calls.MISSED_TYPE -> "Bị nhỡ"
                        else -> "Khác"
                    }
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(it.getLong(dateIndex)))
                    val duration = "${it.getString(durationIndex) ?: "0"} giây"

                    callList.add(CallEntry(number, type, date, duration))
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return callList
    }
}
