package com.example.swiftwave.data.model

import com.example.swiftwave.R

data class UserPref(
    var recentEmojis: MutableList<String>? = emptyList<String>().toMutableList(),
    var fontSize: Int? = 14,
    var roundedCornerRadius: Int? = 30,
    var doodleBackground: Float? = 1f,
    var doodleTint: Float? = 0.5f,
    var swapChatColors: Boolean ? = false,
    var background:Int ? = R.drawable.i0,
    var readRecipients: Boolean? = true,
    var uploadQuality: Int? = 90
)