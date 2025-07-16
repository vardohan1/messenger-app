package com.vdavitashviliekvitsiani.messenger_app.ui.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdavitashviliekvitsiani.messenger_app.ui.theme.MessengerappTheme

class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val otherUserId = intent.getStringExtra("otherUserId") ?: ""
        val otherUserNickname = intent.getStringExtra("otherUserNickname") ?: ""
        val otherUserProfileUrl = intent.getStringExtra("otherUserProfileUrl") ?: ""
        val conversationId = intent.getStringExtra("conversationId") ?: ""
        val otherUserWork = intent.getStringExtra("otherUserWork") ?: ""

        setContent {
            MessengerappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ChatViewModel = viewModel(
                        factory = ChatViewModelFactory(
                            otherUserId = otherUserId,
                            otherUserNickname = otherUserNickname,
                            otherUserProfileUrl = otherUserProfileUrl,
                            conversationId = conversationId,
                            otherUserWork = otherUserWork
                        )
                    )

                    val messages by viewModel.messages.collectAsState()
                    val isLoading by viewModel.isLoading.collectAsState()
                    val messageText by viewModel.messageText.collectAsState()

                    ChatScreen(
                        otherUserNickname = otherUserNickname,
                        otherUserProfileUrl = otherUserProfileUrl,
                        messages = messages,
                        messageText = messageText,
                        onMessageTextChange = viewModel::updateMessageText,
                        onSendMessage = viewModel::sendMessage,
                        onBackClick = { finish() },
                        isLoading = isLoading,
                        otherUserWork = otherUserWork
                    )
                }
            }
        }
    }
}