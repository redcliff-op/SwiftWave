package com.example.swiftwave.ui.components;

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftwave.R
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun demoChatCard(
    index: Int,
    messageData: MessageData,
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    fontSize: Int ? = 17,
    roundedCornerRadius: Int ? = 30
){
    val swapColors = firebaseViewModel.swapChatColors
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                    shape = RoundedCornerShape(roundedCornerRadius!!.dp),
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
                        if(messageData.media!=null){
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
                            Card(
                                modifier = Modifier
                                    .padding(
                                        top = 3.dp,
                                        bottom = 3.dp,
                                        start = 3.dp,
                                        end = 3.dp
                                    ),
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
                                            text = messageData.message.toString() + "    ",
                                            fontSize = fontSize!!.sp,
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
                                            text = firebaseViewModel.userData?.username.toString(),
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
                                        Column(
                                            modifier = Modifier,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if(messageData.repliedTo?.message?.isNotEmpty()==true){
                                                Text(
                                                    text = messageData.repliedTo?.message.toString(),
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = fontSize!!.sp
                                                )
                                            }
                                        }
                                    }
                                }
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
                                fontSize = fontSize!!.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                            )
                        }
                        Row (
                            modifier = Modifier
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = taskViewModel.getTime(messageData.time ?: 0),
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                                fontSize = 12.sp,
                            )
                            if(messageData.senderID==firebaseViewModel.userData?.userId){
                                Icon(
                                    painter = painterResource(id = R.drawable.sentnotifiericon),
                                    contentDescription = null,
                                    tint = Color.White,
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