package com.example.studentexpensetracker.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.studentexpensetracker.viewmodel.SignInViewModel
import com.example.studentexpensetracker.ui.AppNavigation
import com.example.studentexpensetracker.ui.Screen

@Composable
fun SignInScreen(viewModel: SignInViewModel = viewModel(), navController: NavController) {
    val authState by viewModel.authState.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        viewModel.handleSignInResult(result.data)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = authState) {
            is SignInViewModel.AuthState.Idle -> {
                Button(
                    onClick = {
                        viewModel.signIn { intentSender ->
                            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                            signInLauncher.launch(intentSenderRequest)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Sign in with Google")
                }
            }
            is SignInViewModel.AuthState.Loading -> {
                CircularProgressIndicator()
            }
            is SignInViewModel.AuthState.Success -> {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            }
            is SignInViewModel.AuthState.Error -> {
                Text("Error: ${state.message}")
                Button(
                    onClick = {
                        viewModel.signIn { intentSender ->
                            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                            signInLauncher.launch(intentSenderRequest)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Retry Sign in with Google")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {

}