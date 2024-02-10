package com.example.swiftwave.ui.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swiftwave.auth.UserData
import com.example.swiftwave.data.model.MessageData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseViewModel(
    val userData: UserData
) : ViewModel() {

    var chattingWith by mutableStateOf<UserData?>(null)
    var text by mutableStateOf("")
    var newUser by mutableStateOf("")
    var Bio by mutableStateOf("")

    private var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _chatListUsers = MutableStateFlow<List<UserData>>(emptyList())
    val chatListUsers: StateFlow<List<UserData>> = _chatListUsers

    private var conversationsListener: ListenerRegistration? = null

    init {
        addUserToFirestore(userData)
    }

    fun loadChatListUsers() {
        viewModelScope.launch {
            val chatListUsers = mutableListOf<UserData>()
            val userQuery = firebase.collection("users").document(userData.userId.toString()).get().await()
            val currentUser = userQuery.toObject(UserData::class.java)

            if (currentUser != null) {
                for (userId in currentUser.chatList!!) {
                    val friendQuery = firebase.collection("users").document(userId).get().await()
                    val friend = friendQuery.toObject(UserData::class.java)
                    friend?.let { chatListUsers.add(it) }
                }
            }
            _chatListUsers.value = chatListUsers
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
                Bio = currentUser?.bio.toString()
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


    fun sendMessage(otherUserId: String, message: String) {
        viewModelScope.launch{
            val currentTime = System.currentTimeMillis()
            val messageData = MessageData(message, userData.userId.toString(), currentTime)
            firebase.collection("conversations").document(userData.userId.toString())
                .collection(otherUserId)
                .add(messageData)
                .await()

            firebase.collection("conversations").document(otherUserId)
                .collection(userData.userId.toString())
                .add(messageData)
                .await()
        }
    }

    private val _chatMessages = MutableStateFlow<List<MessageData>>(emptyList())
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
                    Log.e(TAG, "Error fetching messages", error)
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
}