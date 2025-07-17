package com.vdavitashviliekvitsiani.messenger_app.util

import java.text.SimpleDateFormat
import java.util.*


fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60 * 1000 -> "1 min"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hour"
        else -> {
            val dateFormat = SimpleDateFormat("d MMM", Locale.ENGLISH)
            dateFormat.format(Date(this)).uppercase()
        }
    }
}

