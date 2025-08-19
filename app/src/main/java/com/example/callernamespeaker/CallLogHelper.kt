package com.example.callernamespeaker

import android.content.Context
import android.provider.CallLog
import java.text.SimpleDateFormat
import java.util.*

data class CallEntry(
    val number: String,
    val type: String,
    val date: String,
    val duration: String
)

object CallLogHelper {
    fun getCallHistory(context: Context): List<CallEntry> {
        val callList = mutableListOf<CallEntry>()
        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = when (it.getInt(typeIndex)) {
                    CallLog.Calls.INCOMING_TYPE -> "Gọi đến"
                    CallLog.Calls.OUTGOING_TYPE -> "Gọi đi"
                    CallLog.Calls.MISSED_TYPE -> "Bị nhỡ"
                    else -> "Khác"
                }
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(it.getLong(dateIndex)))
                val duration = "${it.getString(durationIndex)} giây"
                callList.add(CallEntry(number, type, date, duration))
            }
        }
        return callList
    }
}
