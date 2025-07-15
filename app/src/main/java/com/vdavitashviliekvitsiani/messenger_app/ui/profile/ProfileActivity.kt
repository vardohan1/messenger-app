package com.vdavitashviliekvitsiani.messenger_app.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.auth.AuthActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.main.MainViewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProfileContent()
                }
            }
        }
    }

    @Composable
    private fun ProfileContent() {
        val viewModel: MainViewModel = viewModel()
        val currentUser by viewModel.currentUser.collectAsState()

        var isSigningOut by remember { mutableStateOf(false) }

        ProfileScreen(
            user = currentUser,
            onSignOutClick = {
                isSigningOut = true
                viewModel.signOut()

                val intent = Intent(this@ProfileActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            onEditProfileClick = {
                // TODO: Navigate to edit profile screen
            },
            onBackToHomeClick = {
                finish()
            },
            isLoading = isSigningOut
        )
    }
}