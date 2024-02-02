package com.example.swiftwave.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.swiftwave.auth.UserData

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun accountScreen(
    userData: UserData?,
    onSignOut: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    ){
        Text(
            text = "Account",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(20.dp))
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            GlideImage(
                model = userData?.profilePictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape)
            )
            Spacer(modifier = Modifier.size(20.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userData?.username.toString(),
                    fontSize = 20.sp,
                )
                Text(
                    text = "Your Name",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }

        }
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Details",
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = userData?.mail?.substring(0, userData.mail.toString().length-10) ?: "",
            fontSize = 20.sp,
        )
        Text(
            text = "User ID",
            fontSize = 15.sp,
            color = Color.Gray
        )
        Text(
            text = "People can discover you through your UserID !",
            fontSize = 15.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.size(10.dp))
        Button(onClick = {onSignOut()}) {
            Text(
                text = "Log Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
