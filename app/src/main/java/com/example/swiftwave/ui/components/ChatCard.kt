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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.swiftwave.R
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel
import io.sanghun.compose.video.RepeatMode
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.uri.VideoPlayerMediaItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun chatCard(
    messageData: MessageData,
    index: Int,
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    val chatList by firebaseViewModel.chatMessages.collectAsState()
    val fontSize = firebaseViewModel.userData?.userPref?.fontSize!!
    val roundedCornerRadius = firebaseViewModel.userData?.userPref?.roundedCornerRadius!!
    val swapColors = firebaseViewModel.userData?.userPref?.swapChatColors!!
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
                val emojiList = mutableListOf("\uD83D\uDC4D","\uD83D\uDC4E","❤\uFE0F","\uD83D\uDE2E","\uD83D\uDE2D","\uD83D\uDE02")
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
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                shape = CircleShape
                            )
                            .clickable {
                                taskViewModel.allEmojis = true
                            }
                    ){
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(26.dp)
                        )
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
                    if (firebaseViewModel.selectedMessage?.time.toString() == messageData.time.toString() || firebaseViewModel.repliedToIndex.collectAsState().value == index || firebaseViewModel.searchIndex == index) {
                        MaterialTheme.colorScheme.secondary.copy(0.5f)
                    } else {
                        Color.Transparent
                    }
                )
                .combinedClickable(
                    onLongClick = {
                        if (firebaseViewModel.chattingWith?.blocked?.contains(firebaseViewModel.userData?.userId.toString()) == false) {
                            firebaseViewModel.selectedMessage = messageData
                            if(messageData.isVideo==true){
                                firebaseViewModel.isUploadingVideo = true
                            }else{
                                firebaseViewModel.isUploadingVideo = false
                            }
                            taskViewModel.chatOptions = true
                        }
                    },
                    onClick = {
                        if (firebaseViewModel.chattingWith?.blocked?.contains(firebaseViewModel.userData?.userId.toString()) == false) {
                            taskViewModel.chatOptions = false
                            firebaseViewModel.selectedMessage = null
                            firebaseViewModel.repliedTo = null
                        }
                    },
                    onDoubleClick = {
                        firebaseViewModel.repliedTo = messageData
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
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                    roundedCornerRadius.dp
                                }
                            }else{
                                roundedCornerRadius.dp
                            },
                            bottomStart =
                            if(messageData.senderID!=firebaseViewModel.userData?.userId){
                                if(index!=chatList.size-1){
                                    if(chatList[index+1].senderID!=firebaseViewModel.userData?.userId){
                                        5.dp
                                    }else{
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                    roundedCornerRadius.dp
                                }
                            }else{
                                roundedCornerRadius.dp
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
                                            roundedCornerRadius.dp
                                        }
                                    }else{
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                     roundedCornerRadius.dp
                                },
                            topEnd =
                                if(messageData.senderID==firebaseViewModel.userData?.userId){
                                    if(index!=0){
                                        if(chatList[index-1].senderID==firebaseViewModel.userData?.userId){
                                            5.dp
                                        }else{
                                            roundedCornerRadius.dp
                                        }
                                    }else{
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                    roundedCornerRadius.dp
                                },
                            bottomStart =
                            if(messageData.senderID!=firebaseViewModel.userData?.userId){
                                if(index!=chatList.size-1){
                                    if(chatList[index+1].senderID!=firebaseViewModel.userData?.userId){
                                        5.dp
                                    }else{
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                    roundedCornerRadius.dp
                                }
                            }else{
                                roundedCornerRadius.dp
                            },
                            bottomEnd =
                                if(messageData.senderID==firebaseViewModel.userData?.userId){
                                    if(index!=chatList.size-1){
                                        if(chatList[index+1].senderID==firebaseViewModel.userData?.userId){
                                            5.dp
                                        }else{
                                            roundedCornerRadius.dp
                                        }
                                    }else{
                                        roundedCornerRadius.dp
                                    }
                                }else{
                                    roundedCornerRadius.dp
                                }
                        )
                    },
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if(messageData.senderID==firebaseViewModel.userData?.userId){
                            if(!swapColors){
                                MaterialTheme.colorScheme.primaryContainer
                            }else{
                                MaterialTheme.colorScheme.surface
                            }
                        }else{
                            if(swapColors){
                                MaterialTheme.colorScheme.primaryContainer
                            }else{
                                MaterialTheme.colorScheme.surface
                            }
                        }
                    )
                ) {
                    Column(
                        modifier =
                        if(messageData.image!=null){
                            Modifier.width(280.dp)
                        }else{
                            Modifier
                        },
                        horizontalAlignment =
                        if(messageData.senderID==firebaseViewModel.userData?.userId)
                            Alignment.End
                        else
                            Alignment.Start,
                    ){
                        if(messageData.repliedTo!=null){
                            val user =
                                if(messageData.repliedTo?.senderID==firebaseViewModel.userData?.userId)
                                    "You"
                                else{
                                    firebaseViewModel.chattingWith?.username
                                }
                            Card(
                                modifier = Modifier
                                    .padding(
                                        top = 5.dp,
                                        bottom = 5.dp,
                                        start = 5.dp,
                                        end = 5.dp
                                    )
                                    .clickable {
                                        messageData.repliedTo?.time?.let {
                                            firebaseViewModel.findRepliedToIndex(it)
                                        }
                                    },
                                shape = RoundedCornerShape(roundedCornerRadius.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        if(messageData.senderID==firebaseViewModel.userData?.userId){
                                            if(!swapColors)
                                                MaterialTheme.colorScheme.surface
                                            else
                                                MaterialTheme.colorScheme.secondaryContainer
                                        }
                                        else{
                                            if(swapColors)
                                                MaterialTheme.colorScheme.surface
                                            else
                                                MaterialTheme.colorScheme.secondaryContainer
                                        }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                ) {
                                    Box {
                                        Text(
                                            text = messageData.message.toString() + "  ",
                                            fontSize = fontSize.sp,
                                            color =
                                                if(messageData.senderID==firebaseViewModel.userData?.userId){
                                                    if(!swapColors){
                                                        MaterialTheme.colorScheme.surface
                                                    }else{
                                                        MaterialTheme.colorScheme.secondaryContainer
                                                    }
                                                }
                                                else {
                                                    if (!swapColors) {
                                                        MaterialTheme.colorScheme.secondaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.surface

                                                    }
                                                },
                                            maxLines = 1
                                        )
                                        Text(
                                            text = user.toString(),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = fontSize.sp
                                        )
                                    }
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        if(messageData.repliedTo?.image!=null){
                                            if(messageData.repliedTo?.isVideo==true){
                                                Icon(
                                                        painter = painterResource(id = R.drawable.videonotifiericon),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                                .size(25.dp),
                                                        tint = Color.White
                                                )
                                            }else{
                                                SubcomposeAsyncImage(
                                                        model = messageData.repliedTo?.image,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                                .size(40.dp)
                                                                .aspectRatio(1f)
                                                                .clip(shape = RoundedCornerShape(4.dp)),
                                                        contentScale = ContentScale.Crop,
                                                        loading = {
                                                            Row (
                                                                    modifier =  Modifier.fillMaxSize(),
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                            ){
                                                                CircularProgressIndicator()
                                                            }
                                                        },
                                                        error = {
                                                            Row (
                                                                    modifier =  Modifier.fillMaxSize(),
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                            ){
                                                                Icon(
                                                                        painter = painterResource(id = R.drawable.erroricon),
                                                                        contentDescription = null,
                                                                        modifier = Modifier.size(30.dp)
                                                                )
                                                            }
                                                        }
                                                )
                                            }
                                            Spacer(modifier = Modifier.size(5.dp))
                                        }
                                        Column(
                                            modifier = Modifier,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if(messageData.repliedTo?.image!=null && messageData.storyReply==false && messageData.repliedTo?.isVideo==true && messageData.repliedTo?.message?.isEmpty()==true){
                                                Text(
                                                        text = "Video",
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = fontSize.sp,
                                                        textAlign = TextAlign.Center
                                                )
                                            }
                                            if(messageData.repliedTo?.image!=null && messageData.storyReply==false && messageData.repliedTo?.isVideo==false){
                                                Text(
                                                    text = "Photo",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = fontSize.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            if(messageData.repliedTo?.image!=null && messageData.storyReply==true){
                                                var storyUser = "Your"
                                                if(messageData.repliedTo?.senderID!=firebaseViewModel.userData?.userId)
                                                    storyUser = firebaseViewModel.chattingWith?.username.toString().substring(0,10).split(" ").get(0)+"'s"
                                                Text(
                                                    text = "Replied to\n$storyUser Story",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = fontSize.sp
                                                )
                                            }
                                            if(messageData.repliedTo?.message?.isNotEmpty()==true){
                                                Text(
                                                    text = messageData.repliedTo?.message.toString(),
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = fontSize.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(messageData.image!=null){
                            if(messageData.isVideo==true){
                                Box(
                                    modifier = Modifier.size(300.dp),
                                    contentAlignment = Alignment.Center
                                ){
                                    VideoPlayer(
                                        mediaItems = listOf(
                                            VideoPlayerMediaItem.NetworkMediaItem(
                                                url = messageData.image,
                                            )
                                        ),
                                        handleLifecycle = false,
                                        autoPlay = false,
                                        usePlayerController = false,
                                        enablePip = false,
                                        handleAudioFocus = true,
                                        volume = 0.5f,
                                        repeatMode = RepeatMode.NONE,
                                        modifier = Modifier
                                            .size(300.dp)
                                            .padding(
                                                start = 3.dp,
                                                end = 3.dp,
                                                top = 3.dp
                                            )
                                            .clip(RoundedCornerShape(10.dp))
                                            .align(Alignment.Center),
                                    )
                                    Row(
                                        modifier = Modifier
                                            .size(300.dp)
                                            .clickable {
                                                firebaseViewModel.videoString = messageData.image
                                                firebaseViewModel.mediaViewText =
                                                    messageData.message.toString()
                                                firebaseViewModel.sentBy =
                                                    messageData.senderID.toString()
                                                taskViewModel.showVideoDialog =
                                                    !taskViewModel.showVideoDialog
                                                if (firebaseViewModel.sentBy == firebaseViewModel.userData?.userId) {
                                                    firebaseViewModel.imageDialogProfilePicture =
                                                        firebaseViewModel.profilePicture
                                                } else {
                                                    firebaseViewModel.imageDialogProfilePicture =
                                                        firebaseViewModel.chattingWith?.profilePictureUrl.toString()
                                                }
                                            },
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.playicon),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                ),
                                            tint = Color.Unspecified
                                        )
                                    }
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.BottomStart
                                    ){
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Box(
                                                modifier = Modifier
                                                    .padding(
                                                        horizontal = 15.dp,
                                                        vertical = 8.dp
                                                    )
                                                    .background(
                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                            ){
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 5.dp)
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.videonotifiericon),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp),
                                                        tint = Color.White
                                                    )
                                                    Spacer(modifier = Modifier.size(5.dp))
                                                    Text(
                                                        text = "Video",
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                                SubcomposeAsyncImage(
                                    model = messageData.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(
                                            start = 3.dp,
                                            end = 3.dp,
                                            top = 3.dp,
                                        )
                                        .clickable {
                                            firebaseViewModel.imageString = messageData.image
                                            firebaseViewModel.mediaViewText =
                                                messageData.message.toString()
                                            firebaseViewModel.sentBy =
                                                messageData.senderID.toString()
                                            taskViewModel.showImageDialog =
                                                !taskViewModel.showImageDialog
                                            if (firebaseViewModel.sentBy == firebaseViewModel.userData?.userId) {
                                                firebaseViewModel.imageDialogProfilePicture =
                                                    firebaseViewModel.profilePicture
                                            } else {
                                                firebaseViewModel.imageDialogProfilePicture =
                                                    firebaseViewModel.chattingWith?.profilePictureUrl.toString()
                                            }
                                        }.size(280.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Row (
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.size(280.dp)
                                        ){
                                            CircularProgressIndicator()
                                        }
                                    }
                                )
                            }
                        }
                        if(messageData.message?.isNotEmpty() == true){
                            Text(
                                text = messageData.message.toString(),
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp,
                                        top = if(messageData.repliedTo!=null) 0.dp else 5.dp,
                                        end = 15.dp
                                    ),
                                fontSize = fontSize.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        }
                        Row (
                            modifier = Modifier
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom =
                                        if(messageData.curUserReaction!=null || messageData.otherUserReaction!=null)
                                            2.dp
                                        else
                                            0.dp,
                                    top = if((messageData.curUserReaction!=null || messageData.otherUserReaction!=null) && messageData.message?.isEmpty()==true)
                                        2.dp
                                    else
                                        0.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            if(messageData.starred == true){
                                Icon(
                                    painter = painterResource(id = R.drawable.selectedstar),
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = taskViewModel.getTime(messageData.time ?: 0),
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                                fontSize = 12.sp,
                            )
                            if(messageData.isForwarded==true){
                                Text(
                                    text = "Forwarded",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                            if(!(messageData.otherUserReaction.isNullOrBlank())) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(1.dp)
                                ){
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        AsyncImage(
                                            model = firebaseViewModel.chattingWith?.profilePictureUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .size(25.dp)
                                        )
                                        Text(
                                            text = if(!messageData.otherUserReaction.isNullOrBlank()) messageData.otherUserReaction.toString() else "",
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 5.dp,
                                                    vertical = 3.dp
                                                )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                            if(!(messageData.curUserReaction.isNullOrBlank())) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.secondary.copy(0.5f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(1.dp)
                                        .clickable {
                                            firebaseViewModel.editMessage(
                                                firebaseViewModel.chattingWith?.userId.toString(),
                                                messageData.time!!,
                                                messageData.message.toString(),
                                                null
                                            )
                                        }
                                ){
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        AsyncImage(
                                            model = firebaseViewModel.profilePicture,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .clip(shape = CircleShape)
                                                .size(25.dp)
                                        )
                                        Text(
                                            text = if(!messageData.curUserReaction.isNullOrBlank()) messageData.curUserReaction.toString() else "",
                                            modifier = Modifier
                                                .padding(
                                                    horizontal = 5.dp,
                                                    vertical = 3.dp
                                                ),
                                        )
                                    }
                                }
                            }
                            if(messageData.senderID==firebaseViewModel.userData?.userId){
                                Icon(
                                    painter = painterResource(id = R.drawable.sentnotifiericon),
                                    contentDescription = null,
                                    tint =
                                    if(messageData.read == true && firebaseViewModel.userData?.userPref?.readRecipients==true){
                                        Color.White
                                    }else{
                                        Color.Gray
                                    },
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}