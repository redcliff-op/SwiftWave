package com.example.swiftwave.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.swiftwave.R
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun CustomDialog(
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel
){
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = {
            taskViewModel.showDialog = false
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ){
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1.4f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Add a New User",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    value = firebaseViewModel.newUser,
                    onValueChange = {firebaseViewModel.newUser = it},
                    label = {
                        Text(
                            text = "Enter the Mail ID",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    singleLine = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(20.dp),
                    placeholder = {
                        Text(
                            text = "Mail ID",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
                Spacer(modifier = Modifier.size(15.dp))
                Button(
                    onClick = {
                        firebaseViewModel.addUserToChatList(firebaseViewModel.newUser,ctx)
                        firebaseViewModel.newUser = ""
                        taskViewModel.showDialog = false
                    }
                ){
                    Text(
                        text = "Add",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteMessageDialog(
    taskViewModel: TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
    navController: NavController
){
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = {
            taskViewModel.showDeleteMsgDialog = false
            firebaseViewModel.selectedMessage = null
            taskViewModel.chatOptions = false
        }
    ){
        AlertDialog(
            onDismissRequest = {taskViewModel.showDeleteMsgDialog = false},
            title = { Text(text = "Delete Message?") },
            text = { Text(text = "This Operation is Irreversible") },
            dismissButton = {
                Button(
                    onClick = {
                        taskViewModel.showDeleteMsgDialog = false
                        firebaseViewModel.selectedMessage = null
                        taskViewModel.chatOptions = false
                    },
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        firebaseViewModel.deleteMessage(
                            otherUserId = firebaseViewModel.chattingWith?.userId.toString(),
                            messageData = firebaseViewModel.selectedMessage!!
                        )
                        taskViewModel.showDeleteMsgDialog = false
                        taskViewModel.chatOptions = false
                        firebaseViewModel.selectedMessage = null
                    },
                ) {
                    Text(text = "Delete")
                }
            }
        )
    }
}

@Composable
fun ImageDialog(
    taskViewModel : TaskViewModel,
    firebaseViewModel: FirebaseViewModel,
){
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = {
            taskViewModel.showImageDialog = false
            taskViewModel.showDeleteStatusOption = false
            firebaseViewModel.sentBy = ""
            firebaseViewModel.imageViewText = ""
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ){
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        taskViewModel.showImageDialog = false
                        taskViewModel.showDeleteStatusOption = false
                        firebaseViewModel.sentBy = ""
                        firebaseViewModel.imageViewText = ""
                    }
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.backicon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Row (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = firebaseViewModel.imageDialogProfilePicture,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = if(firebaseViewModel.chattingWith?.userId == firebaseViewModel.userData.userId || firebaseViewModel.sentBy == firebaseViewModel.userData.userId){
                            "You"
                        }else {
                            firebaseViewModel.chattingWith?.username.toString()
                        },
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines =
                        if(taskViewModel.expandedPersonInfo){
                            2
                        }else{
                            1
                        },
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if(taskViewModel.showDeleteStatusOption){
                    IconButton(onClick = {
                        firebaseViewModel.deleteStatus(firebaseViewModel.userData)
                        taskViewModel.showImageDialog = false;
                        Toast.makeText(
                            ctx,
                            "Status Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        taskViewModel.showDeleteStatusOption = false
                        firebaseViewModel.sentBy = ""
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if(firebaseViewModel.imageViewText.isNotBlank()) 10.dp else 30.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                AsyncImage(
                    model = firebaseViewModel.imageString,
                    contentDescription = null,
                )
                if(firebaseViewModel.imageViewText.isNotBlank()){
                    Text(
                        text = firebaseViewModel.imageViewText,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SetProfilePictureAndStatusDialog(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = {
            taskViewModel.showSetProfilePictureAndStatusDialog = false
            taskViewModel.isUploadingStatus = false
            firebaseViewModel.imageUri = null
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 30.dp,
                        top = 30.dp
                    ),
                verticalArrangement = Arrangement.SpaceAround
            ){
                Text(
                    text =
                    if(taskViewModel.isUploadingStatus){
                        "Set Status ?"
                    }else{
                     "Update Profile Picture?"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                Spacer(modifier = Modifier.size(10.dp))
                AsyncImage(
                    model = firebaseViewModel.imageUri,
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Row {
                    ElevatedButton(onClick = {
                        taskViewModel.showSetProfilePictureAndStatusDialog = false
                        firebaseViewModel.imageUri = null
                        taskViewModel.isUploadingStatus = false
                    }) {
                        Text(
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    ElevatedButton(onClick = {
                        if(taskViewModel.isUploadingStatus){
                            firebaseViewModel.setStatus()
                        }else{
                            firebaseViewModel.updateProfilePic()
                            firebaseViewModel.imageUri = null
                        }
                        Toast.makeText(
                            ctx,
                            if(taskViewModel.isUploadingStatus){
                                "Status will be Updated !"
                            }else{
                                "Profile Picture will  be Updated"
                            },
                            Toast.LENGTH_SHORT
                        ).show()
                        taskViewModel.showSetProfilePictureAndStatusDialog = false
                        taskViewModel.isUploadingStatus = false
                    }) {
                        Text(
                            text = "Set",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}