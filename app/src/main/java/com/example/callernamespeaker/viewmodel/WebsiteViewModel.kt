import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.Website
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class WebsiteViewModel : ViewModel() {
    var urlInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var result by mutableStateOf<String?>(null)
    var domain by mutableStateOf<String?>(null)

    private val client = OkHttpClient()

    fun checkWebsiteSafety(apiKey: String) {
        val input = urlInput.trim().lowercase()
        if (input.isBlank()) return

        isLoading = true
        result = null
        domain = input
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")
            .substringBefore("/")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = "Hãy phân tích link sau và cho biết nó có an toàn không: $input"

                val body = """
                    {
                      "contents":[{"parts":[{"text":"$prompt"}]}]
                    }
                """.trimIndent()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$apiKey")
                    .post(RequestBody.create("application/json".toMediaType(), body))
                    .build()

                client.newCall(request).execute().use { response ->
                    val resBody = response.body?.string()
                    val message = if (resBody != null && response.isSuccessful) {
                        "AI phân tích: $resBody"
                    } else {
                        "Không thể phân tích website"
                    }
                    withContext(Dispatchers.Main) {
                        result = message
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result = "Lỗi: ${e.message}"
                    isLoading = false
                }
            }
        }
    }
}
