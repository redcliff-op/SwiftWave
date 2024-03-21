package com.example.swiftwave.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.swiftwave.R
import com.example.swiftwave.ui.components.DeleteMessageDialog
import com.example.swiftwave.ui.components.ImageDialog
import com.example.swiftwave.ui.components.chatCard
import com.example.swiftwave.ui.viewmodels.FirebaseViewModel
import com.example.swiftwave.ui.viewmodels.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun personChatScreen(
    firebaseViewModel: FirebaseViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
){
    firebaseViewModel.getMessagesWithUser()
    val chatList = firebaseViewModel.chatMessages.collectAsState()
    val userList by firebaseViewModel.chatListUsers.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {uri -> firebaseViewModel.imageUri = uri}
    )
    val ctx = LocalContext.current
    val imageLoader = ImageLoader.Builder(ctx)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    DisposableEffect(Unit){
        taskViewModel.showNavBar = false
        onDispose {
            taskViewModel.showNavBar = true
            taskViewModel.expandedPersonInfo = false
            firebaseViewModel.text = ""
            firebaseViewModel.stopConversationsListener()
            firebaseViewModel._chatMessages.value = emptyList()
        }
    }

    if(taskViewModel.showDeleteMsgDialog){
        DeleteMessageDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel,
            navController = navController
        )
    }

    if(taskViewModel.showImageDialog){
        ImageDialog(
            taskViewModel = taskViewModel,
            firebaseViewModel = firebaseViewModel
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RectangleShape,
            onClick = {
                taskViewModel.expandedPersonInfo = !taskViewModel.expandedPersonInfo
            }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        top = 10.dp,
                        bottom = 20.dp,
                        start = 10.dp,
                        end = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.backicon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Row (
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    AsyncImage(
                        model = firebaseViewModel.chattingWith?.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = firebaseViewModel.chattingWith?.username.toString(),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        AnimatedVisibility(visible = userList.first { it.userId == firebaseViewModel.chattingWith?.userId }.online==true) {
                            Text(
                                text = "Online",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    taskViewModel.expandedPersonInfo = !taskViewModel.expandedPersonInfo
                }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = taskViewModel.expandedPersonInfo,
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = firebaseViewModel.chattingWith?.mail.toString(),
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Mail",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                    if(firebaseViewModel.chattingWith?.bio!!.isNotEmpty()){
                        Spacer(modifier = Modifier.size(5.dp))
                        Text(
                            text = firebaseViewModel.chattingWith?.bio.toString(),
                            fontSize = 17.sp,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Bio",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                }
            }
            AnimatedVisibility(taskViewModel.chatOptions) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Divider(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 10.dp,
                            top = 5.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(
                        onClick = {
                            taskViewModel.chatOptions = false
                            firebaseViewModel.selectedMessage = null
                            taskViewModel.isEditing = false
                            firebaseViewModel.text = ""
                            firebaseViewModel.imageUri = null
                        }
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.backicon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .rotate(90f)
                        )
                    }
                    Row{
                        AnimatedVisibility(firebaseViewModel.selectedMessage?.senderID == firebaseViewModel.userData?.userId){
                            IconButton(
                                onClick = {
                                    firebaseViewModel.text = firebaseViewModel.selectedMessage?.message.toString()
                                    firebaseViewModel.imageUri = firebaseViewModel.selectedMessage?.image?.toUri()
                                    taskViewModel.isEditing = true
                                }
                            ){
                                Icon(
                                    painter = painterResource(id = R.drawable.editicon),
                                    contentDescription = null,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                if(firebaseViewModel.selectedMessage?.message.toString().isNotEmpty()){
                                    taskViewModel.copyToClipboard(ctx,firebaseViewModel.selectedMessage?.message.toString())
                                    firebaseViewModel.selectedMessage = null
                                    taskViewModel.chatOptions = false
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.copyicon),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                taskViewModel.showDeleteMsgDialog = true
                            }
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.deleteimageicon),
                                contentDescription = null,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Image(
                painter = painterResource(id = R.drawable.chatbg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary.copy(0.5f), blendMode = BlendMode.Overlay)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(50.dp)
                        .weight(1f),
                    reverseLayout = true
                ) {
                    itemsIndexed(chatList.value.sortedBy { it.time }.reversed()) {index, message ->
                        chatCard(
                            message,
                            chatList.value.size-1-index,
                            firebaseViewModel,
                            taskViewModel
                        )
                    }
                }
                AnimatedVisibility(visible =userList.first { it.userId == firebaseViewModel.chattingWith?.userId }.typing == firebaseViewModel.userData?.userId) {
                    Row (
                        modifier = Modifier.fillMaxWidth(0.9f),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(ctx).data(data = R.drawable.typing).apply(block = {
                                    size(Size.ORIGINAL)
                                }).build(), imageLoader = imageLoader
                            ),
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = "Typing...", fontWeight = FontWeight.Bold)
                    }
                }
                AnimatedVisibility(visible = firebaseViewModel.imageUri!=null){
                    ElevatedCard{
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text =
                                    if(taskViewModel.isEditing){
                                        "Edit Message"
                                    }else{
                                        "Send Image?"
                                    },
                                    fontSize = 20.sp
                                )
                                if(taskViewModel.isEditing){
                                    IconButton(onClick = {
                                        firebaseViewModel.imageUri = null
                                        firebaseViewModel.text = ""
                                        firebaseViewModel.selectedMessage = null
                                        taskViewModel.isEditing = false
                                        taskViewModel.chatOptions = false
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.crossicon),
                                            contentDescription =  null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }else{
                                    IconButton(onClick = {
                                        firebaseViewModel.imageUri = null
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.deleteimageicon),
                                            contentDescription =  null,
                                            modifier = Modifier.size(20.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            AsyncImage(
                                model = firebaseViewModel.imageUri,
                                contentDescription = null,
                                modifier = Modifier.size(280.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
                if(firebaseViewModel.chattingWith?.blocked?.contains(firebaseViewModel.userData?.userId.toString()) == true){
                    Text(
                        text = "You cannot Message this Person Anymore",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        fontWeight = FontWeight.Bold
                    )
                }else{
                    Row (
                        modifier = Modifier.fillMaxWidth(0.9f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        OutlinedTextField(
                            value = firebaseViewModel.text,
                            onValueChange = {
                                newText -> firebaseViewModel.text = newText
                                firebaseViewModel.updateTypingStatus()
                            },
                            shape = RoundedCornerShape(30.dp),
                            label = {
                                Text(
                                    text = "Message",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.8f),
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.5f),
                                unfocusedBorderColor = Color.Transparent,
                            ),
                            trailingIcon = {
                                AnimatedVisibility(visible = firebaseViewModel.imageUri==null && (!taskViewModel.isEditing || firebaseViewModel.selectedMessage?.image!=null)) {
                                    IconButton(
                                        onClick = {
                                            imagePicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.padding(end = 10.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.sendimageicon),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(30.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        )
                        AnimatedVisibility(visible = firebaseViewModel.imageUri!=null || firebaseViewModel.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    if(taskViewModel.isEditing){
                                        firebaseViewModel.editMessage(
                                            firebaseViewModel.chattingWith?.userId.toString(),
                                            firebaseViewModel.selectedMessage?.time!!,
                                            firebaseViewModel.text,
                                            if(firebaseViewModel.selectedMessage!!.curUserReaction==null){
                                                null
                                            }else{
                                                firebaseViewModel.selectedMessage!!.curUserReaction
                                            }
                                        )
                                    }else{
                                        firebaseViewModel.uploadImageAndSendMessage(
                                            firebaseViewModel.chattingWith?.userId.toString(),
                                            firebaseViewModel.text
                                        )
                                        if(firebaseViewModel.imageUri!=null){
                                            Toast.makeText(
                                                ctx,
                                                "Image will be Uploaded and Sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    firebaseViewModel.text = ""
                                    firebaseViewModel.imageUri = null
                                    taskViewModel.isEditing = false
                                    taskViewModel.chatOptions = false
                                    firebaseViewModel.selectedMessage = null
                                }
                            ){
                                Icon(
                                    painter = painterResource(id = R.drawable.sendicon),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}