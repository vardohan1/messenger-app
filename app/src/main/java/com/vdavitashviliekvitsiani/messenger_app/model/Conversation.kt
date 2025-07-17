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
}