package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.utils.ActionButton
import com.example.groupworkmanagement.ui.components.utils.AddInsiderMemberRow
import com.example.groupworkmanagement.ui.components.utils.BottomSheet
import com.example.groupworkmanagement.ui.components.utils.CancelButton
import com.example.groupworkmanagement.ui.components.utils.ConfirmButton
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.UserCard
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.popUpNavigate
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.utils.showList
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelLists(
    rootNavController: NavController,
    navController: NavController,
    vm: GroupViewModel
) {
    val publicChannel = mutableStateOf(vm.publicChannel.value)
    val privateChannel = mutableStateOf(vm.privateChannel.value)

    val scope = rememberCoroutineScope()
    val typeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showTypeBottomSheet = remember { mutableStateOf(typeSheetState.isVisible) }
    val publicSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showPublicBottomSheet = remember { mutableStateOf(typeSheetState.isVisible) }
    val privateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showPrivateBottomSheet = remember { mutableStateOf(typeSheetState.isVisible) }

    LaunchedEffect(key1 = typeSheetState.currentValue) {
        showTypeBottomSheet.value = typeSheetState.isVisible
    }
    LaunchedEffect(key1 = publicSheetState.currentValue) {
        showPublicBottomSheet.value = publicSheetState.isVisible
    }
    LaunchedEffect(key1 = privateSheetState.currentValue) {
        showPrivateBottomSheet.value = privateSheetState.isVisible
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ChannelList(
            channel = publicChannel.value,
            title = "chung",
            onActionClick = { showTypeBottomSheet.value = true },
            showButton = true,
            isClick = true,
            onChannelClick = {ch ->
                popUpNavigate(navController, Destination.GroupChannelRoom.createRoute(ch.channelId!!))
            }
        )
        ChannelList(
            channel = privateChannel.value,
            title = "riêng tư",
            onActionClick = { showTypeBottomSheet.value = true },
            isClick = true,
            onChannelClick = {ch ->
                popUpNavigate(navController, Destination.GroupChannelRoom.createRoute(ch.channelId!!, false))
            }
        )
        if (showTypeBottomSheet.value) {
            TypeChannelBottomSheet(
                scope = scope,
                sheetState = typeSheetState,
                onDismiss = { showTypeBottomSheet.value = false },
                onConfirmPrivate = {
                    showPrivateBottomSheet.value = true
                    showTypeBottomSheet.value = false
                },
                onConfirmPublic = {
                    showPublicBottomSheet.value = true
                    showTypeBottomSheet.value = false
                }
            )
        }
        if (showPublicBottomSheet.value) {
            PublicChannelBottomSheet(
                scope = scope,
                sheetState = publicSheetState,
                onDismiss = { showPublicBottomSheet.value = false },
                onConfirm = {name ->
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.createPublicChannel(name)
                    }
                })
        }
        if (showPrivateBottomSheet.value) {
            PrivateChannelBottomSheet(
                vm = vm,
                navController = navController,
                scope = scope,
                sheetState = privateSheetState,
                onDismiss = {showPrivateBottomSheet.value = false },
                onConfirm = { name, ls ->
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.createPrivateChannel( name = name, list = ls)
                    }
                })
        }
    }
}

@Composable
fun ChannelList(
    channel: List<AChannel>,
    title: String,
    onActionClick: () -> Unit,
    showButton: Boolean = false,
    isClick: Boolean = false,
    onChannelClick: (channel: AChannel) -> Unit = {},
) {
    val showIcon = listOf(Icons.Default.KeyboardArrowUp, Icons.Filled.KeyboardArrowDown)
    var isShow by remember { mutableStateOf(1) }
    var number by remember { mutableStateOf(channel.size) }

    LaunchedEffect(key1 = channel.size) {
        number = channel.size
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.clickable { isShow = showList(isShow) }
            ) {
                Image(imageVector = showIcon[isShow], contentDescription = null)
                CustomText(text = "$title - $number", modifier = Modifier.padding(start = 32.dp))
            }
            if (showButton)
                ActionButton(onClick = { onActionClick() }, description = "Tạo kênh")
        }

        if (isShow == 1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                channel.forEach { ch ->
                    CustomText(
                        text = ch.channelName.toString(),
                        modifier =
                        if (isClick) Modifier.padding(3.dp).clickable { onChannelClick(ch) }
                            else Modifier.padding(3.dp),
                        color = Color(0xFFA09080),
                        fontFamily = robotoBold,
                        fontSize = bigFontSize

                    )
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChannelBottomSheet(
    vm: GroupViewModel,
    navController: NavController,
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, ls: List<AUser>) -> Unit,
) {
    val channelName = remember { vm.channelName }
    val listMember = remember {
        mutableStateOf<List<AUser>>(listOf())
    }

    val errorInput = mutableStateOf(0)
    val errorText = listOf("", "Vui lòng chọn tên khác")

    BottomSheet(
        title = "Tạo kênh riêng tư",
        sheetState = sheetState,
        onDismiss = {
            vm.channelName.value = ""
            onDismiss()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = channelName.value,
                    onValueChange = { channelName.value = it },
                    label = { CustomText(text = "Tên kênh") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                )
                CustomText(text = errorText[errorInput.value], color = Color.Red)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText("Chủ sở hữu:", modifier = Modifier.padding(end = 24.dp))
                    UserCard(user = AUser(name = "bạn"))
                }
                Spacer(modifier = Modifier.height(24.dp))
                listMember.value = AddInsiderMemberRow(vm = vm, navController = navController)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        onConfirm(channelName.value, listMember.value)
                        vm.channelName.value = ""
                        sheetState.hide()
                    }.invokeOnCompletion { onDismiss() }
                }, description = "Tạo")
                CancelButton(
                    onClick = {
                        scope.launch {
                            vm.channelName.value = ""
                            sheetState.hide()
                        }.invokeOnCompletion { onDismiss() }
                    },
                    description = "Hủy"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicChannelBottomSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var channelName by remember { mutableStateOf("") }

    val errorInput = mutableStateOf(0)
    val errorText = listOf("", "Vui lòng chọn tên khác")

    BottomSheet(
        title = "Tạo kênh công khai",
        height = 0.5f,
        sheetState = sheetState,
        onDismiss = {
            onDismiss()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { CustomText(text = "Tên kênh") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                )
                CustomText(text = errorText[errorInput.value], color = Color.Red)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText("Chủ sở hữu:", modifier = Modifier.padding(end = 24.dp))
                    UserCard(user = AUser(name = "bạn"))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        onConfirm(channelName)
                        sheetState.hide()
                    }.invokeOnCompletion { onDismiss() }
                }, description = "Tạo")
                CancelButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    description = "Hủy"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChannelBottomSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirmPrivate: () -> Unit,
    onConfirmPublic: () -> Unit,
) {
    BottomSheet(
        title = "Chọn loại kênh",
        height = 0.3f,
        sheetState = sheetState,
        onDismiss = {
            onDismiss()
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 45.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ConfirmButton(onClick = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion { onConfirmPublic()}
            }, description = "Công khai")
            CancelButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }
                        .invokeOnCompletion { onConfirmPrivate() }
                },
                description = "Riêng tư"
            )
        }
    }

}