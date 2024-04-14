package com.example.swiftwave.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.data.model.MessageData
import com.example.swiftwave.data.model.StoryViewers
import com.example.swiftwave.data.model.UploadStatus
import com.example.swiftwave.data.model.UserPref
import com.example.swiftwave.data.remote.callNotifAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.storage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.UUID

class FirebaseViewModel: ViewModel() {

    var userData: UserData? = null
    var chattingWith by mutableStateOf<UserData?>(null)
    var text by mutableStateOf("")
    var mediaUri by mutableStateOf<Uri?>(null)
    var imageString by mutableStateOf("")
    var mediaString by mutableStateOf("")
    var mediaViewText by mutableStateOf("")
    var newUser by mutableStateOf("")
    var Bio by mutableStateOf("")
    var selectedMessage by mutableStateOf<MessageData?>(null)
    var repliedTo by mutableStateOf<MessageData?>(null)
    var forwarded by mutableStateOf<MessageData?>(null)
    var searchContact by mutableStateOf("")
    var profilePicture by mutableStateOf("")
    var curUserStatus by mutableStateOf(false)
    var sentBy by mutableStateOf("")
    var imageDialogProfilePicture by mutableStateOf("")
    var uploadingMedia by mutableStateOf<Uri?>(null)
    var swapChatColors by mutableStateOf(false)
    var isLoadingUsers = false
    var isLoadingChat = false
    var updatingStoryView = false
    var searchText by mutableStateOf("")
    var searchIndex by mutableStateOf<Int?>(null)
    var searchListIndex by mutableStateOf<Int?>(null)
    var isUploadingVideo by mutableStateOf(false)
    var isUploadingFile by mutableStateOf(false)
    var fileName by mutableStateOf<String?>("")
    var uploadFileName by mutableStateOf("")

    private var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _chatListUsers = MutableStateFlow<List<UserData>>(emptyList())
    val chatListUsers: StateFlow<List<UserData>> = _chatListUsers
    val _chatMessages = MutableStateFlow<List<MessageData>>(emptyList())
    val chatMessages: StateFlow<List<MessageData>> = _chatMessages
    private val _favorites = MutableStateFlow<List<UserData>>(emptyList())
    val favorites : StateFlow<List<UserData>> = _favorites
    private val _blockedUsers = MutableStateFlow<List<UserData>>(emptyList())
    val blockedUsers : StateFlow<List<UserData>> = _blockedUsers
    private val _searchContacts = MutableStateFlow<List<UserData>>(emptyList())
    val searchContacts : StateFlow<List<UserData>> = _searchContacts
    private val _usersWithStatus = MutableStateFlow<List<UserData>>(emptyList())
    val usersWithStatus : StateFlow<List<UserData>> = _usersWithStatus
    private val _storyViewers = MutableStateFlow<List<UserData>>(emptyList())
    val storyViewers : StateFlow<List<UserData>> = _storyViewers
    private var conversationsListener: ListenerRegistration? = null
    val _viewedStatus = MutableStateFlow<MutableList<String?>>(emptyList<String>().toMutableList())
    val viewedStatus : StateFlow<MutableList<String?>> get() = _viewedStatus.asStateFlow()
    private val _searchMessages = MutableStateFlow<List<MessageData>>(emptyList())
    val searchMessages : StateFlow<List<MessageData>> = _searchMessages
    private val _repliedToIndex = MutableStateFlow<Int?>(null)
    val repliedToIndex : StateFlow<Int?> = _repliedToIndex
    private val _uploadStatus = MutableStateFlow<UploadStatus?>(null)
    val uploadStatus : StateFlow<UploadStatus?> = _uploadStatus
    val listeners = mutableListOf<ListenerRegistration>()

