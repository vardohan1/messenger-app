package com.vdavitashviliekvitsiani.messenger_app.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.main.MainViewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchUsersActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessengerappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchUsersContent()
                }
            }
        }
    }

    @Composable
    private fun SearchUsersContent() {
        val viewModel: MainViewModel = viewModel()
        val searchResults by viewModel.searchResults.collectAsState()
        val isSearching by viewModel.isSearching.collectAsState()

        var searchQuery by remember { mutableStateOf("") }
        var searchJob by remember { mutableStateOf<Job?>(null) }
        val coroutineScope = rememberCoroutineScope()

        SearchUsersScreen(
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query

                searchJob?.cancel()

                if (query.length >= 3) {
                    searchJob = coroutineScope.launch {
                        delay(500)
                        viewModel.searchUsers(query)
                    }
                } else {
                    viewModel.searchUsers("")
                }
            },
            searchResults = searchResults,
            onUserClick = { user ->
                Toast.makeText(
                    this@SearchUsersActivity,
                    "Clicked on ${user.nickname}",
                    Toast.LENGTH_SHORT
                ).show()

                // TODO: Navigate to chat when ChatActivity is implemented
                // val intent = Intent(this@SearchUsersActivity, ChatActivity::class.java)
                // intent.putExtra("otherUserId", user.uid)
                // intent.putExtra("otherUserNickname", user.nickname)
                // intent.putExtra("otherUserProfileUrl", user.profileImageUrl)
                // startActivity(intent)
            },
            onBackClick = {
                finish()
            },
            isLoading = isSearching
        )
    }
}