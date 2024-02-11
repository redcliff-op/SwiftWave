package com.example.swiftwave.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel

@Composable
fun accountScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    firebaseViewModel: FirebaseViewModel
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
            AsyncImage(
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
            text = userData?.mail.toString(),
            fontSize = 20.sp,
        )
        Text(
            text = "Mail ID",
            fontSize = 15.sp,
            color = Color.Gray
        )
        Text(
            text = "People can discover you through your Mail ID !",
            fontSize = 15.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("EditBio")
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = firebaseViewModel.Bio
            )
            Text(
                text = "Bio",
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
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
