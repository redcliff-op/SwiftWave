package com.example.swiftwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.swiftwave.R
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.components.SetProfilePictureAndStatusDialog
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun accountScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    if(firebaseViewModel.imageUri!=null && taskViewModel.showSetProfilePictureAndStatusDialog){
        SetProfilePictureAndStatusDialog(
            firebaseViewModel = firebaseViewModel,
            taskViewModel = taskViewModel
        )
    }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> firebaseViewModel.imageUri = uri}
    )
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
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(20.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row {
                AsyncImage(
                    model = firebaseViewModel.profilePicture,
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
            IconButton(onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
                taskViewModel.showSetProfilePictureAndStatusDialog = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.profilepicediticon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
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
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "Settings",
            fontSize = 25.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                navController.navigate("BlockedScreen")
            }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column {
                    Text(
                        text = "Blocked Users",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage Blocked Users",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.blockicon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
