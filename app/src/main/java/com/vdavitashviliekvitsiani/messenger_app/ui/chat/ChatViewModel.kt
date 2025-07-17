package com.vdavitashviliekvitsiani.messenger_app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vdavitashviliekvitsiani.messenger_app.model.Message
import com.vdavitashviliekvitsiani.messenger_app.service.MessagingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val otherUserId: String,
    private val conversationId: String
) : ViewModel() {

    private val messagingService = MessagingService.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMessages()
        markMessagesAsRead()
    }

    private fun loadMessages() {
        _isLoading.value = true

        if (conversationId.isNotEmpty()) {
            messagingService.getMessagesForConversation(conversationId) { messagesList ->
                _messages.value = messagesList
                _isLoading.value = false
            }
        } else {
            messagingService.createConversation(otherUserId) { newConversationId ->
                if (newConversationId != null) {
                    messagingService.getMessagesForConversation(newConversationId) { messagesList ->
                        _messages.value = messagesList
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun markMessagesAsRead() {
        viewModelScope.launch {
            val actualConversationId = if (conversationId.isNotEmpty()) {
                conversationId
            } else {
                val currentUserId = com.vdavitashviliekvitsiani.messenger_app.service.AuthService.getInstance().getCurrentUser()?.uid ?: ""
                if (currentUserId < otherUserId) "${currentUserId}_${otherUserId}" else "${otherUserId}_${currentUserId}"
            }

            messagingService.markMessagesAsRead(actualConversationId,
                com.vdavitashviliekvitsiani.messenger_app.service.AuthService.getInstance().getCurrentUser()?.uid ?: ""
            )
        }
    }

    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isNotEmpty()) {
            messagingService.sendMessage(
                receiverId = otherUserId,
                content = text
            ) { success, error ->
                if (success) {
                    _messageText.value = ""
                }
            }
        }
    }
}

class ChatViewModelFactory(
    private val otherUserId: String,
    private val conversationId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(otherUserId, conversationId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}