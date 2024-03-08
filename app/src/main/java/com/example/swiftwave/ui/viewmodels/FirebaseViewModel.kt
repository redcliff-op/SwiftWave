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
import com.example.swiftwave.data.remote.callNotifAPI
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.UUID

class FirebaseViewModel(
    val userData: UserData
) : ViewModel() {

    var chattingWith by mutableStateOf<UserData?>(null)
    var text by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)
    var imageString by mutableStateOf("")
    var newUser by mutableStateOf("")
    var Bio by mutableStateOf("")
    var selectedMessage by mutableStateOf<MessageData?>(null)
    var searchContact by mutableStateOf("")
    var profilePicture by mutableStateOf("")
    var curStatus by mutableStateOf("")
    var sentBy by mutableStateOf("")
    var imageDialogProfilePicture by mutableStateOf("")

    private var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _chatListUsers = MutableStateFlow<List<UserData>>(emptyList())
    val chatListUsers: StateFlow<List<UserData>> = _chatListUsers
    private val _favorites = MutableStateFlow<List<UserData>>(emptyList())
    val favorites : StateFlow<List<UserData>> = _favorites
    private val _searchContacts = MutableStateFlow<List<UserData>>(emptyList())
    val searchContacts : StateFlow<List<UserData>> = _searchContacts
    private var conversationsListener: ListenerRegistration? = null
    val _viewedStatus = MutableStateFlow<MutableList<String?>>(emptyList<String>().toMutableList())
    val viewedStatus : StateFlow<MutableList<String?>> get() = _viewedStatus.asStateFlow()

    init {
        addUserToFirestore(userData)
        getToken()
        viewModelScope.launch {
            delay(5000)
            setupLatestMessageListener()
        }
    }

    fun loadChatListUsers() {
        viewModelScope.launch {
            val chatListUsers = mutableListOf<UserData>()
            val favorites = mutableListOf<UserData>()
            val userQuery = firebase.collection("users").document(userData.userId.toString()).get().await()
            val currentUser = userQuery.toObject(UserData::class.java)
            if (currentUser != null) {
                for (userId in currentUser.chatList!!) {
                    val friendQuery = firebase.collection("users").document(userId).get().await()
                    val friend = friendQuery.toObject(UserData::class.java)
                    val latestMessageQuery = firebase.collection("conversations")
                        .document(userData.userId.toString())
                        .collection(userId)
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .await()
                    val latestMessage = latestMessageQuery.toObjects(MessageData::class.java).firstOrNull()
                    friend?.latestMessage = latestMessage
                    friend?.let { chatListUsers.add(it) }
                }
                for (userId in currentUser.favorites!!) {
                    val friendQuery = firebase.collection("users").document(userId).get().await()
                    val friend = friendQuery.toObject(UserData::class.java)
                    val latestMessageQuery = firebase.collection("conversations")
                        .document(userData.userId.toString())
                        .collection(userId)
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .await()
                    val latestMessage = latestMessageQuery.toObjects(MessageData::class.java).firstOrNull()
                    friend?.latestMessage = latestMessage
                    friend?.let { favorites.add(it) }
                }
            }
            _chatListUsers.value = chatListUsers
            _favorites.value = favorites
        }
    }

    fun setupLatestMessageListener() {
        chatListUsers.value.forEach { user ->
            firebase.collection("conversations").document(userData.userId.toString())
                .collection(user.userId.toString())
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    loadChatListUsers()
                }
        }
    }

    private fun addUserToFirestore(user: UserData) {
        viewModelScope.launch{
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
                userData.bio = currentUser?.bio.toString()
                profilePicture = currentUser?.profilePictureUrl.toString()
                Bio = currentUser?.bio.toString()
                userData.status = currentUser?.status.toString()
                curStatus = currentUser?.status.toString()
            }
        }
    }

    fun addUserToChatList(userMail: String, context: Context) {
        viewModelScope.launch {
            val userQuery = firebase.collection("users").whereEqualTo("mail", userMail).get().await()
            if (!userQuery.isEmpty) {
                val otherUser = userQuery.documents.first().toObject(UserData::class.java)
                if (otherUser?.userId != userData.userId) {
                    firebase.collection("users").document(userData.userId.toString())
                        .update("chatList", FieldValue.arrayUnion(otherUser?.userId.toString()))
                        .await()
                    firebase.collection("users").document(otherUser?.userId.toString())
                        .update("chatList", FieldValue.arrayUnion(userData.userId.toString())).addOnSuccessListener {
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
                Toast.makeText(
                    context,
                    "Given User has not Registered on the App!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addUserToFavorites(userMail: String, context: Context) {
        viewModelScope.launch {
            val userQuery = firebase.collection("users").whereEqualTo("mail", userMail).get().await()
            if (!userQuery.isEmpty) {
                val otherUser = userQuery.documents.first().toObject(UserData::class.java)
                if (otherUser?.userId != userData.userId) {
                    firebase.collection("users").document(userData.userId.toString())
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

    fun sendMessage(otherUserId: String, message: String, imageUrl : String ? = null) {
        viewModelScope.launch{
            val currentTime = System.currentTimeMillis()
            val messageData = MessageData(message, userData.userId.toString(), currentTime, imageUrl)
            firebase.collection("conversations").document(userData.userId.toString())
                .collection(otherUserId)
                .add(messageData)
                .await()

            firebase.collection("conversations").document(otherUserId)
                .collection(userData.userId.toString())
                .add(messageData)
                .addOnSuccessListener {
                    if (imageUrl!=null && message.isEmpty()) {
                        sendNotif("Image")
                    }else{
                        sendNotif(message)
                    }
                }
                .await()
        }
    }

    fun uploadImageAndSendMessage(otherUserId: String, message: String) {
        if (imageUri != null) {
            val storageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
            val uploadTask = storageRef.putFile(imageUri!!)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    sendMessage(otherUserId, imageUrl = imageUrl, message = message)
                }
            }
        } else {
            sendMessage(otherUserId, message = message)
        }
    }

    val _chatMessages = MutableStateFlow<List<MessageData>>(emptyList())
    val chatMessages: StateFlow<List<MessageData>> = _chatMessages

    fun getMessagesWithUser() {
        viewModelScope.launch {
            val messages = firebase.collection("conversations").document(userData.userId.toString())
                .collection(chattingWith?.userId.toString())
                .orderBy("time")
                .get()
                .await()
            _chatMessages.value = messages.toObjects(MessageData::class.java)
        }
    }

    fun startMessageListener() {
        conversationsListener = firebase.collection("conversations")
            .document(userData.userId.toString())
            .collection(chattingWith?.userId.toString())
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                getMessagesWithUser()
            }
    }

    fun stopConversationsListener() {
        conversationsListener?.remove()
    }

    fun addBio() {
        viewModelScope.launch {
            val userDocumentRef = firebase.collection("users").document(userData.userId.toString())
            userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentUser = documentSnapshot.toObject(UserData::class.java)
                    currentUser?.bio = Bio
                    userData.bio
                    userDocumentRef.set(currentUser!!)
                }
            }
        }
    }

    fun deleteFriend(friendUserId: String) {
        viewModelScope.launch {
            _chatListUsers.value = _chatListUsers.value.filter { it.userId != friendUserId }
            firebase.collection("users").document(userData.userId.toString())
                .update("chatList", FieldValue.arrayRemove(friendUserId))
                .await()
            firebase.collection("users").document(friendUserId)
                .update("chatList", FieldValue.arrayRemove(userData.userId.toString()))
                .await()
            loadChatListUsers()
            firebase.collection("conversations").document(userData.userId.toString())
                .collection(friendUserId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .await()
            firebase.collection("conversations").document(friendUserId)
                .collection(userData.userId.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .await()
        }
    }

    fun deleteFavorite(friendUserId: String) {
        viewModelScope.launch {
            _favorites.value = _favorites.value.filter { it.userId != friendUserId }
            firebase.collection("users").document(userData.userId.toString())
                .update("favorites", FieldValue.arrayRemove(friendUserId))
                .await()
            loadChatListUsers()
        }
    }

    fun deleteMessage(otherUserId: String, messageData: MessageData) {
        viewModelScope.launch {
            val senderMessageRef = firebase.collection("conversations")
                .document(userData.userId.toString())
                .collection(otherUserId)
                .whereEqualTo("message", messageData.message)
                .whereEqualTo("time", messageData.time)
                .get()
                .await()

            val receiverMessageRef = firebase.collection("conversations")
                .document(otherUserId)
                .collection(userData.userId.toString())
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

            messageData.image?.let { imageUrl ->
                val storageRef = Firebase.storage.getReferenceFromUrl(imageUrl)
                storageRef.delete()
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

    private fun getToken(){
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                val token = it.toString()
                userData.token = token
                val userDocumentRef = firebase.collection("users").document(userData.userId.toString())
                userDocumentRef.update("token", token)
            }
        }
    }

    private fun sendNotif(message: String){
        viewModelScope.launch {
            val jsonObject = JSONObject()
            val notificationObject = JSONObject()
            val dataObject = JSONObject()
            notificationObject.put("title",userData.username.toString())
            notificationObject.put("body",message)
            dataObject.put("userId",userData.userId.toString())
            jsonObject.put("notification", notificationObject)
            jsonObject.put("data", dataObject)
            jsonObject.put("to", chattingWith?.token)
            callNotifAPI(jsonObject)
        }
    }

    fun editMessage(otherUserId: String, messageTimestamp: Long, newMessage: String) {
        viewModelScope.launch {
            val currentUserRef = firebase.collection("conversations")
                .document(userData.userId.toString())
                .collection(otherUserId)

            val recipientUserRef = firebase.collection("conversations")
                .document(otherUserId)
                .collection(userData.userId.toString())

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
                }
            }
            if (!curUserQuerySnapshot.isEmpty) {
                for (document in otherUserUuerySnapshot) {
                    val messageId = document.id
                    recipientUserRef.document(messageId).update("message", newMessage)
                }
            }
        }
    }

    fun updateProfilePic() {
        viewModelScope.launch {
            if (imageUri != null && userData.userId!!.isNotEmpty()) {
                val storageRef = Firebase.storage.reference.child("profilePics/${userData.userId}/${UUID.randomUUID()}")
                val allProfilePics = Firebase.storage.reference.child("profilePics/${userData.userId}")
                allProfilePics.listAll()
                    .addOnSuccessListener { listResult ->
                        listResult.items.forEach { item ->
                            item.delete()
                        }
                    }
                val uploadTask = storageRef.putFile(imageUri!!)
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
                        val userDocumentRef = firebase.collection("users").document(userData.userId.toString())
                        userDocumentRef.update("profilePictureUrl", downloadUri.toString())
                        profilePicture = downloadUri.toString()
                    }
                }
            }
            imageUri = null
        }

    }

    fun setStatus() {
        viewModelScope.launch {
            if (imageUri != null && userData.userId!!.isNotEmpty()) {
                val storageRef = Firebase.storage.reference.child("status/${userData.userId}/${UUID.randomUUID()}")
                val allStatus = Firebase.storage.reference.child("status/${userData.userId}")
                allStatus.listAll()
                    .addOnSuccessListener { listResult ->
                        listResult.items.forEach { item ->
                            item.delete()
                        }
                    }
                val uploadTask = storageRef.putFile(imageUri!!)
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
                        val userDocumentRef = firebase.collection("users").document(userData.userId.toString())
                        userDocumentRef.update("status", downloadUri.toString())
                        userDocumentRef.update("statusExpiry", expirationTimeMillis)
                        userData.status = downloadUri.toString()
                        curStatus = downloadUri.toString()
                        userData.statusExpiry = expirationTimeMillis
                    }
                }
            }
            imageUri = null
        }
    }

    fun deleteStatus(otherUserData: UserData) {
        viewModelScope.launch {
            if(otherUserData == userData){
                curStatus = ""
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
        }
    }

}