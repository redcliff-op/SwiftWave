package com.example.swiftwave.data.model

data class UploadStatus(
    val progress: Float? = 0f,
    val MegabytesTransferred: String? = "",
    val totalMegaBytes: String? = ""
)