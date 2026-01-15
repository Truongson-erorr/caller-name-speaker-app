package com.example.callernamespeaker.ui.theme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BannerCarousel() {
    val images = listOf(
        "https://ss-images.saostar.vn/2019/03/05/4711548/f3.jpg",
        "https://image.plo.vn/1200x630/Uploaded/2026/qjfsm/2024_04_06/dai-hoc-thu-dau-mot-1-8046.jpg",
        "https://res.cloudinary.com/dq64aidpx/image/upload/v1750685133/hydr9m9vdyzqioxsbvw4.jpg",
        "https://cdnphoto.dantri.com.vn/yIxJPfv4bXBVrvY6qzJueUXqdzA=/thumb_w/1020/2024/03/20/vv11-1710893833500.jpg",
        "https://ss-images.saostar.vn/2019/03/05/4711548/f7.jpg"
    )
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                ) with slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                )
            },
            label = "bannerSlide"
        ) { index ->
            Image(
                painter = rememberAsyncImagePainter(images[index]),
                contentDescription = "Banner",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        IconButton(
            onClick = {
                currentIndex = if (currentIndex - 1 < 0) images.lastIndex else currentIndex - 1
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                currentIndex = (currentIndex + 1) % images.size
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
