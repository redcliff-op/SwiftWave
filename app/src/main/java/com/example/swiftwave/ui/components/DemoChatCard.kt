package com.example.swiftwave.ui.components;

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun demoChatCard(
    messageData: MessageData,
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    fontSize: Int ? = 17,
    roundedCornerRadius: Int ? = 30
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                    shape = RoundedCornerShape(roundedCornerRadius?.dp!!),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if(messageData.senderID==firebaseViewModel.userData?.userId){
                            if(firebaseViewModel.swapChatColors == false){
                                MaterialTheme.colorScheme.primaryContainer
                            }else{
                                MaterialTheme.colorScheme.surface
                            }
                        }else{
                            if(firebaseViewModel.swapChatColors == false){
                                MaterialTheme.colorScheme.surface
                            }else{
                                MaterialTheme.colorScheme.primaryContainer
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        if(messageData.message?.isNotEmpty() == true){
                            Text(
                                text = messageData.message.toString(),
                                modifier = Modifier
                                    .padding(
                                        start = 15.dp,
                                        top = if(messageData.repliedTo!=null) 5.dp else 10.dp,
                                        end = 15.dp
                                    ),
                                fontSize = fontSize?.sp!!,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
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
                        }
                    }
                }
            }
        }
    }
}