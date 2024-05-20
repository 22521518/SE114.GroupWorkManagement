package com.example.groupworkmanagement.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.groupworkmanagement.ui.components.utils.ChatHeader
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.viewmodel.HomeViewModel

@Composable
fun HomeChatRoom(
    home: HomeViewModel,
    navController: NavController,
    chatRoomId: String,
) {
    LaunchedEffect(key1 = Unit) {
        home.getChatRoom(chatRoomId)
    }
    BackHandler {
        home.deactivateChatRoom()
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            home.deactivateChatRoom()
        }
    }
    Column {
        ChatHeader(chatter = home.currentChatRoom.value!!.user2,
            onBackHandClick = {
                navController.popBackStack()
            })
        home.currentUser.value?.let {currentUser ->
            SingleChatScreen(
                currentUserId = currentUser.uid,
                messageList = home.messList.value,
                onSend = {msg ->
                    home.sendMessage(roomId = chatRoomId, msg = msg)
                })
        }
    }
}