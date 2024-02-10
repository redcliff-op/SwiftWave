package com.example.swiftwave.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
        Card(
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