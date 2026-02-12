package com.example.callernamespeaker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class WebsiteViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result

    private val apiKey = BuildConfig.API_KEY_GEMINI
    private val geminiClient = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    private val client = OkHttpClient()

    fun checkWebsiteSafety(url: String) {
        if (url.isBlank() || _isLoading.value) return

        _isLoading.value = true
        _result.value = null

        viewModelScope.launch {
            try {

                val formattedUrl = if (!url.startsWith("http")) {
                    "http://$url"
                } else url

                // Check HTTPS
                val isHttps = formattedUrl.startsWith("https://")

                // Check domain dạng IP
                val containsIp = Regex("""\b\d{1,3}(\.\d{1,3}){3}\b""")
                    .containsMatchIn(formattedUrl)

                // Google Safe Browsing
                val googleFlagged = checkGoogleSafeBrowsing(formattedUrl)

                // Gửi sang Gemini phân tích tổng hợp
                val prompt = """
                    URL: $formattedUrl
                    
                    Phân tích kỹ thuật:
                    - HTTPS: $isHttps
                    - Domain là IP: $containsIp
                    - Google Safe Browsing cảnh báo: $googleFlagged
                    
                    Hãy:
                    - Đánh giá mức độ an toàn (An toàn / Đáng nghi / Nguy hiểm)
                    - Giải thích ngắn gọn lý do
                    - Trình bày rõ ràng, dễ hiểu cho người dùng phổ thông
                """.trimIndent()

                val response = geminiClient.generateContent(prompt)
                val analysis = response.text ?: "Không thể phân tích."

                _result.value = analysis

            } catch (e: Exception) {
                Log.e("WebsiteViewModel", "Error", e)
                _result.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun checkGoogleSafeBrowsing(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject().apply {
                    put("client", JSONObject().apply {
                        put("clientId", "your-app")
                        put("clientVersion", "1.0")
                    })
                    put("threatInfo", JSONObject().apply {
                        put("threatTypes", JSONArray()
                            .put("MALWARE")
                            .put("SOCIAL_ENGINEERING")
                            .put("UNWANTED_SOFTWARE"))
                        put("platformTypes", JSONArray().put("ANY_PLATFORM"))
                        put("threatEntryTypes", JSONArray().put("URL"))
                        put("threatEntries", JSONArray().put(
                            JSONObject().put("url", url)
                        ))
                    })
                }

                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://safebrowsing.googleapis.com/v4/threatMatches:find?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()

                return@withContext body?.contains("matches") == true

            } catch (e: Exception) {
                Log.e("SafeBrowsing", "API Error", e)
                false
            }
        }
    }
}
