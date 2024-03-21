package com.example.swiftwave.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun chatCard(
    messageData: MessageData,
    index: Int,
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    val chatList by firebaseViewModel.chatMessages.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(visible = firebaseViewModel.selectedMessage?.time.toString() == messageData.time.toString()) {
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                val emojiList = mutableListOf("\uD83D\uDC4D","\uD83D\uDC4E","❤\uFE0F","\uD83D\uDE2E","\uD83D\uDE2D","\uD83D\uDE4F")
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        if(firebaseViewModel.userData?.userId == messageData.senderID){
                            Arrangement.End
                        }else{
                            Arrangement.Start
                        },
                ) {
                    emojiList.forEach{
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                    shape = CircleShape
                                )
                                .combinedClickable(
                                    onClick = {
                                        if (messageData.curUserReaction == it) {
                                            messageData.curUserReaction = null
                                            firebaseViewModel.editMessage(
                                                firebaseViewModel.chattingWith?.userId.toString(),
                                                messageData.time!!,
                                                messageData.message.toString(),
                                                null
                                            )
                                        } else {
                                            messageData.curUserReaction = it
                                            firebaseViewModel.editMessage(
                                                firebaseViewModel.chattingWith?.userId.toString(),
                                                messageData.time!!,
                                                messageData.message.toString(),
                                                messageData.curUserReaction
                                            )
                                        }
                                        firebaseViewModel.selectedMessage = null
                                        taskViewModel.chatOptions = false
                                    }
                                )
                        ){
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(10.dp),
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .background(
                    color =
                    if (firebaseViewModel.selectedMessage?.time.toString() == messageData.time.toString()) {
                        MaterialTheme.colorScheme.secondary.copy(0.5f)
                    } else {
                        Color.Transparent
                    }
                )
                .combinedClickable(
                    onLongClick = {
                        if(firebaseViewModel.chattingWith?.blocked?.contains(firebaseViewModel.userData?.userId.toString()) == false){
                            firebaseViewModel.selectedMessage = messageData
                            taskViewModel.chatOptions = true
                        }
                    },
                    onClick = {
                        if(firebaseViewModel.chattingWith?.blocked?.contains(firebaseViewModel.userData?.userId.toString()) == false){
                            taskViewModel.chatOptions = false
                            firebaseViewModel.selectedMessage = null
                        }
                    }
                )
        ){
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start =
                        if (messageData.senderID == firebaseViewModel.userData?.userId) {
                            35.dp
                        } else {
                            0.dp
                        },
                        end =
                        if (messageData.senderID == firebaseViewModel.userData?.userId) {
                            0.dp
                        } else {
                            35.dp
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                if(messageData.senderID==firebaseViewModel.userData?.userId){
                    Arrangement.End
                }else{
                    Arrangement.Start
                },
            ){
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    shape = if(messageData.image!=null){
                        RoundedCornerShape(
                            topEnd = 10.dp,
                            topStart = 10.dp,
                            bottomEnd =
                            if(messageData.senderID==firebaseViewModel.userData?.userId){
                                if(index!=chatList.size-1){
                                    if(chatList[index+1].senderID==firebaseViewModel.userData?.userId){
                                        5.dp
                                    }else{
                                        30.dp
                                    }
                                }else{
                                    30.dp
                                }
                            }else{
                                30.dp
                            },
                            bottomStart =
                            if(messageData.senderID!=firebaseViewModel.userData?.userId){
                                if(index!=chatList.size-1){
                                    if(chatList[index+1].senderID!=firebaseViewModel.userData?.userId){
                                        5.dp
                                    }else{
                                        30.dp
                                    }
                                }else{
                                    30.dp
                                }
                            }else{
                                30.dp
                            }
                        )
                    }else{
                        RoundedCornerShape(
                            topStart =
                                if(messageData.senderID!=firebaseViewModel.userData?.userId){
                                    if(index!=0){
                                        if(chatList[index-1].senderID!=firebaseViewModel.userData?.userId){
                                            5.dp
                                        }else{
                                            30.dp
                                        }
                                    }else{
                                        30.dp
                                    }
                                }else{
                                     30.dp
                                },
                            topEnd =
                                if(messageData.senderID==firebaseViewModel.userData?.userId){
                                    if(index!=0){
                                        if(chatList[index-1].senderID==firebaseViewModel.userData?.userId){
                                            5.dp
                                        }else{
                                            30.dp
                                        }
                                    }else{
                                        30.dp
                                    }
                                }else{
                                    30.dp
                                },
                            bottomStart =
                            if(messageData.senderID!=firebaseViewModel.userData?.userId){
                                if(index!=chatList.size-1){
                                    if(chatList[index+1].senderID!=firebaseViewModel.userData?.userId){
                                        5.dp
                                    }else{
                                        30.dp
                                    }
                                }else{
                                    30.dp
                                }
                            }else{
                                30.dp
                            },
                            bottomEnd =
                                if(messageData.senderID==firebaseViewModel.userData?.userId){
                                    if(index!=chatList.size-1){
                                        if(chatList[index+1].senderID==firebaseViewModel.userData?.userId){
                                            5.dp
                                        }else{
                                            30.dp
                                        }
                                    }else{
                                        30.dp
                                    }
                                }else{
                                    30.dp
                                }
                        )
                    },
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if(messageData.senderID==firebaseViewModel.userData?.userId){
                            MaterialTheme.colorScheme.primaryContainer
                        }else{
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Column(
                        modifier =
                        if(messageData.image!=null){
                            Modifier.width(280.dp)
                        }else{
                            Modifier
                        }
                    ){
                        if(messageData.image!=null){
                            SubcomposeAsyncImage(
                                model = messageData.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(280.dp)
                                    .padding(
                                        start = 7.dp,
                                        end = 7.dp,
                                        top = 7.dp,
                                    )
                                    .clickable {
                                        firebaseViewModel.imageString = messageData.image
                                        firebaseViewModel.imageViewText = messageData.message.toString()
                                        firebaseViewModel.sentBy = messageData.senderID.toString()
                                        taskViewModel.showImageDialog =
                                            !taskViewModel.showImageDialog
                                        if (firebaseViewModel.sentBy == firebaseViewModel.userData?.userId) {
                                            firebaseViewModel.imageDialogProfilePicture =
                                                firebaseViewModel.profilePicture
                                        } else {
                                            firebaseViewModel.imageDialogProfilePicture =
                                                firebaseViewModel.chattingWith?.profilePictureUrl.toString()
                                        }
                                    },
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Row (
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        CircularProgressIndicator()
                                    }
                                }
                            )
                        }
                        if(messageData.message?.isNotEmpty() == true){
                            Text(
                                text = messageData.message.toString(),
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp,
                                        top = 10.dp,
                                        end = 15.dp
                                    ),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Row (
                            modifier = Modifier
                                .padding(
                                    bottom = 5.dp,
                                    start = 10.dp,
                                    top =
                                    if(messageData.image!=null && messageData.message.toString().isEmpty()){
                                        5.dp
                                    }else{
                                        0.dp
                                    },
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = taskViewModel.getTime(messageData.time?.toLong() ?: 0),
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                fontSize = 12.sp,
                            )
                            if(!(messageData.otherUserReaction.isNullOrBlank())) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(3.dp)
                                ){
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        AsyncImage(
                                            model = firebaseViewModel.chattingWith?.profilePictureUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .size(30.dp)
                                        )
                                        Text(
                                            text = if(!messageData.otherUserReaction.isNullOrBlank()) messageData.otherUserReaction.toString() else "",
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 3.dp
                                                )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                            Spacer(modifier = Modifier.size(5.dp))
                            if(!(messageData.curUserReaction.isNullOrBlank())) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(3.dp)
                                ){
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        AsyncImage(
                                            model = firebaseViewModel.profilePicture,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .size(30.dp)
                                        )
                                        Text(
                                            text = if(!messageData.curUserReaction.isNullOrBlank()) messageData.curUserReaction.toString() else "",
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 3.dp
                                                )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                }
            }
        }
    }
}