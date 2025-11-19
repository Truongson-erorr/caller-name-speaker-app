package com.example.callernamespeaker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IdentityViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _result = MutableStateFlow<String?>(null)
    val result = _result.asStateFlow()

    private val _riskLevel = MutableStateFlow("Thấp")
    val riskLevel = _riskLevel.asStateFlow()

    fun analyzeIdentity(sms: String) {
        if (sms.isBlank()) {
            _result.value = "Vui lòng nhập nội dung tin nhắn để hệ thống phân tích."
            _riskLevel.value = "Thấp"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            delay(1200)

            val lower = sms.lowercase()

            val highRiskKeywords = listOf(
                "otp", "ngân hàng", "chuyển khoản", "xác minh tài khoản",
                "mã bảo mật", "trúng thưởng", "nhận quà", "click vào", "bấm vào liên kết",
                "http://", "https://", "link", "cập nhật thông tin", "tài khoản bị khóa"
            )

            val mediumRiskKeywords = listOf(
                "ưu đãi", "khuyến mãi", "giảm giá", "quảng cáo", "thông báo", "dịch vụ"
            )

            val hasLink = lower.contains("http://") || lower.contains("https://") || lower.contains("bit.ly")
            val hasMoneyRequest = lower.contains("chuyển tiền") || lower.contains("nạp tiền") || lower.contains("phí dịch vụ")

            when {
                highRiskKeywords.any { lower.contains(it) } || hasLink || hasMoneyRequest -> {
                    _riskLevel.value = "Cao"
                    _result.value =
                        "Tin nhắn có dấu hiệu **lừa đảo hoặc giả mạo**. Hệ thống phát hiện từ khóa hoặc đường dẫn nghi ngờ. " +
                                "Không cung cấp mã OTP, thông tin tài khoản hoặc nhấp vào liên kết trong tin nhắn này."
                }

                mediumRiskKeywords.any { lower.contains(it) } -> {
                    _riskLevel.value = "Trung bình"
                    _result.value =
                        "Tin nhắn có nội dung quảng cáo hoặc tiếp thị. Dù không có dấu hiệu lừa đảo rõ ràng, " +
                                "bạn nên kiểm tra nguồn gửi trước khi tương tác."
                }

                else -> {
                    _riskLevel.value = "Thấp"
                    _result.value =
                        "Tin nhắn có vẻ an toàn. Không phát hiện từ khóa hoặc dấu hiệu bất thường."
                }
            }
            _isLoading.value = false
        }
    }
}
