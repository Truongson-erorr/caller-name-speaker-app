package com.example.callernamespeaker.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.callernamespeaker.ui.theme.RegisterScreen
import com.example.callernamespeaker.ui.theme.LoginScreen
import com.example.callernamespeaker.MainScreen
import com.example.callernamespeaker.model.NewsPost
import com.example.callernamespeaker.ui.theme.AllNewsScreen
import com.example.callernamespeaker.ui.theme.NewsDetailScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "LoginScreen",
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
    ) {
        composable("LoginScreen") {
            LoginScreen(navController)
        }

        composable("RegisterScreen") {
            RegisterScreen(navController)
        }

        composable("main") {
            MainScreen(navController)
        }

        composable(
            route = "news_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            NewsDetailScreen(postId = id)
        }
        composable("all_news") {
            AllNewsScreen(navController)
        }

    }
}
