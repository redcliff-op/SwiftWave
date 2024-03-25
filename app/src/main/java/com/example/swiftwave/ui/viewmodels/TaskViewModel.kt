package com.example.swiftwave.ui.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskViewModel : ViewModel(){

    var selected by mutableIntStateOf(0)
    var isSignedIn by mutableStateOf(false)
    var expandedPersonInfo by mutableStateOf(false)
    var showNavBar by mutableStateOf(false)
    var showDialog by mutableStateOf(false)
    var showDeleteMsgDialog by mutableStateOf(false)
    var showImageDialog by mutableStateOf(false)
    var showSetProfilePictureAndStatusDialog by mutableStateOf(false)
    var chatOptions by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var isUploadingStatus by mutableStateOf(false)
    var showDeleteStatusOption by mutableStateOf(false)
    var allEmojis by mutableStateOf(false)
    var isForwarding by mutableStateOf(false)
    var isStatus by mutableStateOf(true)

    fun getTime(mills: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun copyToClipboard(context: Context, text: String) {
        viewModelScope.launch {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", text)
            clipboardManager.setPrimaryClip(clipData)
        }
    }
}