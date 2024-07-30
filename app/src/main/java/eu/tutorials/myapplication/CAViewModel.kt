package eu.tutorials.myapplication

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.tutorials.myapplication.data.COLLECTION_CHAT
import eu.tutorials.myapplication.data.COLLECTION_MESSAGES
import eu.tutorials.myapplication.data.COLLECTION_STATUS
import eu.tutorials.myapplication.data.COLLECTION_USER
import eu.tutorials.myapplication.data.ChatData
import eu.tutorials.myapplication.data.ChatUser
import eu.tutorials.myapplication.data.Event
import eu.tutorials.myapplication.data.Message
import eu.tutorials.myapplication.data.Status
import eu.tutorials.myapplication.data.UserData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CAViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage:FirebaseStorage
): ViewModel() {


    val inProgress = mutableStateOf(false)
    val popupNotification = mutableStateOf<Event<String>?>(null)

    val signedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val inProgressChats = mutableStateOf(false)

    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessages = mutableStateOf(false)
    var currentChatMessagesListener: ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)


    init {
        //    onLogout()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getuserData(uid)
        }
    }

    fun onSignup(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill in all fields")
            return
        }
        inProgress.value = true
        db.collection(COLLECTION_USER).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                //auth
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signedIn.value = true
                                //create user profile
                                createorUpdateProfile(name = name, number = number)
                            } else
                                handleException(task.exception, "Signup failed")
                        }
                else
                    handleException(customMessage = "number already exists")
                inProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill in all fields")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        getuserData(uid)
                    }
                } else
                    handleException(task.exception, "Login failed")
            }
            .addOnFailureListener {
                handleException(it, "Login failed")
            }
    }

    fun onForgotPassword(email: String) {
        if (email.isEmpty()) {
            handleException(customMessage = "Please enter your email")
            return
        }
        inProgress.value = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    popupNotification.value = Event("Password reset email sent")
                } else {
                    handleException(task.exception, "Failed to send reset email")
                }
                inProgress.value = false
            }
            .addOnFailureListener { exception ->
                handleException(exception, "Failed to send reset email")
                inProgress.value = false
            }
    }


    private fun createorUpdateProfile(
        name: String? = null, number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val userdata = UserData(
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            userId = uid,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )
        uid?.let { uid ->
            inProgress.value = true
            db.collection(COLLECTION_USER).document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        //update user
                        it.reference.update(userdata.toMap()).addOnSuccessListener {
                            inProgress.value = false
                        }.addOnFailureListener {
                            handleException(it, "Cannot update user")
                        }
                    } else {
                        //create user
                        db.collection(COLLECTION_USER).document(uid).set(userdata)
                        inProgress.value = false
                        getuserData(uid)
                    }
                }
                .addOnFailureListener {
                    handleException(it, "cannot retrieve data")
                }
        }
    }

    fun updateProfileData(name: String, number: String) {
        createorUpdateProfile(name = name, number = number)
    }

    private fun getuserData(uid: String) {
        inProgress.value = true
        db.collection(COLLECTION_USER).document(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Cannot retrieve user data")
                }
                if (value != null) {
                    val user = value.toObject<UserData>()
                    userData.value = user
                    inProgress.value = false
                    populateChats()
                    populateStatuses()
                }
            }
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged Out")
        chats.value = listOf()

    }


    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("ChatAppClone", "Chat app exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage $errorMsg"
        popupNotification.value = Event(message)
        inProgress.value = false
    }

    private fun uploadImage(uri: Uri, onsuccess: (Uri) -> Unit) {
        inProgress.value = true

        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onsuccess)
            inProgress.value = false
        }.addOnFailureListener {
            handleException()
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createorUpdateProfile(imageUrl = it.toString())
        }
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly())
            handleException(customMessage = "Please enter a valid number")
        else {
            db.collection(COLLECTION_USER).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number),
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        db.collection(COLLECTION_USER).whereEqualTo("number", number)
                            .get().addOnSuccessListener {
                                if (it.isEmpty) {
                                    handleException(customMessage = "cannot retrieve user with number$number")
                                } else {
                                    val chatPartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(COLLECTION_CHAT).document().id
                                    val chat = ChatData(
                                        id,
                                        ChatUser(
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.number,
                                            userData.value?.imageUrl
                                        ),
                                        ChatUser(
                                            chatPartner.userId,
                                            chatPartner.name,
                                            chatPartner.number,
                                            chatPartner.imageUrl
                                        )
                                    )
                                    db.collection(COLLECTION_CHAT).document(id).set(chat)
                                }
                            }.addOnFailureListener { handleException(it) }
                    } else {
                        handleException(customMessage = "Chat already exists")
                    }
                }

        }
    }

    private fun populateChats() {
        inProgressChats.value = true
        db.collection(COLLECTION_CHAT).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null)
                handleException(error)
            if (value != null)
                chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
            inProgressChats.value = false
        }
    }

    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message, time)
        db.collection(COLLECTION_CHAT).document(chatId).collection(COLLECTION_MESSAGES)
            .document()
            .set(msg)
    }

    fun populateChat(chatId: String) {
        inProgressChatMessages.value = true
        currentChatMessagesListener =
            db.collection(COLLECTION_CHAT).document(chatId).collection(COLLECTION_MESSAGES)
                .addSnapshotListener { value, error ->
                    if (error != null)
                        handleException(error)
                    if (value != null)
                        chatMessages.value = value.documents.mapNotNull {
                            it.toObject<Message>()
                        }.sortedBy { it.timestamp }
                    inProgressChatMessages.value = false
                }
    }

    fun depopulateChat() {
        chatMessages.value = listOf()
        currentChatMessagesListener = null
    }


    private fun createStatus(imageUrl: String?) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.number,
                userData.value?.imageUrl
            ),
            imageUrl,
            System.currentTimeMillis()
        )
        db.collection(COLLECTION_STATUS).document().set(newStatus)
    }

    fun uploadStatus(imageUri: Uri) {
        uploadImage(imageUri) {
            createStatus(imageUrl = it.toString())
        }
    }

    private fun populateStatuses() {
        inProgressStatus.value = true
        val milliTimeData = 24L * 60 * 60 * 1000
        val cutoff = System.currentTimeMillis() - milliTimeData

        db.collection(COLLECTION_CHAT).where(
            Filter.or(
                Filter.equalTo("user1.userid", userData.value?.userId),
                Filter.equalTo("user2.userid", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null)
                handleException(error)
            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chats ->
                    if (chats.user1.userId == userData.value?.userId)
                        currentConnections.add(chats.user2.userId)
                    else
                        currentConnections.add(chats.user1.userId)
                }
                db.collection(COLLECTION_STATUS).whereGreaterThan("timestamp", cutoff)
                    .whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if (error != null)
                            handleException(error)
                        if (value != null)
                            status.value = value.toObjects()
                        inProgressStatus.value = false
                    }
            }

        }

    }
    fun onSendImage(chatId: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                inProgress.value = true
                val imageUrl = uploadImageToStorage(imageUri)
                val time = Calendar.getInstance().time.toString()
                val msg = Message(userData.value?.userId,timestamp = time, imageUrl = imageUrl)
                db.collection(COLLECTION_CHAT).document(chatId).collection(COLLECTION_MESSAGES)
                    .document()
                    .set(msg)
                inProgress.value = false
            } catch (e: Exception) {
                handleException(e, "Failed to send image")
            }
        }
    }

    private suspend fun uploadImageToStorage(uri: Uri): String {
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }
}
