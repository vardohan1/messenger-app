package com.vdavitashviliekvitsiani.messenger_app.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    val conversationId: String = "",
    val isRead: Boolean = false,
    val messageType: MessageType = MessageType.TEXT
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "content" to content,
            "timestamp" to timestamp,
            "conversationId" to conversationId,
            "isRead" to isRead,
            "messageType" to messageType.name
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Message {
            return Message(
                id = map["id"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                receiverId = map["receiverId"] as? String ?: "",
                content = map["content"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: 0L,
                conversationId = map["conversationId"] as? String ?: "",
                isRead = map["isRead"] as? Boolean ?: false,
                messageType = MessageType.valueOf(
                    map["messageType"] as? String ?: MessageType.TEXT.name
                )
            )
        }
    }
}

enum class MessageType {
    TEXT,
    IMAGE
}