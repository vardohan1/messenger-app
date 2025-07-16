package com.vdavitashviliekvitsiani.messenger_app.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.auth.AuthActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.main.MainViewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.search.SearchUsersActivity
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
        var isUpdating by remember { mutableStateOf(false) }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var isUploadingImage by remember { mutableStateOf(false) }

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                isUploadingImage = true
                viewModel.updateUserProfileImage() { success ->
                    isUploadingImage = false
                }
            }
        }

        ProfileScreen(
            user = currentUser,
            selectedImageUri = selectedImageUri,
            onProfileImageClick = {
                photoPickerLauncher.launch("image/*")
            },
            onSignOutClick = {
                isSigningOut = true
                viewModel.signOut()

                val intent = Intent(this@ProfileActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            onEditProfileClick = { nickname, profession ->
                isUpdating = true
                viewModel.updateUserProfile(nickname, profession) { success, error ->
                    isUpdating = false
                }
            },
            onFabClick = {
                val intent = Intent(this@ProfileActivity, SearchUsersActivity::class.java)
                startActivity(intent)
            },
            onBackToHomeClick = {
                finish()
            },
            isLoading = isSigningOut,
            isUpdating = isUpdating || isUploadingImage
        )
    }
}