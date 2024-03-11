package com.example.swiftwave.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.swiftwave.ui.components.BlockedUserCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel

@Composable
fun blockedScreen(
    firebaseViewModel: FirebaseViewModel
){
    val blockedUserList = firebaseViewModel.blockedUsers.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(
                text = "Blocked Users",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if(blockedUserList.value.isEmpty()){
                Text(
                    text = "No Blocked Users",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            LazyColumn {
                items(blockedUserList.value){userData ->
                    BlockedUserCard(
                        userData = userData,
                        firebaseViewModel = firebaseViewModel
                    )
                }
            }
        }
    }
}