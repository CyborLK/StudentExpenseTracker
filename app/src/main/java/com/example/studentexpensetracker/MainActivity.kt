package com.example.studentexpensetracker

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentexpensetracker.ui.AppNavigation
import com.example.studentexpensetracker.ui.screens.SignInScreen
import com.example.studentexpensetracker.ui.theme.StudentExpenseTrackerTheme
import com.example.studentexpensetracker.viewmodel.SignInViewModel
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase

        //val signInViewModel = SignInViewModel(application) // Create ViewModel manually

        enableEdgeToEdge()
        setContent {
            StudentExpenseTrackerTheme {
                val signInViewModel: SignInViewModel = viewModel()
                AppNavigation(signInViewModel) // This will handle navigation properly
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController() // Add this line

    StudentExpenseTrackerTheme {
        SignInScreen(viewModel = SignInViewModel(Application()), navController = navController)
    }
}

