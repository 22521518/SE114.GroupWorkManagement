package com.example.groupworkmanagement.ui.screens

import android.service.autofill.UserData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Groups2
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.Groups2
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groupworkmanagement.MainViewModel
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.ChannelLists
import com.example.groupworkmanagement.ui.components.GroupTaskLists
import com.example.groupworkmanagement.ui.components.MemberList
import com.example.groupworkmanagement.ui.components.utils.CustomFAB
import com.example.groupworkmanagement.ui.components.utils.GroupHeader
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.NavigationItems
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.popUpNavigate
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel

@Composable
fun GroupScreen(
    main: MainViewModel,
    home: HomeViewModel,
    groupVM: GroupViewModel,
    rootNavController: NavController,
    groupId: String,
    showGroup: MutableState<Boolean>,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val navHostController = rememberNavController()
    LaunchedEffect(key1 = Unit) {
        groupVM.getGroup(groupId)
    }
    BackHandler {
        groupVM.deactivateListener()
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            groupVM.deactivateListener()
        }
    }
    val tabItem = listOf(
        NavigationItems(
            title = "",
            route = Destination.GroupChannel.route,
            selectedIcon = Icons.Filled.Dialpad,
            unselectedIcon = Icons.Outlined.Dialpad,
        ),
        NavigationItems(
            title = "",
            route = Destination.GroupMember.route,
            selectedIcon = Icons.Filled.Groups2,
            unselectedIcon = Icons.Outlined.Groups2,
        ),
        NavigationItems(
            title = "",
            route = Destination.GroupTask.route,
            selectedIcon = Icons.Filled.Business,
            unselectedIcon = Icons.Outlined.Business,
        ),
//        NavigationItems(
//            title = "",
//            route = Destination.GroupTask.route,
//            selectedIcon = Icons.Filled.CalendarMonth,
//            unselectedIcon = Icons.Outlined.CalendarMonth,
//        ),
    )
    var isGroup = remember { showGroup }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isGroup.value) {
            GroupHeader(group = groupVM.groupInstance.value ?: AGroup(),
                addMember = {
                    navHostController.navigate(Destination.GroupAddOutsider.route)
                }, addTask = {
                    navHostController.navigate(Destination.GroupTask.route)
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
                            Icon(
                                imageVector =
                                if (selectedIndex == index) item.selectedIcon
                                else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = fontColor
                            )
                        },
                        selectedContentColor = fontColor,
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp)
        ) {
            NavHost(navController = navHostController, startDestination = Destination.GroupChannel.route ) {
                composable(Destination.GroupChannel.route) {
                    isGroup.value = true
                    ChannelLists(rootNavController = rootNavController , navController = navHostController, vm = groupVM)
                }
                composable(
                    Destination.GroupChannelRoom.route,
                    arguments = listOf(
                        navArgument("id") {type = NavType.StringType},
                        navArgument("public") { type = NavType.BoolType}
                    )
                    ) {
                    isGroup.value = false
                    val id = it.arguments?.getString("id")
                    val public = it.arguments?.getBoolean("public")?: true
                    id?.let {
                        ChannelScreen(navController = navHostController, home = home, group = groupVM, chatId = id, isPublic = public)
                    }
                }
                composable(Destination.GroupTaskChannelRoom.route) {
                    isGroup.value = false
                    it.arguments?.getString("id")?.let {id ->
                        TaskChannelScreen(
                            navController = navHostController,
                            home = home,
                            group = groupVM,
                            channelId = id
                        )
                    }
                }
                composable(Destination.GroupMember.route) {
                    isGroup.value = true
                    val showDialog = remember { mutableStateOf(false) }
                    val deleteId = remember { mutableStateOf<AMember?>(null) }
                    if (!showDialog.value) {
                        MemberList(memList = groupVM.memberList.value,
                            onActionClick = {
                                popUpNavigate(navHostController, Destination.GroupAddOutsider.route)
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
                                groupVM.removeMember(mem.user.uid)
                            }
                            deleteId.value = null
                            showDialog.value = false
                        }
                    }
                }
                composable(Destination.GroupAddOutsider.route) {
                    isGroup.value = false
                    groupVM.insertList.value = AddMemberScreenGroup(
                        list = main.allUserList.value.filter {
                            it !in (groupVM.memberList.value.map { it.user })
                        },
                        onDismiss = {
                            navHostController.popBackStack()
                        },
                        onConfirm = {
                            groupVM.insertMember(groupVM.insertList.value)
                            navHostController.popBackStack()
                        }
                    )
                }
                composable(Destination.GroupAddInsider.route) {
                    isGroup.value = false
                    groupVM.insertList.value = AddMemberScreenGroup(
                        clearList = false,
                        list = groupVM.memberList.value.map { it.user },
                        onDismiss = {
                            navHostController.popBackStack()
                        },
                        onConfirm = {
                            navHostController.popBackStack()
                        }
                    )
                }
                composable(Destination.GroupTask.route) {
                    isGroup.value = true
                    GroupTaskLists(navController = navHostController, vm = groupVM)
                }
                composable(Destination.GroupAddLeader.route) {
                    isGroup.value = false
                    groupVM.leaders.value = AddMemberScreenGroup(
                        clearList = false,
                        list = groupVM.memberList.value.map { it.user }
                            .filter { it.uid != home.currentUser.value?.uid && it !in groupVM.members.value } ,
                        onDismiss = {
                            navHostController.popBackStack()
                        },
                        onConfirm = {
                            navHostController.popBackStack()
                        }
                    )
                }
                composable(Destination.GroupAddMember.route) {
                    isGroup.value = false
                    groupVM.members.value = AddMemberScreenGroup(
                        clearList = false,
                        list = groupVM.memberList.value.map { it.user }
                            .filter { it.uid != home.currentUser.value?.uid && it !in groupVM.leaders.value } ,
                        onDismiss = {
                            navHostController.popBackStack()
                        },
                        onConfirm = {
                            navHostController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
