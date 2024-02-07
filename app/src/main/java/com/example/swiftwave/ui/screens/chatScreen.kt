package com.example.swiftwave.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.swiftwave.ui.components.CustomDialog
import com.example.swiftwave.ui.components.PersonCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun chatScreen(
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController
){
    firebaseViewModel.loadChatListUsers()
    val chatListUsers = firebaseViewModel.chatListUsers.collectAsState()
    if(taskViewModel.showDialog){
        CustomDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.9f)
        ){
            Text(
                text = "Chats",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ){
            items(chatListUsers.value){userData ->
                PersonCard(
                    userData = userData,
                    firebaseViewModel = firebaseViewModel,
                    navController = navController,
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}