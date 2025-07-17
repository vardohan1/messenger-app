package com.vdavitashviliekvitsiani.messenger_app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdavitashviliekvitsiani.messenger_app.MainActivity
import com.vdavitashviliekvitsiani.messenger_app.service.AuthService
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme

class AuthActivity : ComponentActivity() {

    private val authService = AuthService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavigation()
                }
            }
        }
    }

    @Composable
    private fun AuthNavigation() {
        val navController = rememberNavController()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        NavHost(
            navController = navController,
            startDestination = "sign_in"
        ) {
            composable("sign_in") {
                SignInScreen(
                    onSignInClick = { nickname, password ->
                        isLoading = true
                        errorMessage = null

                        authService.signIn(nickname, password) { success, error ->
                            isLoading = false
                            if (success) {
                                startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                                finish()
                            } else {
                                errorMessage = error
                            }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate("sign_up")
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }

            composable("sign_up") {
                SignUpScreen(
                    onSignUpClick = { nickname, password, profession ->
                        isLoading = true
                        errorMessage = null

                        authService.signUp(nickname, profession, password) { success, error ->
                            isLoading = false
                            if (success) {
                                startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                                finish()
                            } else {
                                errorMessage = error
                            }
                        }
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}