package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AChatRoom
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.UserCard
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.viewmodel.HomeViewModel

@Composable
fun PersonalRecentChatList(
    home: HomeViewModel,
    navController: NavController
) {
    val list = mutableStateOf(home.chatList.value)
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Destination.HomeAddChat.route)
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier =  Modifier.padding(40.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if(list.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomText(text = "No Chats Available")
                }
            } else {
                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    items(list.value) {chat ->
                        val other = if (chat.user1.uid == home.currentUser.value?.uid) chat.user2 else chat.user1
                        Row (modifier = Modifier.fillMaxWidth().padding(4.dp).clickable {
                            navController.navigate(Destination.HomeChatRoom.createRoute(chat.roomId)) {
                                popUpTo(Destination.HomeChatRoom.createRoute(chat.roomId))
                                launchSingleTop = true
                            }
                            home.currentChatRoom.value = chat
                        }) {
                            UserCard(user = other, imageSize = 60.dp)
                        }
                    }
                }
            }
        }
    }
}