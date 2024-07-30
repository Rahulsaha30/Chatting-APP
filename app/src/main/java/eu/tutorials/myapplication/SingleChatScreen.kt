package eu.tutorials.myapplication

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController



import eu.tutorials.myapplication.data.Message


@Composable
fun SingleChatScreen(navController: NavController, vm: CAViewModel, chatId: String) {
    // Initialize the image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                vm.onSendImage(chatId, it)
            }
        }
    )

    // Load the chat messages
    LaunchedEffect(Unit) {
        vm.populateChat(chatId)
    }

    // Handle back navigation and chat cleanup
    BackHandler {
        vm.depopulateChat()
        navController.popBackStack()
    }

    // Track reply state
    var reply by rememberSaveable { mutableStateOf("") }
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val myId = vm.userData.value
    val chatUser = if (myId?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    // Send reply action
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }


    val chatMessages = vm.chatMessages.value

    Column(modifier = Modifier.fillMaxSize().background(if (isSystemInDarkTheme()) Color.Black else Color.White)) {
        // Chat header
        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            onBackClicked = { navController.popBackStack()
                vm.depopulateChat()
            }
        )

        // Messages list
        Messages(
            modifier = Modifier.weight(1f),
            chatMessages = chatMessages,
            currentUserId = myId?.userId ?: ""
        )

        // Reply box with image picker
        ReplyBox(
            reply = reply,
            onReplyChange = { reply = it },
            onSendReply = onSendReply,
            onPickImage = { imagePickerLauncher.launch("image/*") }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(name: String, imageUrl: String, onBackClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 3.dp, start = 0.dp, end = 0.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.left),
            contentDescription = null,
            modifier = Modifier
                .clickable { onBackClicked.invoke() }
                .padding(10.dp).size(24.dp)
        )
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
    CommonDivider()
}


@Composable
fun Messages(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp), // Adjust horizontal padding as needed
        contentPadding = PaddingValues(vertical = 8.dp) // Reduce vertical padding
    ) {
        items(chatMessages) { msg ->
            // Check if the message or imageUrl is null or empty and skip rendering if true
            if (!msg.message.isNullOrBlank() || !msg.imageUrl.isNullOrBlank()) {
                val alignment = if (msg.sentBy == currentUserId) Alignment.End else Alignment.Start
                val color = if (msg.sentBy == currentUserId) Color(0xFF187783) else Color(0xFF616161)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp), // Reduce vertical padding between messages
                    horizontalAlignment = alignment
                ) {
                    msg.message?.let {
                        if (it.isNotBlank()) {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .padding(12.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    msg.imageUrl?.let { imageUrl ->
                        if (imageUrl.isNotBlank()) {
                            CommonImage(
                                data = imageUrl,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
    onPickImage: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,
                shape = RoundedCornerShape(30.dp),
                maxLines = 6,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onSendReply,
                modifier = Modifier
                    .size(50.dp)
                    .background(colorResource(id = R.color.black), shape = CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send2),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = onPickImage,
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Black, shape = CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.image_gallery),
                    contentDescription = "Pick Image",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
