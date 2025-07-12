package com.vdavitashviliekvitsiani.messenger_app.model

data class Conversation(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val lastMessageSender: String = "",
    val otherUserNickname: String = "",
    val otherUserProfileUrl: String = "",
    val otherUserProfession: String = "",
    val unreadCount: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "participants" to participants,
            "lastMessage" to lastMessage,
            "lastMessageTime" to lastMessageTime,
            "lastMessageSender" to lastMessageSender,
            "otherUserNickname" to otherUserNickname,
            "otherUserProfileUrl" to otherUserProfileUrl,
            "otherUserProfession" to otherUserProfession,
            "unreadCount" to unreadCount
        )
    }
}