package com.example.studentexpensetracker.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studentexpensetracker.ui.Screen
import com.example.studentexpensetracker.ui.AppNavigation
import com.example.studentexpensetracker.ui.theme.StudentExpenseTrackerTheme
import com.example.studentexpensetracker.viewmodel.SignInViewModel

@Composable
fun DashboardScreen(viewModel: SignInViewModel, navController: NavController) {
    val user = viewModel.currentUser // Access the user via the public property

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${user?.displayName ?: user?.email ?: "User"}!")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.logout()
            navController.navigate(Screen.SignIn.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StudentExpenseTrackerTheme {
    }
}
