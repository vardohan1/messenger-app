package com.vdavitashviliekvitsiani.messenger_app.service

import com.google.firebase.database.*
import com.vdavitashviliekvitsiani.messenger_app.model.Conversation
import com.vdavitashviliekvitsiani.messenger_app.model.Message
import com.vdavitashviliekvitsiani.messenger_app.model.MessageType
import com.vdavitashviliekvitsiani.messenger_app.model.User

class MessagingService {

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")
    private val conversationsRef = database.getReference("conversations")
    private val usersRef = database.getReference("users")

    companion object {
        @Volatile
        private var INSTANCE: MessagingService? = null

        fun getInstance(): MessagingService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MessagingService().also { INSTANCE = it }
            }
        }
    }

    fun sendMessage(
        receiverId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        onResult: (Boolean, String?) -> Unit
    ) {
        val currentUser = AuthService.getInstance().getCurrentUser()
        if (currentUser == null) {
            onResult(false, "User not authenticated")
            return
        }

        val messageId = messagesRef.push().key ?: return
        val conversationId = generateConversationId(currentUser.uid, receiverId)

        val message = Message(
            id = messageId,
            senderId = currentUser.uid,
            receiverId = receiverId,
            content = content,
            timestamp = System.currentTimeMillis(),
            conversationId = conversationId,
            isRead = false,
            messageType = messageType
        )

        messagesRef.child(messageId).setValue(message.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateConversation(conversationId, currentUser.uid, receiverId, content) { success ->
                        onResult(success, if (success) null else "Failed to update conversation")
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Failed to send message")
                }
            }
    }

    fun getMessagesForConversation(
        conversationId: String,
        onResult: (List<Message>) -> Unit
    ) {
        messagesRef.orderByChild("conversationId")
            .equalTo(conversationId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    for (messageSnapshot in snapshot.children) {
                        val messageMap = messageSnapshot.value as? Map<String, Any>
                        messageMap?.let { map ->
                            messages.add(Message.fromMap(map))
                        }
                    }
                    messages.sortBy { it.timestamp }
                    onResult(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun getConversationsForUser(onResult: (List<Conversation>) -> Unit) {
        val currentUser = AuthService.getInstance().getCurrentUser()
        if (currentUser == null) {
            onResult(emptyList())
            return
        }

        conversationsRef.orderByChild("participants/${currentUser.uid}")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val conversations = mutableListOf<Conversation>()
                    var processedCount = 0
                    val totalCount = snapshot.childrenCount.toInt()

                    if (totalCount == 0) {
                        onResult(emptyList())
                        return
                    }

                    for (conversationSnapshot in snapshot.children) {
                        val conversationMap = conversationSnapshot.value as? Map<String, Any>
                        conversationMap?.let { map ->
                            processConversationData(map, currentUser.uid) { conversation ->
                                conversation?.let { conversations.add(it) }
                                processedCount++

                                if (processedCount == totalCount) {
                                    conversations.sortByDescending { it.lastMessageTime }
                                    onResult(conversations)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun searchUsers(query: String, onResult: (List<User>) -> Unit) {
        if (query.isEmpty()) {
            onResult(emptyList())
            return
        }

        val currentUser = AuthService.getInstance().getCurrentUser()

        usersRef.orderByChild("nickname")
            .startAt(query.lowercase())
            .endAt(query.lowercase() + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val userMap = userSnapshot.value as? Map<String, Any>
                        userMap?.let { map ->
                            val user = User.fromMap(map)
                            if (user.uid != currentUser?.uid) {
                                users.add(user)
                            }
                        }
                    }
                    onResult(users)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun createConversation(otherUserId: String, onResult: (String?) -> Unit) {
        val currentUser = AuthService.getInstance().getCurrentUser()
        if (currentUser == null) {
            onResult(null)
            return
        }

        val conversationId = generateConversationId(currentUser.uid, otherUserId)

        conversationsRef.child(conversationId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        onResult(conversationId)
                    } else {
                        val conversationData = mapOf(
                            "id" to conversationId,
                            "participants" to mapOf(
                                currentUser.uid to true,
                                otherUserId to true
                            ),
                            "lastMessage" to "",
                            "lastMessageTime" to System.currentTimeMillis(),
                            "lastMessageSender" to ""
                        )

                        conversationsRef.child(conversationId)
                            .setValue(conversationData)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(conversationId)
                                } else {
                                    onResult(null)
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }

    fun markMessagesAsRead(conversationId: String, currentUserId: String) {
        messagesRef.orderByChild("conversationId")
            .equalTo(conversationId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updates = mutableMapOf<String, Any>()

                    for (messageSnapshot in snapshot.children) {
                        val messageMap = messageSnapshot.value as? Map<String, Any>
                        messageMap?.let { map ->
                            val message = Message.fromMap(map)
                            if (message.receiverId == currentUserId && !message.isRead) {
                                updates["${messageSnapshot.key}/isRead"] = true
                            }
                        }
                    }

                    if (updates.isNotEmpty()) {
                        messagesRef.updateChildren(updates)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun generateConversationId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    private fun updateConversation(
        conversationId: String,
        senderId: String,
        receiverId: String,
        lastMessage: String,
        onResult: (Boolean) -> Unit
    ) {
        val conversationData = mapOf(
            "id" to conversationId,
            "participants" to mapOf(
                senderId to true,
                receiverId to true
            ),
            "lastMessage" to lastMessage,
            "lastMessageTime" to System.currentTimeMillis(),
            "lastMessageSender" to senderId
        )

        conversationsRef.child(conversationId)
            .setValue(conversationData)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    private fun processConversationData(
        conversationMap: Map<String, Any>,
        currentUserId: String,
        onResult: (Conversation?) -> Unit
    ) {
        val participantsMap = conversationMap["participants"] as? Map<String, Any>
        val otherUserId = participantsMap?.keys?.find { it != currentUserId }

        if (otherUserId != null) {
            usersRef.child(otherUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userMap = snapshot.value as? Map<String, Any>
                        userMap?.let { map ->
                            val otherUser = User.fromMap(map)
                            val conversation = Conversation(
                                id = conversationMap["id"] as? String ?: "",
                                participants = participantsMap.keys.toList(),
                                lastMessage = conversationMap["lastMessage"] as? String ?: "",
                                lastMessageTime = conversationMap["lastMessageTime"] as? Long ?: 0L,
                                lastMessageSender = conversationMap["lastMessageSender"] as? String ?: "",
                                otherUserNickname = otherUser.nickname,
                                otherUserProfileUrl = otherUser.profileImageUrl,
                                otherUserProfession = otherUser.profession
                            )
                            onResult(conversation)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult(null)
                    }
                })
        } else {
            onResult(null)
        }
    }
}