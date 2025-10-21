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

    fun analyzeIdentity(input: String) {
        if (input.isBlank()) {
            _result.value = "Vui lòng nhập số điện thoại hoặc nội dung để phân tích."
            _riskLevel.value = "Thấp"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            delay(1200)

            when {
                input.contains("OTP", true) || input.contains("chuyển khoản", true)
                        || input.contains("ngân hàng", true) || input.contains("trúng thưởng", true) -> {
                    _riskLevel.value = "Cao"
                    _result.value = "Hệ thống phát hiện nội dung có dấu hiệu lừa đảo hoặc giả mạo tổ chức tài chính. Vui lòng cảnh giác và không cung cấp thông tin cá nhân."
                }
                input.length in 10..11 && input.all { it.isDigit() } -> {
                    _riskLevel.value = "Thấp"
                    _result.value = "Đây có vẻ là số cá nhân. Không phát hiện dấu hiệu đáng ngờ."
                }
                else -> {
                    _riskLevel.value = "Trung bình"
                    _result.value = "Nội dung có thể thuộc về doanh nghiệp hoặc tổng đài. Chưa phát hiện rủi ro rõ ràng."
                }
            }

            _isLoading.value = false
        }
    }
}
