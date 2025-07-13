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

class MainViewModel : ViewModel() {
    //private val messagingService = MessagingService.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    private val authService = AuthService.getInstance()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _filteredConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val filteredConversations: StateFlow<List<Conversation>> = _filteredConversations.asStateFlow()

    init {
        //loadConversations()
        loadCurrentUser()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterConversations()
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

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        _isSearching.value = true
//        messagingService.searchUsers(query) { users ->
//            _searchResults.value = users
//            _isSearching.value = false
//        }
    }
}