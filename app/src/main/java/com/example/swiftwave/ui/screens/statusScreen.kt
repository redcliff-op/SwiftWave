package com.example.swiftwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.swiftwave.ui.components.ImageDialog
import com.example.swiftwave.ui.components.SetProfilePictureAndStatusDialog
import com.example.swiftwave.ui.components.StatusCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun statusScreen(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
){
    firebaseViewModel.loadChatListUsers()
    val userList = firebaseViewModel.usersWithStatus.collectAsState(initial = emptyList())
    if(firebaseViewModel.mediaUri!=null && taskViewModel.showSetProfilePictureAndStatusDialog){
        SetProfilePictureAndStatusDialog(
            firebaseViewModel = firebaseViewModel,
            taskViewModel = taskViewModel
        )
    }
    val imagePicker = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            firebaseViewModel.mediaUri = result.uriContent
        }
    }
    val cropOption = CropImageContractOptions(
        CropImage.CancelledResult.uriContent, CropImageOptions(
            progressBarColor = MaterialTheme.colorScheme.primary.toArgb(),
            activityBackgroundColor = MaterialTheme.colorScheme.surface.toArgb(),
            borderLineColor = Color.White.toArgb(),
            cropperLabelTextColor = MaterialTheme.colorScheme.primary.toArgb(),
            activityMenuIconColor = MaterialTheme.colorScheme.primary.toArgb(),
            guidelinesColor = Color.White.toArgb(),
            toolbarColor = MaterialTheme.colorScheme.surface.toArgb(),
            activityMenuTextColor = MaterialTheme.colorScheme.primary.toArgb(),
            borderCornerColor = Color.White.toArgb(),
            toolbarTintColor = Color.White.toArgb(),
            toolbarTitleColor = MaterialTheme.colorScheme.primary.toArgb(),
            toolbarBackButtonColor = MaterialTheme.colorScheme.primary.toArgb(),
            autoZoomEnabled = true,
            outputCompressQuality = firebaseViewModel.userData?.userPref?.uploadQuality!!,
            showIntentChooser = true
        )
    )
    if(taskViewModel.showImageDialog){
        ImageDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel,
            navController = navController
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Status",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable {
                    taskViewModel.isUploadingStatus = true
                    imagePicker.launch(cropOption)
                    taskViewModel.showSetProfilePictureAndStatusDialog = true
                },
            verticalAlignment = Alignment.CenterVertically,
        ){
            Box(
                modifier =
                    if(firebaseViewModel.curUserStatus){
                        Modifier
                            .size(65.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary
                                ),
                                CircleShape
                            )
                    }else{
                        Modifier.size(65.dp)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (firebaseViewModel.curUserStatus) {
                                firebaseViewModel.chattingWith = firebaseViewModel.userData
                                firebaseViewModel.imageString =
                                    firebaseViewModel.userData?.status.toString()
                                taskViewModel.showImageDialog = true
                                taskViewModel.showDeleteStatusOption = true
                                firebaseViewModel.imageDialogProfilePicture =
                                    firebaseViewModel.profilePicture
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = firebaseViewModel.profilePicture,
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
                    text = "My Status",
                    fontSize = 20.sp,
                )
                Text(
                    text = "Click to Add Status Update",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row (
            modifier = Modifier.fillMaxWidth(0.9f),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Recent Updates",
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        LazyColumn{
            items(userList.value){ UserData ->  
                StatusCard(
                    userData = UserData ,
                    taskViewModel = taskViewModel,
                    firebaseViewModel = firebaseViewModel
                )
            }
        }
    }
}
