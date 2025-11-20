package com.example.callernamespeaker.ui.theme

import NewsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.model.NewsPost

@Composable
fun NewsSection(navController: NavController) {
    val viewModel: NewsViewModel = viewModel()
    val newsList by viewModel.newsPosts.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tin tức, cảnh báo",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Xem tất cả",
                color = Color(0xFF4A9CFF),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    navController.navigate("all_news")
                }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
        ) {
            items(newsList) { post ->
                NewsCardItem(post) {
                    navController.navigate("news_detail/${post.id}")
                }
            }
        }
    }
}

@Composable
fun NewsCardItem(post: NewsPost, onClick: () -> Unit = {}) {

    val cardColor = Color(0xFF111829)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFB7C8FF)

    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    color = textPrimary,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = post.shortDescription,
                    maxLines = 2,
                    color = textSecondary,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = post.date,
                    color = Color(0xFF7A8DA6),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

