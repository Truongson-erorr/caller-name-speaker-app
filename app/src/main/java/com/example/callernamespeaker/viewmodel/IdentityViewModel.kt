package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.callernamespeaker.BuildConfig

class IdentityViewModel : ViewModel() {

    private val geminiClient = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.API_KEY_GEMINI
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _result = MutableStateFlow<String?>(null)
    val result = _result.asStateFlow()

    private val _riskLevel = MutableStateFlow("Thấp")
    val riskLevel = _riskLevel.asStateFlow()

    fun analyzeIdentity(sms: String) {
        if (sms.isBlank()) {
            _result.value = "Vui lòng nhập nội dung tin nhắn để phân tích."
            _riskLevel.value = "Thấp"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val ruleResult = analyzeByRule(sms)

            _riskLevel.value = ruleResult.riskLevel
            _result.value = ruleResult.message

            if (ruleResult.shouldCallGemini) {
                analyzeByGemini(sms)
            }

            _isLoading.value = false
        }
    }

    private fun analyzeByRule(sms: String): RuleResult {
        val text = sms.lowercase()
        var score = 0
        val reasons = mutableListOf<String>()

        val hasLink = text.contains("http://") ||
                text.contains("https://") ||
                text.contains("bit.ly") ||
                text.contains("tinyurl")

        if (hasLink) {
            score += 3
            reasons.add("Có chứa đường link lạ")
        }

        val otpKeywords = listOf("otp", "mã xác minh", "mã bảo mật")
        if (otpKeywords.any { text.contains(it) }) {
            score += 4
            reasons.add("Yêu cầu mã OTP")
        }

        val bankKeywords = listOf("ngân hàng", "tài khoản", "ví điện tử")
        if (bankKeywords.any { text.contains(it) }) {
            score += 2
            reasons.add("Giả mạo ngân hàng / tài khoản")
        }

        val moneyKeywords = listOf("chuyển tiền", "nạp tiền", "phí dịch vụ")
        if (moneyKeywords.any { text.contains(it) }) {
            score += 4
            reasons.add("Yêu cầu chuyển tiền")
        }

        val threatKeywords = listOf("bị khóa", "xác minh gấp", "trong 24 giờ")
        if (threatKeywords.any { text.contains(it) }) {
            score += 3
            reasons.add("Nội dung đe dọa / khẩn cấp")
        }

        return when {
            score >= 7 -> RuleResult(
                riskLevel = "Cao",
                shouldCallGemini = true,
                message = buildMessage(
                    "⚠️ Tin nhắn có dấu hiệu lừa đảo NGUY HIỂM.",
                    reasons
                )
            )

            score >= 4 -> RuleResult(
                riskLevel = "Trung bình",
                shouldCallGemini = true,
                message = buildMessage(
                    "⚠️ Tin nhắn có dấu hiệu đáng nghi.",
                    reasons
                )
            )

            else -> RuleResult(
                riskLevel = "Thấp",
                shouldCallGemini = false,
                message = "Tin nhắn có vẻ an toàn. Không phát hiện dấu hiệu lừa đảo rõ ràng."
            )
        }
    }

    private fun buildMessage(title: String, reasons: List<String>): String {
        return buildString {
            appendLine(title)
            appendLine()
            reasons.forEach {
                appendLine("• $it")
            }
            appendLine()
            append("⚠️ Không nhấp link và không cung cấp thông tin cá nhân.")
        }
    }

    private suspend fun analyzeByGemini(sms: String) {
        try {
            val prompt = """
                Hãy phân tích tin nhắn SMS sau cho người lớn tuổi:
                
                "$sms"

                Yêu cầu:
                1. Tin nhắn có phải lừa đảo không?
                2. Thuộc kịch bản nào? (giả ngân hàng, trúng thưởng, đe dọa pháp lý, link độc hại…)
                3. Mức độ rủi ro: Thấp / Trung bình / Cao
                4. Giải thích NGẮN GỌN, DỄ HIỂU
            """.trimIndent()

            val response = geminiClient.generateContent(prompt)
            val aiText = response.text ?: return

            _result.value =
                (_result.value ?: "") +
                        "\n\n🤖 Phân tích AI:\n$aiText"

        } catch (e: Exception) {
            _result.value =
                (_result.value ?: "") +
                        "\n\n⚠️ Không thể phân tích AI lúc này."
        }
    }
}

private data class RuleResult(
    val riskLevel: String,
    val shouldCallGemini: Boolean,
    val message: String
)
