package com.example.swiftwave.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.swiftwave.R
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.components.CustomDialog
import com.example.swiftwave.ui.components.ImageDialog
import com.example.swiftwave.ui.components.StatusCard
import com.example.swiftwave.ui.components.StoryCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun chatScreen(
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController
){
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()){}
    LaunchedEffect(key1 = true){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    firebaseViewModel.loadChatListUsers()
    val chatListUsers = firebaseViewModel.chatListUsers.collectAsState()
    val blockedUsers = firebaseViewModel.blockedUsers.collectAsState()
    val filteredUsers = firebaseViewModel.searchContacts.collectAsState()
    val statusList = firebaseViewModel.usersWithStatus.collectAsState()
    if(taskViewModel.showDialog){
        CustomDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel
        )
    }
    if(taskViewModel.showImageDialog){
        ImageDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel
        )
    }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Chats",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        OutlinedTextField(
            value = firebaseViewModel.searchContact,
            onValueChange = {
                newValue -> firebaseViewModel.searchContact = newValue
                firebaseViewModel.filterContacts(firebaseViewModel.chatListUsers.value,firebaseViewModel.searchContact)
            },
            label = { Text("Search") },
            placeholder = { Text(text = "Search By Name or Email")},
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(30.dp))
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                    Spacer(modifier = Modifier.size(10.dp))
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(30.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.2f),
            )
        )
        AnimatedVisibility(visible = statusList.value.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                items(statusList.value){ UserData ->
                    StoryCard(
                        userData = UserData,
                        taskViewModel = taskViewModel,
                        firebaseViewModel = firebaseViewModel
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Message",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ){
            items(
                items = if(filteredUsers.value.isEmpty()){
                    chatListUsers.value.filter { userData -> !blockedUsers.value.any { it.userId == userData.userId } }.sortedByDescending { it.latestMessage?.time }
                }else{
                    filteredUsers.value
                },
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
                                AnimatedVisibility(
                                    visible = dismissState.targetValue == DismissValue.DismissedToStart,
                                    enter = slideInHorizontally (
                                        initialOffsetX = { fullWidth -> fullWidth }
                                    ),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth }
                                    )
                                ) {
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
                                AnimatedVisibility(visible = dismissState.targetValue == DismissValue.DismissedToEnd){
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ){
                                        Text(text = "Add Contact to Favorites ?")
                                        IconButton(onClick = { scope.launch { dismissState.reset() } }) {
                                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                                        }
                                        IconButton(onClick = {
                                            firebaseViewModel.addUserToFavorites(userData.mail.toString(),ctx)
                                            scope.launch { dismissState.reset() }
                                        }) {
                                            Icon(Icons.Rounded.Favorite, contentDescription = "Favorites")
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