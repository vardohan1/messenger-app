package com.vdavitashviliekvitsiani.messenger_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdavitashviliekvitsiani.messenger_app.model.Conversation
import com.vdavitashviliekvitsiani.messenger_app.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.vdavitashviliekvitsiani.messenger_app.model.User
import com.vdavitashviliekvitsiani.messenger_app.service.MessagingService

class MainViewModel : ViewModel() {
    private val messagingService = MessagingService.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private val authService = AuthService.getInstance()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _filteredConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val filteredConversations: StateFlow<List<Conversation>> = _filteredConversations.asStateFlow()

    init {
        loadConversations()
        loadCurrentUser()
    }

    private fun filterConversations() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isEmpty()) {
            _conversations.value
        } else {
            _conversations.value.filter { conversation ->
                conversation.otherUserNickname.lowercase().contains(query) ||
                        conversation.lastMessage.lowercase().contains(query)
            }
        }
        _filteredConversations.value = filtered
    }

    fun signOut() {
        authService.signOut()
    }

    fun loadCurrentUser() {
        authService.getCurrentUserData { user ->
            _currentUser.value = user
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true

            messagingService.getConversationsForUser { conversations ->
                _conversations.value = conversations
                filterConversations()
                _isLoading.value = false
            }
        }
    }

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        _isSearching.value = true
        messagingService.searchUsers(query) { users ->
            _searchResults.value = users
            _isSearching.value = false
        }
    }

    fun updateUserProfile(nickname: String, profession: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            authService.updateUserProfile(nickname, profession) { success, error ->
                if (success) {
                    _currentUser.value = _currentUser.value?.copy(
                        nickname = nickname,
                        profession = profession
                    )
                }
                onComplete(success, error)
            }
        }
    }

    fun updateUserProfileImage(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val randomImageUrl = "https://picsum.photos/200/200?random=${System.currentTimeMillis()}"

            _currentUser.value?.let { user ->
                authService.updateUserProfileImage(randomImageUrl) { success ->
                    if (success) {
                        _currentUser.value = user.copy(profileImageUrl = randomImageUrl)
                    }
                    onComplete(success)
                }
            }
        }
    }
}