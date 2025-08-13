import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callernamespeaker.model.NewsPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class NewsViewModel : ViewModel() {
    private val _newsPosts = MutableStateFlow<List<NewsPost>>(emptyList())
    val newsPosts: StateFlow<List<NewsPost>> = _newsPosts

    private val db = FirebaseFirestore.getInstance()

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    init {
        fetchNewsFromFirestore()
    }

    private fun fetchNewsFromFirestore() {
        viewModelScope.launch {
            db.collection("Posts")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val posts = result.map { doc ->
                        val timestamp = doc.getTimestamp("date")
                        val dateStr = timestamp?.toDate()?.let { dateFormat.format(it) } ?: ""

                        NewsPost(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            shortDescription = doc.getString("shortDescription") ?: "",
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            date = dateStr
                        )
                    }
                    _newsPosts.value = posts
                }
                .addOnFailureListener {
                    _newsPosts.value = emptyList()
                }
        }
    }
}
