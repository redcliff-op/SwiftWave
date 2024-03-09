package com.example.swiftwave.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun chatCard(
    messageData: MessageData,
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(
                color =
                if (firebaseViewModel.selectedMessage?.time.toString() == messageData.time.toString()) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    Color.Transparent
                }
            ).combinedClickable(
                onLongClick = {
                    firebaseViewModel.selectedMessage = messageData
                    taskViewModel.chatOptions = true
                },
                onClick = {
                    taskViewModel.chatOptions = false
                    firebaseViewModel.selectedMessage = null
                }
            )
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start =
                        if (messageData.senderID == firebaseViewModel.userData.userId) {
                            35.dp
                        } else {
                            0.dp
                        },
                    end =
                        if (messageData.senderID == firebaseViewModel.userData.userId) {
                            0.dp
                        } else {
                            35.dp
                        },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
            if(messageData.senderID==firebaseViewModel.userData.userId){
                Arrangement.End
            }else{
                Arrangement.Start
            },
        ){
            Card(
                modifier = Modifier
                    .padding(5.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                    if(messageData.senderID==firebaseViewModel.userData.userId){
                        MaterialTheme.colorScheme.inversePrimary.copy(0.8f)
                    }else{
                        MaterialTheme.colorScheme.secondaryContainer.copy(0.8f)
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
                        AsyncImage(
                            model = messageData.image,
                            contentDescription = null,
                            modifier = Modifier
                                .size(280.dp)
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 10.dp,
                                ).clickable {
                                    firebaseViewModel.imageString = messageData.image
                                    firebaseViewModel.sentBy = messageData.senderID.toString()
                                    taskViewModel.showImageDialog = !taskViewModel.showImageDialog
                                    if(firebaseViewModel.sentBy == firebaseViewModel.userData.userId){
                                        firebaseViewModel.imageDialogProfilePicture = firebaseViewModel.profilePicture
                                    }else{
                                        firebaseViewModel.imageDialogProfilePicture = firebaseViewModel.chattingWith?.profilePictureUrl.toString()
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                    if(messageData.message?.isNotEmpty() == true){
                        Text(
                            text = messageData.message.toString(),
                            modifier = Modifier
                                .padding(
                                    start = 10.dp,
                                    top = 5.dp,
                                    end = 10.dp
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
                            )
                    ){
                        Text(
                            text = taskViewModel.getTime(messageData.time?.toLong() ?: 0),
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 10.dp),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}