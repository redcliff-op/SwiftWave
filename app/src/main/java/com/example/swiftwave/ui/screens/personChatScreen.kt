package com.example.swiftwave.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.swiftwave.R
import com.example.swiftwave.ui.components.DeleteMessageDialog
import com.example.swiftwave.ui.components.chatCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun personChatScreen(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
){
    firebaseViewModel.getMessagesWithUser()
    val chatList = firebaseViewModel.chatMessages.collectAsState()
    DisposableEffect(Unit){
        taskViewModel.showNavBar = false
        onDispose {
            taskViewModel.showNavBar = true
            taskViewModel.expandedPersonInfo = false
            firebaseViewModel.stopConversationsListener()
            firebaseViewModel._chatMessages.value = emptyList()
        }
    }

    if(taskViewModel.showDeleteMsgDialog){
        DeleteMessageDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel,
            navController = navController
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            painter = painterResource(
                id = if(isSystemInDarkTheme()){
                    R.drawable.chatbg
                }else{
                    R.drawable.chatlightbg
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(0.5f), blendMode = BlendMode.Overlay)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp),
                onClick = {
                    taskViewModel.expandedPersonInfo = !taskViewModel.expandedPersonInfo
                }
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.backicon),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    AsyncImage(
                        model = firebaseViewModel.chattingWith?.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(shape = RoundedCornerShape(20.dp))
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = firebaseViewModel.chattingWith?.username.toString(),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                        maxLines =
                        if(taskViewModel.expandedPersonInfo){
                            2
                        }else{
                            1
                        },
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = {
                        taskViewModel.expandedPersonInfo = !taskViewModel.expandedPersonInfo
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = taskViewModel.expandedPersonInfo,
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Details",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = firebaseViewModel.chattingWith?.mail.toString(),
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Mail",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                        if(firebaseViewModel.chattingWith?.bio!!.isNotEmpty()){
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = firebaseViewModel.chattingWith?.bio.toString(),
                                fontSize = 20.sp,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Bio",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .height(50.dp)
                    .weight(1f),
                state = rememberLazyListState(10000)
            ) {
                items(chatList.value) { message ->
                    chatCard(
                        message,
                        firebaseViewModel,
                        taskViewModel
                    )
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                OutlinedTextField(
                    value = firebaseViewModel.text,
                    onValueChange = {newText -> firebaseViewModel.text = newText},
                    shape = RoundedCornerShape(30.dp),
                    label = {
                        Text(
                            text = "Message",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                        unfocusedBorderColor = Color.Transparent,
                    )
                )
                AnimatedVisibility(visible = firebaseViewModel.text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            firebaseViewModel.sendMessage(firebaseViewModel.chattingWith?.userId.toString(),firebaseViewModel.text.trim())
                            firebaseViewModel.text = ""
                        }
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.sendicon),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}