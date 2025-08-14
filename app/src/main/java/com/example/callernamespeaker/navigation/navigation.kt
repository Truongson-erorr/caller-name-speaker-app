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
import com.example.callernamespeaker.CallEntry
import com.example.callernamespeaker.ui.theme.RegisterScreen
import com.example.callernamespeaker.ui.theme.LoginScreen
import com.example.callernamespeaker.MainScreen
import com.example.callernamespeaker.model.NewsPost
import com.example.callernamespeaker.ui.theme.AllNewsScreen
import com.example.callernamespeaker.ui.theme.CallDetailScreen
import com.example.callernamespeaker.ui.theme.CallHistoryScreen
import com.example.callernamespeaker.ui.theme.ChatScreen
import com.example.callernamespeaker.ui.theme.EmergencyTab
import com.example.callernamespeaker.ui.theme.ForgotPasswordScreen
import com.example.callernamespeaker.ui.theme.NewsDetailScreen
import com.example.callernamespeaker.ui.theme.OtpVerificationScreen
import com.example.callernamespeaker.ui.theme.UserInfoScreen
import com.example.callernamespeaker.ui.theme.WebsiteScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    callList: List<CallEntry>
) {
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
        composable("EmergencyTab") {
            EmergencyTab(navController)
        }
        composable("UserInfoScreen") {
        UserInfoScreen(navController)
    }
        composable(
            "otp_verification/{verificationId}/{phoneNumber}",
            arguments = listOf(
                navArgument("verificationId") { type = NavType.StringType },
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val verificationId = backStackEntry.arguments?.getString("verificationId")!!
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")!!
            OtpVerificationScreen(navController, verificationId, phoneNumber)
        }

        composable("main") {
            MainScreen(navController)
        }
        composable("WebsiteScreen") {
            WebsiteScreen(navController)
        }
        composable(
            route = "news_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            NewsDetailScreen(postId = id, navController)
        }
        composable("all_news") {
            AllNewsScreen(navController)
        }
        // chi tiet cuoc goi
        composable(
            route = "call_detail/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val call = callList.getOrNull(index)
            if (call != null) {
                CallDetailScreen(navController, call)
            }
        }
        composable("ForgotPasswordScreen") {
            ForgotPasswordScreen(navController)
        }
        composable("ChatScreen") {
            ChatScreen()
        }
    }
}
