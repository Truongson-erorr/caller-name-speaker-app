package com.example.callernamespeaker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.BuildConfig
import com.example.callernamespeaker.model.Message
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        listOf(Message("Bạn cần hỗ trợ hay tư vấn gì về BlockSon?", isUser = false))
    )
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val geminiClient = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY_GEMINI
    )

    fun sendMessage(userMsg: String) {
        if (userMsg.isBlank() || _isLoading.value) return

        _messages.value = _messages.value + Message(userMsg, isUser = true)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Sending: $userMsg")
                val response = geminiClient.generateContent(userMsg)
                val botReply = response.text ?: "Xin lỗi, mình chưa có câu trả lời."
                Log.d("ChatViewModel", "Response: $botReply")

                _messages.value = _messages.value + Message(botReply, isUser = false)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Gemini API Error", e)
                _messages.value = _messages.value + Message("Lỗi: ${e.message}", isUser = false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}