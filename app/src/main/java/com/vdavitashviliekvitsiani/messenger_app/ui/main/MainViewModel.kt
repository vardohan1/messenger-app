package com.vdavitashviliekvitsiani.messenger_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdavitashviliekvitsiani.messenger_app.model.Conversation
import com.vdavitashviliekvitsiani.messenger_app.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

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
        loadConversations()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true

            _conversations.value = getMockConversations()
            filterConversations()

            _isLoading.value = false
        }
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

    private fun getMockConversations(): List<Conversation> {
        return listOf(
            Conversation(
                id = "1",
                participants = listOf("currentUser", "sayed_eftiaz"),
                lastMessage = "On my way home but I needed to stop by the book store to",
                lastMessageTime = System.currentTimeMillis() - 5 * 60 * 1000, // 5 minutes ago
                lastMessageSender = "sayed_eftiaz",
                otherUserNickname = "Sayed Eftiaz",
                otherUserProfileUrl = "",
                otherUserProfession = "Manager"
            ),
            Conversation(
                id = "2",
                participants = listOf("currentUser", "sanjida_akter"),
                lastMessage = "On my way home but I needed to stop by the book store to",
                lastMessageTime = System.currentTimeMillis() - 15 * 60 * 1000, // 15 minutes ago
                lastMessageSender = "sanjida_akter",
                otherUserNickname = "Sanjida Akter",
                otherUserProfileUrl = "",
                otherUserProfession = "Designer"
            ),
            Conversation(
                id = "3",
                participants = listOf("currentUser", "tour_de_bhutan"),
                lastMessage = "On my way home but I needed to stop by the book store to",
                lastMessageTime = System.currentTimeMillis() - 60 * 60 * 1000, // 1 hour ago
                lastMessageSender = "tour_de_bhutan",
                otherUserNickname = "Tour de Bhutan",
                otherUserProfileUrl = "",
                otherUserProfession = "Manager"
            )
        )
    }

    fun signOut() {
        authService.signOut()
    }
}