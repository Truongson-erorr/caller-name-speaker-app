package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SmsAnalysisViewModel : ViewModel() {

    private val _analysisResult = MutableStateFlow<String?>(null)
    val analysisResult: StateFlow<String?> = _analysisResult

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing

    private val geminiClient = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY_GEMINI
    )

    fun analyzeMessage(content: String) {
        if (content.isBlank() || _isAnalyzing.value) return

        _isAnalyzing.value = true
        _analysisResult.value = null

        viewModelScope.launch {
            try {
                val prompt = """
                    Bạn là hệ thống phát hiện tin nhắn lừa đảo.
                    Hãy phân tích nội dung sau và cho biết:
                    - Có dấu hiệu lừa đảo không?
                    - Mức độ nguy hiểm (Thấp / Trung bình / Cao)
                    - Giải thích ngắn gọn.

                    Nội dung:
                    $content
                """.trimIndent()

                val response = geminiClient.generateContent(prompt)
                _analysisResult.value =
                    response.text ?: "Không phân tích được nội dung."
            } catch (e: Exception) {
                _analysisResult.value = "Lỗi: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun clearResult() {
        _analysisResult.value = null
    }
}