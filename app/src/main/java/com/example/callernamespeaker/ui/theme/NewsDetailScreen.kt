// NewsDetailScreen.kt
package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.model.NewsPost
import com.example.callernamespeaker.viewmodel.CommentViewModel
import com.example.callernamespeaker.model.Comment
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
        val scrollState = rememberScrollState()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = post!!.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

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

@Composable
fun CommentSection(postId: String, userName: String, viewModel: CommentViewModel = viewModel()) {
    val comments by viewModel.comments.collectAsState()
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.fetchComments(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding( horizontal = 16.dp)
    ) {
        Text(
            text = "Bình luận (${comments.size})",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (comments.isEmpty()) {
            Text(
                text = "Chưa có bình luận nào. Hãy là người đầu tiên!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment = comment)
                    Divider(
                        color = Color.Gray.copy(alpha = 0.1f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newComment,
            onValueChange = { newComment = it },
            shape = RoundedCornerShape(26.dp),
            placeholder = { Text("Nhập bình luận...") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (newComment.isNotBlank()) {
                        viewModel.addComment(postId, userName, newComment.trim())
                        newComment = ""
                    }
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (newComment.isNotBlank()) {
                            viewModel.addComment(postId, userName, newComment.trim())
                            newComment = ""
                        }
                    },
                    enabled = newComment.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi bình luận",
                        tint = if (newComment.isNotBlank()) Color.Black else Color.Gray
                    )
                }
            }
        )
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User avatar",
            tint = Color.Gray,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD))
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = comment.userName,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}