package com.example.callernamespeaker.ui.theme.Posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.Comment
import com.example.callernamespeaker.viewmodel.CommentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(
    postId: String,
    userName: String,
    viewModel: CommentViewModel = viewModel()
) {
    val comments by viewModel.comments.collectAsState()
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.fetchComments(postId)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Bình luận (${comments.size})",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))

        comments.forEach { comment ->
            CommentItem(comment)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(12.dp))

        TextField(
            value = newComment,
            onValueChange = { newComment = it },
            placeholder = {
                Text(
                    text = "Thêm bình luận...",
                    color = Color(0xFF9CA3AF)
                )
            },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF1A202C),
                cursorColor = Color(0xFF3B82F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                sendComment(viewModel, postId, userName, newComment) {
                    newComment = ""
                }
            }),
            trailingIcon = {
                IconButton(
                    onClick = {
                        sendComment(viewModel, postId, userName, newComment) {
                            newComment = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi",
                        tint = if (newComment.isNotBlank())
                            Color(0xFF3B82F6) else Color.Gray
                    )
                }
            }
        )
    }
}

private fun sendComment(
    viewModel: CommentViewModel,
    postId: String,
    userName: String,
    content: String,
    onDone: () -> Unit
) {
    if (content.isBlank()) return
    viewModel.addComment(postId, userName, content.trim())
    onDone()
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF111827), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = comment.userName,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    text = comment.content,
                    color = Color(0xFFE5E7EB),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    text = formatTimeAgo(comment.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        else -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }
}
