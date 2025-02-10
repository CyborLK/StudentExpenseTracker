package com.example.studentexpensetracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentexpensetracker.ui.screens.DashboardScreen
import com.example.studentexpensetracker.ui.screens.SignInScreen
import com.example.studentexpensetracker.viewmodel.SignInViewModel

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object Dashboard : Screen("dashboard")
}

@Composable
fun AppNavigation(signInViewModel: SignInViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SignIn.route) {
        composable(Screen.SignIn.route) {
            SignInScreen(viewModel = signInViewModel, navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = signInViewModel, navController)
        }
    }
}
