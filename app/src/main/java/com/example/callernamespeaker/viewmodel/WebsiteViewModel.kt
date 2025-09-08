package com.example.callernamespeaker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WebsiteViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result

    private val geminiClient = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.API_KEY_GEMINI
    )

    fun checkWebsiteSafety(url: String) {
        if (url.isBlank() || _isLoading.value) return

        _isLoading.value = true
        _result.value = null

        viewModelScope.launch {
            try {
                Log.d("WebsiteViewModel", "Checking: $url")

                val prompt = """
                    Phân tích độ an toàn của website sau: $url
                    - Website có khả năng là an toàn hay nguy hiểm?
                    - Nếu nguy hiểm, hãy giải thích lý do (ví dụ: lừa đảo, giả mạo, chứa phần mềm độc hại...).
                    - Nếu an toàn, xác nhận ngắn gọn.
                """.trimIndent()

                val response = geminiClient.generateContent(prompt)
                val analysis = response.text ?: "Không thể phân tích website này."

                _result.value = analysis
                Log.d("WebsiteViewModel", "Result: $analysis")
            } catch (e: Exception) {
                Log.e("WebsiteViewModel", "Gemini API Error", e)
                _result.value = "Lỗi khi phân tích: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
