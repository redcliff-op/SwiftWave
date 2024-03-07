package com.example.swiftwave.ui.components

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swiftwave.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonCard(
    userData: UserData,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController,
    taskViewModel: TaskViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = {
            firebaseViewModel.chattingWith = userData
            firebaseViewModel.startMessageListener()
            navController.navigate("PersonChat")
        }
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .size(70.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = userData.username.toString(),
                    fontSize = 25.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        if(userData.latestMessage?.image !=null){
                            Icon(
                                painter = painterResource(id = R.drawable.latestimageicon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                        if(userData.latestMessage?.message != null){
                            Text(
                                text = userData.latestMessage?.message.toString(),
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    if(userData.latestMessage?.time!=null){
                        Text(
                            text = taskViewModel.getTime(userData.latestMessage?.time ?:0),
                            color = Color.Gray,
                        )
                    }
                }
            }
        }
    }
}