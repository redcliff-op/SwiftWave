package com.example.swiftwave.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImage.CancelledResult.uriContent
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.swiftwave.R
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.ui.components.SetProfilePictureAndStatusDialog
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun accountScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    navController: NavController,
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    var readRecipients by remember {
        mutableStateOf(firebaseViewModel.userData?.userPref?.readRecipients)
    }
    var quality by remember {
        mutableIntStateOf(firebaseViewModel.userData?.userPref?.uploadQuality!!)
    }
    var qualitySlider by remember {
        mutableFloatStateOf((quality-10)/(100-10).toFloat())
    }
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
        uriContent, CropImageOptions(
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
            aspectRatioX = 1,
            aspectRatioY = 1,
            fixAspectRatio = true,
            autoZoomEnabled = true,
            outputCompressQuality = 30,
            showIntentChooser = true
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 80.dp
            )
            .verticalScroll(reverseScrolling = true, state = rememberScrollState()),
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
            IconButton(
                onClick = {
                    imagePicker.launch(cropOption)
                    taskViewModel.showSetProfilePictureAndStatusDialog = true
                }
            ) {
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
                    .padding(vertical = 10.dp),
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
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                navController.navigate("ChatSettings")
            }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column {
                    Text(
                        text = "Chat Customisations",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Personalize your Chat Experience!",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.chaticon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                taskViewModel.expandUploadQualitySetting = !taskViewModel.expandUploadQualitySetting
            }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Upload Quality",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Adjust Image and Status Quality",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.hdicon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            AnimatedVisibility(visible = taskViewModel.expandUploadQualitySetting) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Uses Efficient Bitmap Compression, ie Image size decreases drastically for very little drop in Image Quality",
                        fontSize = 15.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "Quality",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            AnimatedVisibility(quality == 90){
                                Text(
                                    text = "(Recommended)",
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        }
                        Text(text = "$quality%")
                    }
                    Slider(
                        value = qualitySlider,
                        onValueChange = {
                            quality = (10 + (90 * it)).toInt()
                            qualitySlider = it
                            firebaseViewModel.userData?.userPref?.uploadQuality = quality
                            firebaseViewModel.updateChatPreferences()
                        },
                        steps = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Read Receipts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Allow both users to see read status",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = readRecipients!!,
                onCheckedChange = {
                    readRecipients = it
                    firebaseViewModel.userData?.userPref?.readRecipients = it
                    firebaseViewModel.updateChatPreferences()
                }
            )
        }
    }
}
