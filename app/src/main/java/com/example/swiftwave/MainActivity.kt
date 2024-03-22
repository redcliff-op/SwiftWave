package com.example.swiftwave

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.swiftwave.auth.GoogleAuthUiClient
import com.example.swiftwave.ui.screens.accountScreen
import com.example.swiftwave.ui.screens.blockedScreen
import com.example.swiftwave.ui.screens.chatScreen
import com.example.swiftwave.ui.screens.editBioScreen
import com.example.swiftwave.ui.screens.favoritesScreen
import com.example.swiftwave.ui.screens.loginScreen
import com.example.swiftwave.ui.screens.personChatScreen
import com.example.swiftwave.ui.screens.statusScreen
import com.example.swiftwave.ui.theme.SwiftWaveTheme
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.SignInViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val taskViewModel = TaskViewModel()
    val firebaseViewModel = FirebaseViewModel()
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),Color.Transparent.toArgb()
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.Transparent.toArgb(),Color.Transparent.toArgb()
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            SwiftWaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val bottomBarList = taskViewModel.initialiseBottomNavBar()
                    Scaffold (
                        bottomBar = {
                            AnimatedVisibility(visible = taskViewModel.showNavBar) {
                                NavigationBar (
                                    containerColor = Color.Transparent
                                ){
                                    if(taskViewModel.isSignedIn){
                                        bottomBarList.forEachIndexed { index, item ->
                                            NavigationBarItem(
                                                selected = index == taskViewModel.selected,
                                                onClick = {
                                                    taskViewModel.selected = index
                                                    navController.navigate(item.label)
                                                },
                                                icon = {
                                                    Icon(
                                                        painter = painterResource(id = item.icon),
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(0.35f),
                                                        tint =
                                                        if(taskViewModel.selected == index){
                                                            MaterialTheme.colorScheme.primary
                                                        }else{
                                                            Color.White
                                                        }
                                                    )
                                                },
                                                colors = NavigationBarItemColors(Color.Transparent,Color.Transparent,Color.Transparent,Color.Transparent,Color.Transparent,Color.Transparent,Color.Transparent)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        floatingActionButton = {
                            val currentScreen by navController.currentBackStackEntryAsState()
                            AnimatedVisibility(
                                visible = taskViewModel.showNavBar && (currentScreen?.destination?.route.equals("Chats")),
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ){
                                FloatingActionButton(
                                    onClick = {
                                        taskViewModel.showDialog = !taskViewModel.showDialog
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ){
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    ){
                        NavHost(navController = navController, startDestination = "Login"){
                            composable(route = "Chats"){
                                chatScreen(
                                    taskViewModel = taskViewModel,
                                    firebaseViewModel = firebaseViewModel,
                                    navController = navController
                                )
                            }
                            composable(route = "Account"){
                                accountScreen(
                                    userData = googleAuthUiClient.getSignedInUser(),
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUiClient.signOut()
                                            Toast.makeText(
                                                applicationContext,
                                                "Signed out",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            taskViewModel.isSignedIn = false
                                            navController.navigate("Login")
                                        }
                                    },
                                    navController = navController,
                                    firebaseViewModel = firebaseViewModel,
                                    taskViewModel = taskViewModel
                                )
                            }
                            composable(route = "Status"){
                                statusScreen(
                                    firebaseViewModel = firebaseViewModel,
                                    taskViewModel = taskViewModel
                                )
                            }
                            composable(route = "Login"){
                                val viewModel = viewModel<SignInViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(key1 = googleAuthUiClient.getSignedInUser()) {
                                    if(googleAuthUiClient.getSignedInUser() != null) {
                                        taskViewModel.isSignedIn = true
                                        firebaseViewModel.userData = googleAuthUiClient.getSignedInUser()
                                        firebaseViewModel.updateOnlineStatus(true)
                                        firebaseViewModel.addUserToFirestore(firebaseViewModel.userData!!)
                                        firebaseViewModel.getToken()
                                        firebaseViewModel.setupLatestMessageListener()
                                        taskViewModel.showNavBar = true
                                        navController.navigate("Chats")
                                    }
                                }

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if(result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthUiClient.signInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if(state.isSignInSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Sign in successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        taskViewModel.isSignedIn = true
                                        firebaseViewModel.userData = googleAuthUiClient.getSignedInUser()
                                        firebaseViewModel.updateOnlineStatus(true)
                                        firebaseViewModel.addUserToFirestore(firebaseViewModel.userData!!)
                                        firebaseViewModel.getToken()
                                        firebaseViewModel.setupLatestMessageListener()
                                        taskViewModel.showNavBar = true
                                        navController.navigate("Chats")
                                        viewModel.resetState()
                                    }
                                }

                                loginScreen(
                                    state = state,
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    }
                                )
                            }
                            composable(
                                route = "PersonChat",
                                enterTransition = {
                                    fadeIn() + expandVertically()
                                },
                                exitTransition = {
                                    fadeOut() + shrinkVertically()
                                }
                            ){
                                personChatScreen(
                                    firebaseViewModel = firebaseViewModel,
                                    taskViewModel = taskViewModel,
                                    navController = navController,
                                )
                            }
                            composable(route = "EditBio"){
                                editBioScreen(
                                    userData = googleAuthUiClient.getSignedInUser()!!,
                                    firebaseViewModel = firebaseViewModel,
                                    navController = navController
                                )
                            }
                            composable(route = "Favorites"){
                                favoritesScreen(
                                    taskViewModel = taskViewModel,
                                    firebaseViewModel = firebaseViewModel,
                                    navController = navController
                                )
                            }
                            composable(route = "BlockedScreen"){
                                blockedScreen(
                                    firebaseViewModel = firebaseViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        firebaseViewModel.updateTypingStatus(false)
        firebaseViewModel.updateOnlineStatus(false)
    }

    override fun onResume() {
        super.onResume()
        firebaseViewModel.updateOnlineStatus(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        firebaseViewModel.updateTypingStatus(false)
        firebaseViewModel.updateOnlineStatus(false)
    }
}