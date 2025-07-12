package com.vdavitashviliekvitsiani.messenger_app.util

import java.text.SimpleDateFormat
import java.util.*


fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60 * 1000 -> "1 min"  // Less than 1 minute
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min"  // Minutes
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hour"  // Hours
        else -> {
            val dateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
            dateFormat.format(Date(this)).uppercase()
        }
    }
}

fun String.isValidNickname(): Boolean {
    return this.isNotBlank() && this.length >= 3 && this.matches(Regex("[a-zA-Z0-9_]+"))
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() && this.length >= 6
}