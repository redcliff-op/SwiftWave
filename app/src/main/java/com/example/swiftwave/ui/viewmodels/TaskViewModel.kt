package com.example.swiftwave.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.swiftwave.R
import com.example.swiftwave.data.model.bottomNavBarItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskViewModel : ViewModel(){

    var selected by mutableIntStateOf(0)
    var isSignedIn by mutableStateOf(false)
    var expandedPersonInfo by mutableStateOf(false)
    var showNavBar by mutableStateOf(true)

    fun initialiseBottomNavBar(): List<bottomNavBarItem> {
        return listOf(
            bottomNavBarItem("Chats", R.drawable.chaticon),
            bottomNavBarItem("Account",R.drawable.accounticon),
            bottomNavBarItem("Settings",R.drawable.settingsicon)
        )
    }

    fun getTime(mills: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}