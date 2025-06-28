package com.example.callernamespeaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.callernamespeaker.ui.theme.RegisterScreen
import com.example.callernamespeaker.ui.theme.LoginScreen
import com.example.callernamespeaker.MainScreen

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

    }
}
