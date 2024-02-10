package com.example.swiftwave.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.swiftwave.ui.components.PersonCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.example.swiftwave.ui.components.CustomDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val scope = rememberCoroutineScope()
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
            items(
                items = chatListUsers.value,
                key = {it.userId.toString()}
            ){userData ->
                val dismissState = rememberDismissState(
                    initialValue = DismissValue.Default,
                    positionalThreshold = { swipeActivationFloat -> swipeActivationFloat / 3 }
                )
                LaunchedEffect(userData) {
                    dismissState.reset()
                }
                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    background = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.Transparent
                                DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primaryContainer
                                DismissValue.DismissedToStart -> MaterialTheme.colorScheme.errorContainer
                            }, label = ""
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            Row (
                                modifier = Modifier.fillMaxSize()
                            ){
                                AnimatedVisibility(visible = dismissState.targetValue == DismissValue.DismissedToStart) {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ){
                                        Column {
                                            Text(text = "Remove Contacts and all Chats?")
                                            Text(text = "This operation is Irreversible")
                                        }
                                        IconButton(onClick = { scope.launch { dismissState.reset() } }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                                        }
                                        IconButton(onClick = {
                                            firebaseViewModel.deleteFriend(userData.userId.toString())
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                                AnimatedVisibility(visible = dismissState.targetValue == DismissValue.DismissedToEnd){
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ){
                                        IconButton(onClick = { scope.launch { dismissState.reset() } }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    dismissContent = {
                        PersonCard(
                            userData = userData,
                            firebaseViewModel = firebaseViewModel,
                            navController = navController,
                            taskViewModel = taskViewModel
                        )
                    }
                )
            }
        }
    }
}