    fun loadChatListUsers() {
        if (isLoadingUsers) {
            return
        }
        isLoadingUsers = true
        viewModelScope.launch(Dispatchers.IO) {
            val chatListUsers = mutableListOf<UserData>()
            val favorites = mutableListOf<UserData>()
            val currentUserDoc = firebase.collection("users").document(userData?.userId.toString())
            val currentUser = currentUserDoc.get().await().toObject(UserData::class.java)
            if (currentUser != null) {
                fetchBlockedUsersData()
                val friendQueries = mutableListOf<Deferred<DocumentSnapshot>>()
                val latestMessageQueries = mutableListOf<Deferred<DocumentSnapshot>>()
                val favoriteQueries = mutableListOf<Deferred<DocumentSnapshot>>()
                val favoriteLatestMessageQueries = mutableListOf<Deferred<DocumentSnapshot>>()

                for (userId in currentUser.chatList!!) {
                    val friendRef = firebase.collection("users").document(userId)
                    val latestMessageRef = firebase.collection("latest_messages").document(userData?.userId.toString() + "_" + userId)
                    friendQueries.add(async { friendRef.get().await() })
                    latestMessageQueries.add(async { latestMessageRef.get().await() })
                }

                for (userId in currentUser.favorites!!) {
                    val favoriteRef = firebase.collection("users").document(userId)
                    val favoriteLatestMessageRef = firebase.collection("latest_messages").document(userData?.userId.toString() + "_" + userId)
                    favoriteQueries.add(async { favoriteRef.get().await() })
                    favoriteLatestMessageQueries.add(async { favoriteLatestMessageRef.get().await() })
                }

                val friendResults = friendQueries.awaitAll().map { it.toObject(UserData::class.java) }
                val latestMessageResults = latestMessageQueries.awaitAll().map { it.toObject(MessageData::class.java) }

                for (i in friendResults.indices) {
                    friendResults[i]?.latestMessage = latestMessageResults[i]
                    friendResults[i]?.let { chatListUsers.add(it) }
                }
                _chatListUsers.value = chatListUsers

                updateStatusList()
                updateStatusViewerList()
                userData?.let { addUserToFirestore(it) }

                val favoriteResults = favoriteQueries.awaitAll().map { it.toObject(UserData::class.java) }
                val favoriteLatestMessageResults = favoriteLatestMessageQueries.awaitAll().map { it.toObject(MessageData::class.java) }

                for (i in favoriteResults.indices) {
                    favoriteResults[i]?.latestMessage = favoriteLatestMessageResults[i]
                    favoriteResults[i]?.let { favorites.add(it) }
                }
                _favorites.value = favorites
            }
            isLoadingUsers = false
        }
    }

