package com.example.groupworkmanagement.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.Group
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.ui.components.MemberList
import com.example.groupworkmanagement.ui.components.SingleChatScreen
import com.example.groupworkmanagement.ui.components.utils.ChannelHeader
import com.example.groupworkmanagement.ui.components.utils.CustomFAB
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.NavigationItems
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.popUpNavigate
import com.example.groupworkmanagement.viewmodel.ChannelViewModel
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel

@Composable
fun ChannelScreen(
    navController: NavController,
    home: HomeViewModel,
    group: GroupViewModel,
    chatId: String,
    isPublic: Boolean,
) {
    val channelVM = hiltViewModel<ChannelViewModel>()
    var selectedIndex by remember { mutableStateOf(0) }
    val navHostController = rememberNavController()

    LaunchedEffect(key1 = Unit) {
        channelVM.groupInstance.value = group.groupInstance.value
        channelVM.getChannel(chatId, isPublic)
    }

    BackHandler {
        channelVM.deactivateListener()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            channelVM.deactivateListener()
        }
    }

    val tabItem = listOf(
        NavigationItems(
            title = "Tin nhắn",
            route = Destination.ChannelHome.route,
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        ),
        NavigationItems(
            title = "Thành viên",
            route = Destination.ChannelMember.route,
            selectedIcon = Icons.Filled.Groups2,
            unselectedIcon = Icons.Outlined.Groups2,
        ),
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ChannelHeader(
            channel = channelVM.channelInstance.value?: AChannel(),
            addMember = {},
            onBackHandClick = {
                navController.popBackStack()
            },
            haveMemberAdd = false
            )
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.height(50.dp)
        ) {
            tabItem.forEachIndexed { index, item ->
                Tab(
                    selected = (index == selectedIndex),
                    onClick = {
                        selectedIndex = index
                        navHostController.navigate(item.route)
                    },
                    icon = {
                        Icon(imageVector =
                        if (selectedIndex == index) item.selectedIcon
                        else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = fontColor
                        )
                    },
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
        ) {
            NavHost(navController = navHostController, startDestination = Destination.ChannelHome.route ) {
                composable(Destination.ChannelHome.route) {
                    home.currentUser.value?.let { currentUser ->
                        SingleChatScreen(
                            currentUserId = currentUser.uid,
                            messageList = channelVM.listMessage.value,
                            onSend = {msg ->
                                    channelVM.sendMessage(msg, currentUser)
                            }
                        )
                    }
                }
                composable(Destination.ChannelMember.route) {
                    val showDialog = remember { mutableStateOf(false) }
                    val deleteId = remember { mutableStateOf<AMember?>(null) }
                    if (!showDialog.value) {
                        MemberList(memList = channelVM.memberList.value,
                            haveActionClick = !isPublic,
                            onActionClick = {
                                if (!isPublic)
                                    popUpNavigate(navHostController, Destination.ChannelAddMember.route)
                            },
                            onMemberClick = {mem ->
                                if (!isPublic) {
                                    showDialog.value = true
                                    deleteId.value = mem
                                }
                            }
                        )
                    } else {
                        CustomFAB(
                            title = deleteId.value?.user?.name?: "",
                            onDismiss = {
                                showDialog.value = false
                                deleteId.value = null
                            }) {
                            deleteId.value?.let { mem ->
                                channelVM.removeMember(mem.user.uid)
                            }
                            deleteId.value = null
                            showDialog.value = false
                        }
                    }
                }
                composable(Destination.ChannelAddMember.route) {
                    channelVM.insertList.value = AddMemberScreenGroup(
                        list = group.memberList.value
                            .map { it.user }
                            .filter { grmem ->
                                grmem !in channelVM.memberList.value.map { it.user }
                                        && grmem.uid != channelVM.auth.currentUser?.uid
                            },
                        onDismiss = {
                                    navHostController.popBackStack()
                        },
                        onConfirm = {
                            channelVM.insertMember(channelVM.insertList.value)
                            navHostController.popBackStack()
                        }
                    )
                }
            }
        }

    }
}

