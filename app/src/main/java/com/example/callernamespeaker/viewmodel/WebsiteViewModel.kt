import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.Website
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WebsiteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _websites = MutableStateFlow<List<Website>>(emptyList())
    val websites: StateFlow<List<Website>> = _websites

    init {
        loadWebsites()
    }

    fun loadWebsites() {
        viewModelScope.launch {
            db.collection("Websites")
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.map { doc ->
                        Website(
                            id = doc.id,
                            url = doc.getString("url") ?: "",
                            category = doc.getString("category") ?: "",
                            description = doc.getString("description") ?: "",
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        )
                    }
                    _websites.value = list
                }
                .addOnFailureListener {
                    _websites.value = emptyList()
                }
        }
    }

    fun addWebsite(website: Website, onDone: () -> Unit = {}) {
        val data = mapOf(
            "url" to website.url,
            "category" to website.category,
            "description" to website.description,
            "createdAt" to website.createdAt
        )
        db.collection("Websites")
            .add(data)
            .addOnSuccessListener { onDone() }
    }

    fun deleteWebsite(id: String, onDone: () -> Unit = {}) {
        db.collection("Websites").document(id)
            .delete()
            .addOnSuccessListener { onDone() }
    }
}
