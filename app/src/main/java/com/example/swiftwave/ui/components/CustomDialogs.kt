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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    image: String
){
    Dialog(
        onDismissRequest = {
            taskViewModel.showImageDialog = false
        },
    ){
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun SetProfilePictureDialog(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    val ctx = LocalContext.current
    Dialog(
        onDismissRequest = {
            taskViewModel.showProfilePictureDialog = false
            firebaseViewModel.imageUri = null
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ElevatedCard {
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set Profile Picture?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    Text(
                        text = "Images with 1:1 Aspect Ratio Are Preferred"
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                AsyncImage(
                    model = firebaseViewModel.imageUri,
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Row {
                    ElevatedButton(onClick = {
                        taskViewModel.showProfilePictureDialog = false
                        firebaseViewModel.imageUri = null

                    }) {
                        Text(
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                    ElevatedButton(onClick = {
                        firebaseViewModel.updateProfilePic()
                        taskViewModel.showProfilePictureDialog = false
                        firebaseViewModel.imageUri = null
                        Toast.makeText(
                            ctx,
                            "Profile Picture Updated",
                            Toast.LENGTH_SHORT
                        ).show()
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