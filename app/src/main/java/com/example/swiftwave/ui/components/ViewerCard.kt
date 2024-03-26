package com.example.swiftwave.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.swiftwave.R
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel

@Composable
fun ViewerCard(
    userData: UserData,
    firebaseViewModel: FirebaseViewModel
){
    val isLiked = userData.viewedStories?.indexOfFirst { it.liked == true && it.userId == firebaseViewModel.userData?.userId }!= -1
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .clip(shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userData.username.toString(),
                            fontSize = 18.sp,
                        )
                    }
                }
                if(isLiked){
                    Icon(
                        painter = painterResource(id = R.drawable.likedicon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}