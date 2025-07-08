package com.example.callernamespeaker.navigation

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
import com.example.callernamespeaker.ui.theme.NewsDetailScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "LoginScreen") {

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



    }
}
