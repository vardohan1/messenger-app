package com.vdavitashviliekvitsiani.messenger_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vdavitashviliekvitsiani.messenger_app.service.AuthService
import com.vdavitashviliekvitsiani.messenger_app.ui.auth.AuthActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme

class MainActivity : ComponentActivity() {

    private val authService = AuthService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticationCheck()
                }
            }
        }
    }

    @Composable
    private fun AuthenticationCheck() {
        var isCheckingAuth by remember { mutableStateOf(true) }
        var isAuthenticated by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (authService.isUserLoggedIn()) {
                authService.getCurrentUserData { user ->
                    isCheckingAuth = false
                    if (user != null) {
                        isAuthenticated = true
                    } else {
                        authService.signOut()
                        isAuthenticated = false
                    }
                }
            } else {
                isCheckingAuth = false
                isAuthenticated = false
            }
        }

        when {
            isCheckingAuth -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            isAuthenticated -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Welcome to Messenger! (Main screens coming soon...)")
                }
            }

            else -> {
                LaunchedEffect(Unit) {
                    startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //recreate()
    }
}