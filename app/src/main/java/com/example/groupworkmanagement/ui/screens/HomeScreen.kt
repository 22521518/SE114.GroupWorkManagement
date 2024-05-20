package com.example.groupworkmanagement.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupworkmanagement.MainViewModel
import com.example.groupworkmanagement.ui.components.GroupList
import com.example.groupworkmanagement.ui.components.HomeChatRoom
import com.example.groupworkmanagement.ui.components.PersonalProfile
import com.example.groupworkmanagement.ui.components.PersonalRecentChatList
import com.example.groupworkmanagement.ui.components.PersonalTaskList
import com.example.groupworkmanagement.ui.components.utils.CommonDivider
import com.example.groupworkmanagement.ui.components.utils.CommonHeader
import com.example.groupworkmanagement.ui.components.utils.CommonProgressBar
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.HomeHeader
import com.example.groupworkmanagement.ui.components.utils.ImageAvatar
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.NavigationItems
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.utils.robotoExtremeBold
import com.example.groupworkmanagement.viewmodel.AuthViewModel
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    main: MainViewModel,
    auth: AuthViewModel,
    showToast: (msg: String) -> Unit,
    rootNavController: NavController
) {
    if (auth.auth.currentUser == null) rootNavController.navigate(Destination.LoginScreen.route)

    val home = hiltViewModel<HomeViewModel>()
    val groupVM = hiltViewModel<GroupViewModel>()
    val navController = rememberNavController()
    val homeTitle = remember { mutableStateOf("") }

    val isProcessing = home.processing.value
    val navigationItems = listOf(
        NavigationItems(
            title = "Group",
            route = Destination.HomeGroup.route,
            selectedIcon = Icons.Filled.Group,
            unselectedIcon =  Icons.Outlined.Group
        ),
        NavigationItems(
            title = "Chat",
            route = Destination.HomeChat.route,
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon =  Icons.Outlined.ChatBubbleOutline
        ),
        NavigationItems(
            title = "Task",
            route = Destination.HomeTask.route,
            selectedIcon = Icons.Filled.Task,
            unselectedIcon =  Icons.Outlined.Task
        ),
//        NavigationItems(
//            title = "Calendar",
//            route = Destination.HomeCalendar.route,
//            selectedIcon = Icons.Filled.DateRange,
//            unselectedIcon =  Icons.Outlined.DateRange
//        ),
        NavigationItems(
            title = "Me",
            route = Destination.HomeMe.route,
            selectedIcon = Icons.Filled.AccountCircle,
            unselectedIcon =  Icons.Outlined.AccountCircle
        )
    )
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(key1 =  Unit) {
        home.activateListener()
    }
    if (!isProcessing) {
        var isGroup = remember { mutableStateOf(true) }
        Scaffold (
            bottomBar = {
                if (isGroup.value)
                    NavigationBar {
                    navigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.route)
                            },
                            label = { CustomText(item.title) },
                            icon = { BadgedBox(badge = { if(false) Badge() }) {
                                Icon(imageVector =
                                if (index == selectedItemIndex) item.selectedIcon
                                else item.unselectedIcon,
                                    contentDescription = item.title,
                                    tint = fontColor
                                )
                            }
                            }
                        )
                    }
                }
            }
        ) {
            Column (
                modifier = Modifier.padding(it)
            ) {

                NavHost(navController = navController, startDestination = Destination.HomeGroup.route) {
                    composable(Destination.HomeGroup.route) {
                        homeTitle.value = "Nhóm"
                        Column {
                            HomeHeader(title = homeTitle.value, home = home)
                            GroupList(navController = navController, homeVM = home)
                        }
                    }
                    composable(Destination.GroupRoom.route) {
                        val id = it.arguments?.getString("id")
                        id?.let {
                            GroupScreen(main = main, home = home, groupVM = groupVM, rootNavController = navController, groupId = id, showGroup = isGroup)
                        }
                    }
                    composable(Destination.HomeAddChat.route) {
                        home.insertMem.value = AddSingleMemberScreenGroup(
                            title = "Thêm",
                            list = main.allUserList.value.filter { it.uid != home.currentUser.value?.uid },
                            onDismiss = {
                                navController.popBackStack()
                            }) {
                            CoroutineScope(Dispatchers.IO).launch {
                                home.addChat(home.insertMem.value?.uid!!)
                            }
                            navController.popBackStack()
                        }
                    }
                    composable(Destination.HomeAdd.route) {
                        home.insertList.value = AddMemberScreenGroup(
                            clearList = false,
                            list = main.allUserList.value.filter { it.uid != home.currentUser.value?.uid },
                            onDismiss = {
                                navController.popBackStack()
                            },
                            onConfirm = {
                                home.insertList.value = home.insertList.value.filter { it.uid != home.currentUser.value?.uid }
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(Destination.HomeChat.route) {
                        homeTitle.value = "Tin nhắn"
                        Column {
                            HomeHeader(title = homeTitle.value, home = home)
                            PersonalRecentChatList(home = home, navController = navController)
                        }
                    }
                    composable(Destination.HomeChatRoom.route) {
                        it.arguments?.getString("id")?.let {id->
                            HomeChatRoom(home = home, navController = navController, chatRoomId = id)
                        }
                    }
                    composable(Destination.HomeTask.route) {
                        homeTitle.value = "Tác vụ"
                        Column {
                            HomeHeader(title = homeTitle.value, home = home)
                            PersonalTaskList(home = home)
                        }
                    }
                    composable(Destination.HomeCalendar.route) {

                    }
                    composable(Destination.HomeMe.route) {
                        homeTitle.value = "Hồ sơ"
                        Column {
                            HomeHeader(title = homeTitle.value, home = home)
                            PersonalProfile(vm = home, auth = auth, rootNavController = rootNavController)
                        }

                    }
                }
            }
        }
    } else {
        CommonProgressBar()
    }
}