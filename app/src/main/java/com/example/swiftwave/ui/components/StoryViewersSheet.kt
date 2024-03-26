package com.example.swiftwave.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swiftwave.R
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryViewersSheet(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel
){
    val viewerList = firebaseViewModel.storyViewers.collectAsState()
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        onDismissRequest = {
            taskViewModel.showStoryViewers = false
        }
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Viewers",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(10.dp))
            LazyColumn {
                items(viewerList.value){
                    ViewerCard(
                        userData = it,
                        firebaseViewModel = firebaseViewModel
                    )
                }
            }
        }
    }
}