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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vdavitashviliekvitsiani.messenger_app.ui.main.HomeScreen
import com.vdavitashviliekvitsiani.messenger_app.ui.main.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vdavitashviliekvitsiani.messenger_app.service.AuthService
import com.vdavitashviliekvitsiani.messenger_app.ui.auth.AuthActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.chat.ChatActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.profile.ProfileActivity
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme
import com.vdavitashviliekvitsiani.messenger_app.ui.search.SearchUsersActivity

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
                val viewModel: MainViewModel = viewModel()
                val conversations by viewModel.filteredConversations.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                HomeScreen(
                    conversations = conversations,
                    searchQuery = searchQuery,
                    onConversationClick = { conversation ->
                        val currentUserId = AuthService.getInstance().getCurrentUser()?.uid ?: ""
                        val otherUserId = conversation.participants.find { it != currentUserId }

                        val intent = Intent(this@MainActivity, ChatActivity::class.java).apply {
                            putExtra("otherUserId", otherUserId)
                            putExtra("otherUserNickname", conversation.otherUserNickname)
                            putExtra("otherUserProfileUrl", conversation.otherUserProfileUrl)
                            putExtra("conversationId", conversation.id)
                            putExtra("otherUserWork", conversation.otherUserProfession)
                        }
                        startActivity(intent)
                    },
                    onAddConversationClick = {
                        val intent = Intent(this@MainActivity, SearchUsersActivity::class.java)
                        startActivity(intent)
                    },
                    onProfileClick = {
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        startActivity(intent)
                    },
                    onSearchUsersClick = {
                        val intent = Intent(this@MainActivity, SearchUsersActivity::class.java)
                        startActivity(intent)
                    },
                    isLoading = isLoading
                )
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
    }
}