package com.example.swiftwave.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.swiftwave.R
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.ui.components.demoChatCard
import com.example.swiftwave.ui.components.doodleBackgroundList
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel

@Composable
fun chatSettings(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
) {
    DisposableEffect(Unit) {
        taskViewModel.showNavBar = false
        onDispose {
            taskViewModel.showNavBar = true
        }
    }
    val ctx = LocalContext.current
    var fontSize by remember {
        mutableIntStateOf(firebaseViewModel.userData?.userPref?.fontSize!!)
    }
    var roundedCornerRadius by remember {
        mutableIntStateOf(firebaseViewModel.userData?.userPref?.roundedCornerRadius!!)
    }
    var fontSizeSlider by remember {
        mutableFloatStateOf((fontSize - 14) / (20 - 14).toFloat())
    }
    var roundedCornerSlider by remember {
        mutableFloatStateOf((roundedCornerRadius - 10) / (30 - 10).toFloat())
    }
    var doodle by remember {
        mutableFloatStateOf(firebaseViewModel.userData?.userPref?.doodleBackground!!)
    }
    var doodleSlider by remember {
        mutableFloatStateOf(firebaseViewModel.userData?.userPref?.doodleBackground!!)
    }
    var doodleTint by remember {
        mutableFloatStateOf(firebaseViewModel.userData?.userPref?.doodleTint!!)
    }
    var doodleTintSlider by remember {
        mutableFloatStateOf(firebaseViewModel.userData?.userPref?.doodleTint!!)
    }
    var swapColors by remember {
        mutableStateOf(firebaseViewModel.userData?.userPref?.swapChatColors)
    }
    var background by remember {
        mutableStateOf(firebaseViewModel.userData?.userPref?.background)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chat Settings",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(modifier = Modifier.size(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = background!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.onPrimary.copy(doodleTint),
                            blendMode = BlendMode.Overlay
                        ),
                        alpha = doodle
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Spacer(modifier = Modifier.size(10.dp))
                        demoChatCard(
                            messageData = MessageData(
                                "Hi There, ${firebaseViewModel.userData?.username} here",
                                "loremipsum",
                                1711219956624
                            ),
                            taskViewModel =  taskViewModel,
                            firebaseViewModel = firebaseViewModel,
                            fontSize = fontSize,
                            roundedCornerRadius = roundedCornerRadius
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        demoChatCard(
                            messageData = MessageData(
                                "Welcome to SwiftWave My Friend!",
                                firebaseViewModel.userData?.userId,
                                1711219956624
                            ),
                            taskViewModel =  taskViewModel,
                            firebaseViewModel = firebaseViewModel,
                            fontSize = fontSize,
                            roundedCornerRadius = roundedCornerRadius
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Font Size",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = fontSize.toString())
                }
                Slider(
                    value = fontSizeSlider,
                    onValueChange = {
                        fontSize = (14 + (6 * it)).toInt()
                        fontSizeSlider = it
                        firebaseViewModel.userData?.userPref?.fontSize = fontSize
                    },
                    valueRange = 0f..1f,
                    steps = 5,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
                Row (
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Rounded Corner Radius",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = roundedCornerRadius.toString())
                }
                Slider(
                    value = roundedCornerSlider,
                    onValueChange = {
                        roundedCornerRadius = (10 + (20 * it)).toInt()
                        roundedCornerSlider = it
                        firebaseViewModel.userData?.userPref?.roundedCornerRadius = roundedCornerRadius
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
                Row (
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Doodle Background",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = (doodle*100).toInt().toString()+"%")
                }
                Slider(
                    value = doodleSlider,
                    onValueChange = {
                        doodle = it
                        doodleSlider = it
                        firebaseViewModel.userData?.userPref?.doodleBackground = it
                    },
                    valueRange = 0.01f..1f,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
                Row (
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Doodle Color Tint",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = (doodleTint*100).toInt().toString()+"%")
                }
                Slider(
                    value = doodleTintSlider,
                    onValueChange = {
                        doodleTint = it
                        doodleTintSlider = it
                        firebaseViewModel.userData?.userPref?.doodleTint = it
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "Choose a Doodle Background",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.9f),

                    )
                Spacer(modifier = Modifier.size(10.dp))
                LazyRow(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    items(doodleBackgroundList){
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier
                                .width(100.dp)
                                .height(200.dp)
                                .padding(5.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .border(
                                    width = 3.dp,
                                    color = if (it == background)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    background = it
                                    firebaseViewModel.userData?.userPref?.background = it
                                    firebaseViewModel.updateChatPreferences()
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    onClick = {
                    }
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Column {
                            Text(
                                text = "Inverse Bubble Colors",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Swaps Chat Bubble Colors",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = swapColors!!,
                            onCheckedChange = {
                                swapColors = it
                                firebaseViewModel.swapChatColors = it
                                firebaseViewModel.userData?.userPref?.swapChatColors = it
                            }
                        )
                    }
                }
                ElevatedButton(
                    onClick = {
                        firebaseViewModel.updateChatPreferences()
                        Toast.makeText(
                            ctx,
                            "Preferences Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigateUp()
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Save Settings",
                        fontSize = 17.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}