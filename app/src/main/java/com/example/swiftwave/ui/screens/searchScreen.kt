package com.example.swiftwave.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.swiftwave.ui.components.PersonCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun searchScreen(
    firebaseViewModel: FirebaseViewModel,
    navController: NavController,
    taskViewModel: TaskViewModel
){
    val filteredContacts by firebaseViewModel.searchContacts.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = firebaseViewModel.searchContact,
            onValueChange = {
                newValue -> firebaseViewModel.searchContact = newValue
                firebaseViewModel.filterContacts(firebaseViewModel.chatListUsers.value,firebaseViewModel.searchContact)
            },
            label = { Text("Search contacts") },
            placeholder = { Text(text = "Search By Name or Email")},
            trailingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
            ),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(30.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                unfocusedBorderColor = Color.Transparent,
            )
        )
        Spacer(modifier = Modifier.size(20.dp))
        if(firebaseViewModel.searchContact.isNotEmpty()){
            LazyColumn{
                items(filteredContacts){userData ->
                    PersonCard(
                        userData = userData,
                        firebaseViewModel = firebaseViewModel,
                        navController = navController,
                        taskViewModel = taskViewModel
                    )
                }
            }
        }
    }
}