package com.example.swiftwave.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.swiftwave.R
import com.example.swiftwave.ui.components.CustomDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun favoritesScreen(
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController
){
    val ctx = LocalContext.current
    firebaseViewModel.loadChatListUsers()
    val favorites = firebaseViewModel.favorites.collectAsState()
    val blockedUsers = firebaseViewModel.blockedUsers.collectAsState()
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
                text = "Favorites",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ){
            items(
                items = favorites.value.filter { userData -> !blockedUsers.value.any { it.userId == userData.userId } }.sortedByDescending { it.latestMessage?.time },
                key = {it.userId.toString()}
            ){userData ->
                val dismissState = rememberSwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                    positionalThreshold = { swipeActivationFloat -> swipeActivationFloat / 3 }
                )
                LaunchedEffect(userData) {
                    dismissState.reset()
                }
                SwipeToDismissBox(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                                SwipeToDismissBoxValue.Settled -> Color.Transparent
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
                                AnimatedVisibility(visible = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ){
                                        Column {
                                            Text(text = "Delete or Block User ?")
                                        }
                                        IconButton(onClick = { scope.launch { dismissState.reset() } }) {
                                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                                        }
                                        IconButton(onClick = {
                                            firebaseViewModel.deleteFriend(userData.userId.toString())
                                            Toast.makeText(
                                                ctx,
                                                "Contact and Chats Deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) {
                                            Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                                        }
                                        IconButton(onClick = {
                                            firebaseViewModel.blockUser(userData.userId.toString())
                                            Toast.makeText(
                                                ctx,
                                                "Blocked User, you can manage blocked users in Settings",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.blockicon),
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                                AnimatedVisibility(visible = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd){
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ){
                                        Text(text = "Remove Contact From Favorites?")
                                        IconButton(onClick = { scope.launch { dismissState.reset() } }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                                        }
                                        IconButton(onClick = {
                                            firebaseViewModel.deleteFavorite(userData.userId.toString())
                                            Toast.makeText(
                                                ctx,
                                                "Removed from Favorites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    content = {
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