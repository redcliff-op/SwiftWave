package com.example.swiftwave.data.model

data class UserPref(
    var recentEmojis: MutableList<String>? = emptyList<String>().toMutableList(),
    var fontSize: Int? = 17,
    var roundedCornerRadius: Int? = 30,
    var doodleBackground: Float? = 1f,
    var doodleTint: Float? = 0.5f,
    var swapChatColors: Boolean ? = false
)