    fun fetchBlockedUsersData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserDoc = firebase.collection("users").document(userData?.userId.toString())
            val blockedUsersIds = currentUserDoc.get().await().toObject(UserData::class.java)?.blocked ?: emptyList()
            if (blockedUsersIds.isNotEmpty()) {
                val blockedUsersQuery = firebase.collection("users").whereIn(FieldPath.documentId(), blockedUsersIds)
                val blockedUsersSnapshot = blockedUsersQuery.get().await()
                val blockedUsersData = blockedUsersSnapshot.documents.mapNotNull { it.toObject(UserData::class.java) }
                _blockedUsers.value = blockedUsersData
            } else {
                _blockedUsers.value = emptyList()
            }
        }
    }

    fun setupLatestMessageListener() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            chatListUsers.value.forEach { user ->
                val listener1 = firebase.collection("conversations").document(userData?.userId.toString())
                    .collection(user.userId.toString())
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener { snapshots, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        loadChatListUsers()
                    }
                listeners.add(listener1)
                val listener2 = firebase.collection("users").document(user.userId!!)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        loadChatListUsers()
                    }
                listeners.add(listener2)
            }
        }
    }

    fun addUserToFirestore(user: UserData) {
        viewModelScope.launch (Dispatchers.IO){
            val userQuery = firebase.collection("users").document(user.userId.toString()).get().await()

            if (!userQuery.exists()) {
                firebase.collection("users").document(user.userId.toString())
                    .set(user)
                    .await()
                firebase.collection("users").document(user.userId.toString())
                    .update("chatList", emptyList<String>())
                    .await()
            }else{
                val currentUser = userQuery.toObject(UserData::class.java)
                userData?.bio = currentUser?.bio.toString()
                profilePicture = currentUser?.profilePictureUrl.toString()
                Bio = currentUser?.bio.toString()
                userData?.status = currentUser?.status.toString()
                if(!currentUser?.status.isNullOrEmpty()){
                    curUserStatus = true
                }
                userData?.blocked = currentUser?.blocked
                userData?.userPref = currentUser?.userPref
                userData?.viewedStories = currentUser?.viewedStories
                swapChatColors = currentUser?.userPref?.swapChatColors == true
            }
        }
    }

    fun addUserToChatList(userMail: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val userQuery = firebase.collection("users").whereEqualTo("mail", userMail).get().await()
            if (!userQuery.isEmpty) {
                val otherUser = userQuery.documents.first().toObject(UserData::class.java)
                if (otherUser?.userId != userData?.userId) {
                    firebase.collection("users").document(userData?.userId.toString())
                        .update("chatList", FieldValue.arrayUnion(otherUser?.userId.toString()))
                        .await()
                    firebase.collection("users").document(otherUser?.userId.toString())
                        .update("chatList", FieldValue.arrayUnion(userData?.userId.toString())).addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "User Added to Friend List",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadChatListUsers()
                        }
                        .await()
                }
            }else{
                viewModelScope.launch (Dispatchers.Main){
                    Toast.makeText(
                        context,
                        "Given User has not Registered on the App!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun addUserToFavorites(userMail: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val userQuery = firebase.collection("users").whereEqualTo("mail", userMail).get().await()
            if (!userQuery.isEmpty) {
                val otherUser = userQuery.documents.first().toObject(UserData::class.java)
                if (otherUser?.userId != userData?.userId) {
                    firebase.collection("users").document(userData?.userId.toString())
                        .update("favorites", FieldValue.arrayUnion(otherUser?.userId.toString()))
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "User Added to Favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadChatListUsers()
                        }
                        .await()
                }
            }
        }
    }

    fun sendMessage(
        otherUserId: String,
        message: String,
        imageUrl : String ? = null,
        repliedTo: MessageData? = null,
        forwarded: Boolean? = false,
        storyReply: Boolean? = false,
        isVideo: Boolean? = false,
        isFile: Boolean? = false,
        filename: String? = ""
    ) {
        viewModelScope.launch(Dispatchers.IO){
            val currentTime = System.currentTimeMillis()
            val messageData = MessageData(message, userData?.userId.toString(), currentTime, imageUrl, repliedTo = repliedTo, isForwarded = forwarded, storyReply = storyReply, isVideo = isVideo, isFile = isFile, filename = filename)
            firebase.collection("conversations").document(userData?.userId.toString())
                .collection(otherUserId)
                .add(messageData)
                .await()
            firebase.collection("conversations").document(otherUserId)
                .collection(userData?.userId.toString())
                .add(messageData)
                .await()
            uploadingMedia = null
            isUploadingVideo = false
            val latestMessageSenderRef = firebase.collection("latest_messages").document(userData?.userId.toString() + "_" + otherUserId)
            latestMessageSenderRef.set(messageData)

            val latestMessageRecipientRef = firebase.collection("latest_messages").document(otherUserId + "_" + userData?.userId.toString())
            latestMessageRecipientRef.set(messageData)

            if (imageUrl != null && message.isEmpty()) {
                sendNotif("Media")
            } else {
                sendNotif(message)
            }
        }
    }

    fun uploadMediaAndSendMessage(otherUserId: String, message: String, repliedTo: MessageData?, context: Context) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus()
            val isVideo = isUploadingVideo
            val isFile = isUploadingFile
            uploadFileName = fileName.toString()
            if (forwarded != null) {
                if (forwarded?.media != null) {
                    sendMessage(otherUserId, imageUrl = forwarded?.media, message = message, forwarded = forwarded?.isForwarded, isVideo = forwarded?.isVideo, isFile = forwarded?.isFile, filename = forwarded?.filename )
                } else {
                    sendMessage(otherUserId, message = message, repliedTo = repliedTo, forwarded = forwarded?.isForwarded)
                }
                forwarded = null
            } else if (mediaUri != null) {
                uploadingMedia = mediaUri
                val storageRef = Firebase.storage.reference.child("media/${uploadFileName}-${UUID.randomUUID()}")
                val uploadTask = storageRef.putFile(mediaUri!!)
                fileName = uploadFileName
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                    val megabytesTransferred = String.format("%.1f", taskSnapshot.bytesTransferred.toDouble() / 1048576)
                    val megabytesTotal = String.format("%.1f", taskSnapshot.totalByteCount.toDouble() / 1048576)
                    _uploadStatus.value = UploadStatus(progress / 100, megabytesTransferred, megabytesTotal)
                }
                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        sendMessage(otherUserId, imageUrl = imageUrl, message = message, isVideo = isVideo, isFile = isFile, filename = uploadFileName)
                    }
                }
            } else {
                sendMessage(otherUserId, message = message, repliedTo = repliedTo)
            }
        }
    }

    fun getMessagesWithUser() {
        if(isLoadingChat){
            return
        }
        isLoadingChat = true
        viewModelScope.launch (Dispatchers.IO) {
            val messages = firebase.collection("conversations").document(userData?.userId.toString())
                .collection(chattingWith?.userId.toString())
                .orderBy("time")
                .get()
                .await()
            _chatMessages.value = messages.toObjects(MessageData::class.java)
            if(userData?.userPref?.readRecipients==true){
                updateReadStatus()
            }
            isLoadingChat = false
        }
    }

    fun updateReadStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserRef = firebase.collection("conversations")
                .document( chattingWith?.userId.toString())
                .collection(userData?.userId.toString())

            val messagesQuery = currentUserRef
                .whereEqualTo("read", false)
                .get()
                .await()

            for (document in messagesQuery.documents) {
                val messageId = document.id
                val message = document.toObject(MessageData::class.java)
                message?.let {
                    it.read = true
                    currentUserRef.document(messageId).set(it, SetOptions.merge())
                }
            }
        }
    }


    fun startMessageListener() {
        viewModelScope.launch (Dispatchers.IO){
            conversationsListener = firebase.collection("conversations")
                .document(userData?.userId.toString())
                .collection(chattingWith?.userId.toString())
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    getMessagesWithUser()
                }
        }
    }

    fun stopConversationsListener() {
        conversationsListener?.remove()
    }

    fun addBio() {
        viewModelScope.launch(Dispatchers.IO) {
            val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
            userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentUser = documentSnapshot.toObject(UserData::class.java)
                    currentUser?.bio = Bio
                    userData?.bio
                    userDocumentRef.set(currentUser!!)
                }
            }
        }
    }

    fun deleteFriend(friendUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _chatListUsers.value = _chatListUsers.value.filter { it.userId != friendUserId }
            firebase.collection("users").document(userData?.userId.toString())
                .update("chatList", FieldValue.arrayRemove(friendUserId))
                .await()
            firebase.collection("users").document(friendUserId)
                .update("chatList", FieldValue.arrayRemove(userData?.userId.toString()))
                .await()
            loadChatListUsers()
            firebase.collection("conversations").document(userData?.userId.toString())
                .collection(friendUserId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .await()
            firebase.collection("conversations").document(friendUserId)
                .collection(userData?.userId.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .await()

            firebase.collection("latest_messages").document(userData?.userId.toString() + "_" + friendUserId)
                .delete()
                .await()
            firebase.collection("latest_messages").document(friendUserId + "_" + userData?.userId.toString())
                .delete()
                .await()
        }
    }

    fun deleteFavorite(friendUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _favorites.value = _favorites.value.filter { it.userId != friendUserId }
            firebase.collection("users").document(userData?.userId.toString())
                .update("favorites", FieldValue.arrayRemove(friendUserId))
                .await()
            loadChatListUsers()
        }
    }

    fun deleteMessage(otherUserId: String, messageData: MessageData) {
        viewModelScope.launch(Dispatchers.IO) {
            val senderMessageRef = firebase.collection("conversations")
                .document(userData?.userId.toString())
                .collection(otherUserId)
                .whereEqualTo("message", messageData.message)
                .whereEqualTo("time", messageData.time)
                .get()
                .await()

            val receiverMessageRef = firebase.collection("conversations")
                .document(otherUserId)
                .collection(userData?.userId.toString())
                .whereEqualTo("message", messageData.message)
                .whereEqualTo("time", messageData.time)
                .get()
                .await()

            for (document in senderMessageRef.documents) {
                document.reference.delete()
            }
            for (document in receiverMessageRef.documents) {
                document.reference.delete()
            }

            val latestMessageSenderRef = firebase.collection("conversations")
                .document(userData?.userId.toString())
                .collection(otherUserId)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val latestMessageRecipientRef = firebase.collection("conversations")
                .document(otherUserId)
                .collection(userData?.userId.toString())
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val latestMessageSender = latestMessageSenderRef.documents.firstOrNull()?.toObject(MessageData::class.java)
            latestMessageSender?.let {
                firebase.collection("latest_messages")
                    .document(userData?.userId.toString() + "_" + otherUserId)
                    .set(it)
            }

            val latestMessageRecipient = latestMessageRecipientRef.documents.firstOrNull()?.toObject(MessageData::class.java)
            latestMessageRecipient?.let {
                firebase.collection("latest_messages")
                    .document(otherUserId + "_" + userData?.userId.toString())
                    .set(it)
            }

            if(messageData.isForwarded == false){
                messageData.media?.let { imageUrl ->
                    val storageRef = Firebase.storage.getReferenceFromUrl(imageUrl)
                    storageRef.delete()
                }
            }
        }
    }

    fun filterContacts(
        contactList : List<UserData>,
        toSearch : String
    ){
        viewModelScope.launch {
            val filteredList = contactList.filter {
                it.username!!.contains(toSearch,true) || it.mail!!.contains(toSearch, true)
            }
            _searchContacts.emit(filteredList)
        }
    }


    // FCM

    fun getToken(){
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                val token = it.toString()
                userData?.token = token
                val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
                userDocumentRef.update("token", token)
            }
        }
    }

    fun removeToken() {
        viewModelScope.launch (Dispatchers.IO){
            userData?.let { user ->
                val userDocumentRef = firebase.collection("users").document(user.userId.toString())
                userDocumentRef.update("token", FieldValue.delete())
            }
        }
    }


    private fun sendNotif(message: String){
        viewModelScope.launch(Dispatchers.IO) {
            val jsonObject = JSONObject()
            val notificationObject = JSONObject()
            val dataObject = JSONObject()
            notificationObject.put("title",userData?.username.toString())
            notificationObject.put("body",message)
            dataObject.put("userId",userData?.userId.toString())
            jsonObject.put("notification", notificationObject)
            jsonObject.put("data", dataObject)
            jsonObject.put("to", chattingWith?.token)
            callNotifAPI(jsonObject)
        }
    }

    fun editMessage(otherUserId: String, messageTimestamp: Long, newMessage: String, reaction :String ? = null, starred: Boolean? = false) {
        viewModelScope.launch(Dispatchers.IO) {
            isUploadingVideo = false
            val currentUserRef = firebase.collection("conversations")
                .document(userData?.userId.toString())
                .collection(otherUserId)

            val recipientUserRef = firebase.collection("conversations")
                .document(otherUserId)
                .collection(userData?.userId.toString())

            val curUserQuerySnapshot = currentUserRef
                .whereEqualTo("time", messageTimestamp)
                .get()
                .await()

            val otherUserUuerySnapshot = recipientUserRef
                .whereEqualTo("time", messageTimestamp)
                .get()
                .await()

            if (!curUserQuerySnapshot.isEmpty) {
                for (document in curUserQuerySnapshot.documents) {
                    val messageId = document.id
                    currentUserRef.document(messageId).update("message", newMessage)
                    currentUserRef.document(messageId).update("curUserReaction", reaction)
                    currentUserRef.document(messageId).update("starred", starred)
                }
            }
            if (!curUserQuerySnapshot.isEmpty) {
                for (document in otherUserUuerySnapshot) {
                    val messageId = document.id
                    recipientUserRef.document(messageId).update("message", newMessage)
                    recipientUserRef.document(messageId).update("otherUserReaction", reaction)
                    recipientUserRef.document(messageId).update("starred", starred)
                }
            }

            val latestMessageSenderRef = firebase.collection("latest_messages").document(userData?.userId.toString() + "_" + otherUserId)
            val latestMessageSenderQuery = currentUserRef.orderBy("time", Query.Direction.DESCENDING).limit(1).get().await()
            val latestMessageSender = latestMessageSenderQuery.documents.firstOrNull()?.toObject(MessageData::class.java)
            latestMessageSender?.let {
                latestMessageSenderRef.set(it)
            }

            val latestMessageRecipientRef = firebase.collection("latest_messages").document(otherUserId + "_" + userData?.userId.toString())
            val latestMessageRecipientQuery = recipientUserRef.orderBy("time", Query.Direction.DESCENDING).limit(1).get().await()
            val latestMessageRecipient = latestMessageRecipientQuery.documents.firstOrNull()?.toObject(MessageData::class.java)
            latestMessageRecipient?.let {
                latestMessageRecipientRef.set(it)
            }
        }
    }

    fun updateProfilePic() {
        viewModelScope.launch{
            if (mediaUri != null && userData?.userId!!.isNotEmpty()) {
                val storageRef = Firebase.storage.reference.child("profilePics/${userData?.userId}/${UUID.randomUUID()}")
                val allProfilePics = Firebase.storage.reference.child("profilePics/${userData?.userId}")
                allProfilePics.listAll()
                    .addOnSuccessListener { listResult ->
                        listResult.items.forEach { item ->
                            item.delete()
                        }
                    }
                val uploadTask = storageRef.putFile(mediaUri!!)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
                        userDocumentRef.update("profilePictureUrl", downloadUri.toString())
                        profilePicture = downloadUri.toString()
                    }
                }
            }
            mediaUri = null
        }

    }

    fun setStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            if (mediaUri != null && userData?.userId!!.isNotEmpty()) {
                val storageRef = Firebase.storage.reference.child("status/${userData?.userId}/${UUID.randomUUID()}")
                val allStatus = Firebase.storage.reference.child("status/${userData?.userId}")
                allStatus.listAll()
                    .addOnSuccessListener { listResult ->
                        listResult.items.forEach { item ->
                            item.delete()
                        }
                    }
                val uploadTask = storageRef.putFile(mediaUri!!)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        val currentTimeMillis = System.currentTimeMillis()
                        val expirationTimeMillis = currentTimeMillis + 24 * 60 * 60 * 1000
                        val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
                        userDocumentRef.update("status", downloadUri.toString())
                        userDocumentRef.update("statusExpiry", expirationTimeMillis)
                        userData?.status = downloadUri.toString()
                        curUserStatus = true
                        userData?.statusExpiry = expirationTimeMillis
                    }
                }
            }
            mediaUri = null
        }
    }

    fun deleteStatus(otherUserData: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            if(otherUserData == userData){
                curUserStatus = false
            }
            _viewedStatus.value.remove(otherUserData.userId.toString())
            val allStatus = Firebase.storage.reference.child("status/${otherUserData.userId}")
            allStatus.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        item.delete()
                    }
                }
            val userDocumentRef = firebase.collection("users").document(otherUserData.userId.toString())
            userDocumentRef.update("status", null)
            userDocumentRef.update("statusExpiry", null)
            removeViewersOfDeletedStory(otherUserData.userId.toString())
        }
    }

    fun blockUser(userIdToBlock: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _blockedUsers.value += _chatListUsers.value.first { it.userId == userIdToBlock }
            _chatListUsers.value = _chatListUsers.value.filter { it.userId != userIdToBlock }
            _favorites.value = _favorites.value.filter { it.userId != userIdToBlock }
            val currentUserDoc = firebase.collection("users").document(userData?.userId.toString())
            currentUserDoc.update("blocked", FieldValue.arrayUnion(userIdToBlock))
        }
    }

    fun unblockUser(userIdToUnblock: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserDoc = firebase.collection("users").document(userData?.userId.toString())
            currentUserDoc.update("blocked", FieldValue.arrayRemove(userIdToUnblock))
                .addOnSuccessListener {
                    _blockedUsers.value = _blockedUsers.value.filter { it.userId != userIdToUnblock }
                }
        }
    }
    fun updateStatusList(){
        viewModelScope.launch (Dispatchers.IO){
            val userList = mutableListOf<UserData>()
            for(UserData in chatListUsers.value){
                if(UserData.status.toString().isNotEmpty()){
                    if(UserData.statusExpiry!=null){
                        if(UserData.statusExpiry!! > System.currentTimeMillis()){
                            userList.add(UserData)
                        }else{
                            deleteStatus(UserData)
                            _viewedStatus.value.remove(UserData.userId.toString())
                        }
                    }
                }
            }
            _usersWithStatus.value = userList
        }
    }

    fun updateOnlineStatus(status: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
            userDocumentRef.update("online", status)
        }
    }

    fun updateTypingStatus(typing :Boolean? = true) {
        viewModelScope.launch (Dispatchers.IO){
            val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
            if(typing==false){
                userDocumentRef.update("typing", "")
            }
            else if(text.isBlank()){
                userDocumentRef.update("typing", "")
            }else{
                userDocumentRef.update("typing", chattingWith?.userId)
            }
        }
    }

    fun updateEmojiPref(emoji: String){
        viewModelScope.launch(Dispatchers.IO) {
            userData?.let { user ->
                if (user.userPref == null) {
                    user.userPref = UserPref()
                }
                user.userPref?.let { userPref ->
                    if (userPref.recentEmojis == null) {
                        userPref.recentEmojis = mutableListOf()
                    }
                    while (userPref.recentEmojis!!.size >= 6) {
                        userPref.recentEmojis!!.removeAt(0)
                    }
                    userPref.recentEmojis!!.add(emoji)
                }
                val userDocumentRef = firebase.collection("users").document(user.userId.toString())
                userDocumentRef.update("userPref", user.userPref).await()
            }
        }
    }
    fun findRepliedToIndex(time: Long){
        viewModelScope.launch (Dispatchers.Default){
            val ind = chatMessages.value.indexOfFirst { it.time == time }
            if(ind!=-1){
                _repliedToIndex.value = ind
                delay(1000)
                _repliedToIndex.value = null
            }
        }
    }

    fun updateChatPreferences(){
        viewModelScope.launch (Dispatchers.IO) {
            val userDocumentRef = firebase.collection("users").document(userData?.userId.toString())
            userDocumentRef.update("userPref", userData?.userPref).await()
        }
    }

    fun updateStatusViewerList(){
        viewModelScope.launch (Dispatchers.IO){
            val viewerList = mutableListOf<UserData>()
            chatListUsers.value.forEach {
                if(it.viewedStories?.any { it.userId == userData?.userId } == true){
                    viewerList.add(it)
                }
            }
            _storyViewers.value = viewerList
        }
    }

    fun updateStoryView(userId: String, liked :Boolean ? = false) {
        if(updatingStoryView){
            return
        }
        updatingStoryView = true
        viewModelScope.launch (Dispatchers.IO) {
            val existingViewerIndex = userData?.viewedStories?.indexOfFirst { it.userId == userId }
            if (existingViewerIndex == -1 || existingViewerIndex == null) {
                val updatedViewedStories = userData?.viewedStories.orEmpty().toMutableList().apply {
                    add(StoryViewers(userId = userId, liked = liked))
                }
                val updatedUserData = userData?.copy(viewedStories = updatedViewedStories)
                userData = updatedUserData
            } else {
                if(existingViewerIndex.let { userData?.viewedStories?.get(it)?.liked } == false && liked == true){
                    sendNotif("Liked Your Story !")
                }
                val updatedViewedStories = userData?.viewedStories.orEmpty().toMutableList().apply {
                    this[existingViewerIndex] = StoryViewers(userId = userId, liked = liked)
                }
                val updatedUserData = userData?.copy(viewedStories = updatedViewedStories)
                userData = updatedUserData
            }
            val userDocRef = firebase.collection("users").document(userData?.userId.toString())
            userDocRef.update("viewedStories", userData?.viewedStories)
            delay(500)
            updatingStoryView = false
        }
    }

    fun removeViewersOfDeletedStory(deletedStoryUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val storyViewers = _storyViewers.value
            if (storyViewers.isNotEmpty()) {
                firebase.collection("users").whereIn(FieldPath.documentId(), storyViewers.map { it.userId })
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        querySnapshot.documents.forEach { document ->
                            document.id
                            val userData = document.toObject(UserData::class.java)
                            val updatedViewedStories = userData?.viewedStories
                                ?.filterNot { it.userId == deletedStoryUserId }
                                ?: emptyList()
                            document.reference.update("viewedStories", updatedViewedStories)
                        }
                    }
            }
        }
    }

    fun updateSearchMessageList(list: List<MessageData>, message: String){
        viewModelScope.launch (Dispatchers.Default){
            val messageList = chatMessages.value.filter {
                it.message!!.contains(message,true)
            }
            _searchMessages.emit(messageList)
            if(messageList.isNotEmpty()){
                searchListIndex = searchMessages.value.size-1
            }
        }
    }

    fun updateSearchMessageIndex(){
        viewModelScope.launch (Dispatchers.Default){
            if(searchMessages.value.isNotEmpty()){
                searchIndex = chatMessages.value.indexOfFirst { it.time == searchListIndex?.let { it1 ->
                    searchMessages.value[it1].time
                } }
                if(searchIndex == -1){
                    searchIndex = null
                }
            }
        }
    }
}