package com.example.groupworkmanagement.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.data.model.ATaskChannel
import com.example.groupworkmanagement.ui.components.MemberList
import com.example.groupworkmanagement.ui.components.SingleChatScreen
import com.example.groupworkmanagement.ui.components.TaskChannelList
import com.example.groupworkmanagement.ui.components.utils.CustomFAB
import com.example.groupworkmanagement.ui.components.utils.TaskChannelHeader
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.NavigationItems
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.popUpNavigate
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel
import com.example.groupworkmanagement.viewmodel.TaskChannelViewModel

@Composable
fun TaskChannelScreen(
    navController: NavController,
    home: HomeViewModel,
    group: GroupViewModel,
    channelId: String,
)  {

    val taskChannelVM = hiltViewModel<TaskChannelViewModel>()
    var selectedIndex by remember { mutableStateOf(0) }
    val navHostController = rememberNavController()

    val tabItem = listOf(
        NavigationItems(
            title = "Tin nhắn",
            route = Destination.TaskHome.route,
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        ),
        NavigationItems(
            title = "Thành viên",
            route = Destination.TaskMember.route,
            selectedIcon = Icons.Filled.Groups2,
            unselectedIcon = Icons.Outlined.Groups2,
        ),
        NavigationItems(
            title = "",
            route = Destination.TaskAssignment.route,
            selectedIcon = Icons.Filled.Business,
            unselectedIcon = Icons.Outlined.Business,
        ),
//        NavigationItems(
//            title = "",
//            route = Destination.TaskCalendar.route,
//            selectedIcon = Icons.Filled.CalendarMonth,
//            unselectedIcon = Icons.Outlined.CalendarMonth,
//        ),
    )

    LaunchedEffect(key1 = Unit) {
        taskChannelVM.getChannel(channelId)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            taskChannelVM.deactivateListener()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TaskChannelHeader(
            channel = taskChannelVM.taskChannelInstance.value?:ATaskChannel(),
            onBackHandClick = {
                navController.popBackStack()
            },
            addMember = {
                navController.navigate(Destination.TaskAddMember.route)
            },
            addTask = {
                navController.navigate(Destination.TaskAssignment.route)
            })
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
        NavHost(navController = navHostController, startDestination = Destination.TaskHome.route) {
            composable(Destination.TaskHome.route) {
                home.currentUser.value?.let {currentUser->
                    SingleChatScreen(
                        currentUserId = currentUser.uid,
                        messageList = taskChannelVM.listMessage.value,
                        onSend = {msg ->
                            taskChannelVM.sendMessage(msg = msg, user = currentUser)
                        }
                    )
                }
            }
            composable(Destination.TaskMember.route) {
                val showDialog = remember { mutableStateOf(false) }
                val deleteId = remember { mutableStateOf<AMember?>(null) }
                if (!showDialog.value) {
                    MemberList(memList = taskChannelVM.memberList.value,
                        haveActionClick = true,
                        onActionClick = {
                            popUpNavigate(navHostController, Destination.TaskAddMember.route)
                        },
                        onMemberClick = {mem ->
                            showDialog.value = true
                            deleteId.value = mem
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
                            taskChannelVM.removeMember(mem.user.uid)
                        }
                        deleteId.value = null
                        showDialog.value = false
                    }
                }
            }
            composable(Destination.TaskAssignment.route) {
                TaskChannelList(vm = taskChannelVM, group = group, navController = navHostController)
            }
            composable(Destination.TaskAddMember.route) {
                taskChannelVM.insertList.value = AddMemberScreenGroup(
                    list = group.memberList.value
                        .map {it.user}
                        .filter {grmem ->
                            grmem !in taskChannelVM.memberList.value.map { it.user }
                                    && grmem.uid != taskChannelVM.auth.currentUser?.uid
                        },
                    onDismiss = {
                        navHostController.popBackStack()
                    },
                    onConfirm = {
                        taskChannelVM.insertMember(taskChannelVM.insertList.value)
                        navHostController.popBackStack()
                    }
                )
            }
            composable(Destination.TaskAddSingleMember.route) {
                taskChannelVM.insertMem.value = AddSingleMemberScreenGroup(
                    list = taskChannelVM.memberList.value.map { it.user },
                    onDismiss = { navHostController.popBackStack()},
                    onConfirm = {
                        taskChannelVM.insertMember(taskChannelVM.insertList.value)
                        navHostController.popBackStack()
                    })
            }
            composable(Destination.TaskCalendar.route) {}
        }
    }
}
