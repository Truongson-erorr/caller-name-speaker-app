package com.example.callernamespeaker.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun NotificationScreen() {
    val imageUrl =
        "https://res.cloudinary.com/dq64aidpx/image/upload/v1754394786/istockphoto-1412282189-612x612_pjohig.jpg"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Thông báo mới",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        NotificationItem(
            imageUrl = imageUrl,
            title = "10 dấu hiệu bạn đang bị lừa đảo công nghệ!",
            time = "5 phút trước",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(8.dp))

        NotificationItem(
            imageUrl = imageUrl,
            title = "Cảnh báo bảo mật: Hãy đổi mật khẩu ngay!",
            time = "10 phút trước",
            onClick = {  }
        )
    }
}

@Composable
fun NotificationItem(
    imageUrl: String,
    title: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(57.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}
