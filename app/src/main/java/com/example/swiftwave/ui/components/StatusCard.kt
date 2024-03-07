package com.example.swiftwave.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusCard(
    userData: UserData,
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel
){
    val viewdList = firebaseViewModel.viewedStatus.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                firebaseViewModel.chattingWith = userData
                firebaseViewModel.imageString = userData.status.toString()
                taskViewModel.showImageDialog = true
                viewdList.value.add(userData.userId.toString())
                firebaseViewModel._viewedStatus.value = viewdList.value
            }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(
                    modifier =
                    Modifier
                        .size(65.dp)
                        .border(
                            BorderStroke(
                                3.dp,
                                color =
                                    if(!viewdList.value.contains(userData.userId.toString())){
                                        MaterialTheme.colorScheme.primary
                                    }else{
                                        Color.Gray
                                    }
                            ),
                            CircleShape
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(shape = CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = userData.username.toString(),
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}