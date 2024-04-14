package com.example.swiftwave.data.model


data class MessageData(
    val message : String? = "",
    val senderID : String? = "",
    val time : Long ? = 0,
    val media : String? = null,
    var curUserReaction : String? = "",
    var otherUserReaction : String? = "",
    var repliedTo: MessageData? = null,
    var isForwarded: Boolean? = false,
    var starred: Boolean? = false,
    var read: Boolean? = false,
    var storyReply: Boolean? = false,
    var isVideo: Boolean? = false,
    var isFile: Boolean? = false,
    var filename: String? = ""
)