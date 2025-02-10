package com.example.studentexpensetracker.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val oneTapClient: SignInClient = Identity.getSignInClient(application.applicationContext)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: FirebaseUser) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("311057173070-nb93a8cijpla4asrbng6rjti56qprjc8.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    fun signIn(onSignInStarted: (IntentSender) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("SignInViewModel", "Starting sign-in process")
                val signInResult = beginSignIn()
                val intentSender = signInResult?.pendingIntent?.intentSender
                if (intentSender != null) {
                    Log.d("SignInViewModel", "Sign-in IntentSender obtained")
                    onSignInStarted(intentSender)
                } else {
                    Log.e("SignInViewModel", "Sign-in IntentSender is null")
                }
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Sign-in failed", e)
            }
        }
    }


    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    Log.d("SignInViewModel", "Google ID Token received: $idToken")
                    firebaseAuthWithGoogle(idToken) { success, message ->
                        if (success) {
                            Log.d("SignInViewModel", "Firebase Authentication Success!")
                        } else {
                            Log.e("SignInViewModel", "Firebase Authentication Failed: $message")
                        }
                    }
                } else {
                    Log.e("SignInViewModel", "Google ID Token is null")
                }
            } catch (e: ApiException) {
                Log.e("SignInViewModel", "Google Sign-in failed: ${e.statusCode}", e)
            }
        }
    }



    private suspend fun beginSignIn() = suspendCancellableCoroutine { continuation ->
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                continuation.resume(result)
            }
            .addOnFailureListener { exception ->
                Log.e("SignInViewModel", "One Tap sign-in failed", exception)
                continuation.resumeWithException(exception)
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        _authState.value = AuthState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        _authState.value = AuthState.Success(user)
                    }
                    onResult(true, null)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

}


