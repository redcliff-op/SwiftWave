package com.example.swiftwave.data.model

data class MessageData(
    val message : String? = "",
    val senderID : String? = "",
    val time : Long ? = 0,
    val image : String? = null,
    var curUserReaction : String? = "",
    var otherUserReaction : String? = ""
)