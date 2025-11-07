package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.model.NewsPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NewsDetailScreen(postId: String, navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    var post by remember { mutableStateOf<NewsPost?>(null) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var currentUserName by remember { mutableStateOf("Ẩn danh") }

    LaunchedEffect(uid) {
        uid?.let {
            firestore.collection("Users").document(it)
                .get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name")
                    if (!name.isNullOrBlank()) {
                        currentUserName = name
                    }
                }
        }
    }

    LaunchedEffect(postId) {
        firestore.collection("Posts").document(postId)
            .get()
            .addOnSuccessListener { doc ->
                val timestamp = doc.getTimestamp("date")
                val dateStr = timestamp?.toDate()?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                } ?: ""
                post = NewsPost(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    shortDescription = doc.getString("shortDescription") ?: "",
                    description = doc.getString("description") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    date = dateStr
                )
            }
    }

    if (post == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        .padding(start = 8.dp)
                        .size(26.dp)
                )
                Text(
                    text = "Chi tiết bài viết",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = post!!.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = post!!.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Image(
                    painter = rememberAsyncImagePainter(post!!.imageUrl),
                    contentDescription = post!!.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = post!!.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                CommentSection(postId = post!!.id, userName = currentUserName)
            }
        }
    }
}
