package com.example.swiftwave.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.swiftwave.R
import com.example.swiftwave.data.model.bottomNavBarItem
import com.example.swiftwave.data.model.messageCardItem

class TaskViewModel : ViewModel(){

    var selected by mutableIntStateOf(0)
    var isSignedIn by mutableStateOf(false)

    fun initialiseBottomNavBar(): List<bottomNavBarItem> {
        return listOf(
            bottomNavBarItem("Chats", R.drawable.chaticon),
            bottomNavBarItem("Account",R.drawable.accounticon),
            bottomNavBarItem("Settings",R.drawable.settingsicon)
        )
    }
}