package com.example.callernamespeaker.ui.theme

import NewsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.callernamespeaker.model.NewsPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllNewsScreen(
    navController: NavController,
    viewModel: NewsViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    var sortDescending by remember { mutableStateOf(true) }

    val newsList by viewModel.newsPosts.collectAsState()
    val filteredNews = if (sortDescending)
        newsList.sortedByDescending { it.date }
    else
        newsList.sortedBy { it.date }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tin tức trong ngày",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(start = 8.dp)
                    )
                },
                actions = {
                    var expandedMenu by remember { mutableStateOf(false) }

                    Box {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Lọc",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { expandedMenu = true }
                        )

                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mới nhất") },
                                onClick = {
                                    sortDescending = true
                                    expandedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cũ nhất") },
                                onClick = {
                                    sortDescending = false
                                    expandedMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredNews) { post ->
                NewsCardItem2(post) {
                    navController.navigate("news_detail/${post.id}")
                }
            }
        }
    }
}

@Composable
fun NewsCardItem2(
    post: NewsPost,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(post.imageUrl),
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
