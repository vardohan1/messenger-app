package com.vdavitashviliekvitsiani.messenger_app.model

data class User(
    val uid: String = "",
    val nickname: String = "",
    val profession: String = "",
    val profileImageUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "nickname" to nickname,
            "profession" to profession,
            "profileImageUrl" to profileImageUrl,
            "isOnline" to isOnline,
            "lastSeen" to lastSeen
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): User {
            return User(
                uid = map["uid"] as? String ?: "",
                nickname = map["nickname"] as? String ?: "",
                profession = map["profession"] as? String ?: "",
                profileImageUrl = map["profileImageUrl"] as? String ?: "",
                isOnline = map["isOnline"] as? Boolean ?: false,
                lastSeen = map["lastSeen"] as? Long ?: 0L
            )
        }
    }
}