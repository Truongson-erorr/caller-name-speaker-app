package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.callernamespeaker.model.Comment
import com.example.callernamespeaker.model.Reply
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
    val replies by viewModel.replies.collectAsState()
    var newComment by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Comment?>(null) }

    LaunchedEffect(postId) {
        viewModel.fetchComments(postId)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            "Bình luận (${comments.size})",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 350.dp)
        ) {
            items(comments) { comment ->
                LaunchedEffect(comment.id) {
                    viewModel.fetchReplies(postId, comment.id)
                }
                CommentItem(comment, replies[comment.id].orEmpty()) {
                    replyTo = comment
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (replyTo != null) {
            Text(
                text = "Trả lời ${replyTo!!.userName}",
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        TextField(
            value = newComment,
            onValueChange = { newComment = it },
            shape = RoundedCornerShape(30.dp),
            placeholder = {
                Text(
                    text = if (replyTo == null) "Thêm bình luận..." else "Trả lời bình luận...",
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF1A202C),
                cursorColor = Color(0xFF3B82F6),
                focusedIndicatorColor = Color(0xFF3B82F6),
                unfocusedIndicatorColor = Color(0xFF374151),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                sendCommentOrReply(viewModel, postId, userName, newComment, replyTo) {
                    newComment = ""
                    replyTo = null
                }
            }),
            trailingIcon = {
                IconButton(onClick = {
                    sendCommentOrReply(viewModel, postId, userName, newComment, replyTo) {
                        newComment = ""
                        replyTo = null
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi",
                        tint = if (newComment.isNotBlank()) Color(0xFF3B82F6) else Color.Gray
                    )
                }
            }
        )
    }
}

private fun sendCommentOrReply(
    viewModel: CommentViewModel,
    postId: String,
    userName: String,
    content: String,
    replyTo: Comment?,
    onDone: () -> Unit
) {
    if (content.isBlank()) return
    if (replyTo == null) {
        viewModel.addComment(postId, userName, content.trim())
    } else {
        viewModel.addReply(postId, replyTo.id, userName, content.trim())
    }
    onDone()
}

@Composable
fun CommentItem(
    comment: Comment,
    replies: List<Reply> = emptyList(),
    onReplyClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF111827), RoundedCornerShape(12.dp))
            .padding(8.dp)
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
                Text(comment.userName, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(comment.content, color = Color(0xFFE5E7EB))
                Text(
                    text = formatTimeAgo(comment.timestamp),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
                TextButton(onClick = onReplyClick) {
                    Icon(
                        Icons.Default.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF3B82F6)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Phản hồi", fontSize = 12.sp, color = Color(0xFF3B82F6))
                }
            }
        }

        if (replies.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 48.dp)) {
                replies.forEach { reply ->
                    ReplyItem(reply)
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: Reply) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(reply.userName, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.White)
            Text(reply.content, fontSize = 13.sp, color = Color(0xFFE5E7EB))
            Text(formatTimeAgo(reply.timestamp), fontSize = 10.sp, color = Color(0xFF9CA3AF))
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        else -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
