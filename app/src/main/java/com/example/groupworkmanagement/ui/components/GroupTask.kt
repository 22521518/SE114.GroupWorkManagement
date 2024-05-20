package com.example.groupworkmanagement.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.ATaskChannel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.utils.ActionButton
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.ExtendTaskBottomSheet
import com.example.groupworkmanagement.ui.components.utils.GroupTaskCard
import com.example.groupworkmanagement.ui.components.utils.ReuniteTaskBottomSheet
import com.example.groupworkmanagement.ui.components.utils.TaskBottomSheet
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTaskLists(
    navController: NavController,
    vm: GroupViewModel,
) {
    vm.activeListener()
    val doneTaskChannel = mutableStateOf(vm.doneTaskChannel.value)
    val lateTaskChannel = mutableStateOf(vm.lateTaskChannel.value)
    val inProgressTaskChannel = mutableStateOf(vm.inProcessTaskChannel.value)

    val scope = rememberCoroutineScope()
    val extendSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val reuniteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val taskSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showExtendSheet = remember { mutableStateOf(false) }
    val showReuniteSheet = remember { mutableStateOf(false) }
    val showTaskSheet = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = extendSheetState.currentValue) {
        showExtendSheet.value = extendSheetState.isVisible
    }
    LaunchedEffect(key1 = reuniteSheetState.currentValue) {
        showReuniteSheet.value = reuniteSheetState.isVisible
    }
    LaunchedEffect(key1 = taskSheetState.currentValue) {
        showTaskSheet.value = taskSheetState.isVisible
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GroupTaskList(
            title = "Đang thực hiện",
            taskList = inProgressTaskChannel.value,
            onExtendActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showExtendSheet.value = true
                                  },
            onReuniteActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showReuniteSheet.value = true
                                   },
            showSheetButton = {
                ActionButton(onClick = { showTaskSheet.value = true }, description = "Tạo tác vụ")
            },
            onClick = { tsk ->  navController.navigate(Destination.GroupTaskChannelRoom.createRoute(tsk.channelId!!))}
        )
        GroupTaskList(
            title = "Trễ hẹn",
            taskList = lateTaskChannel.value,
            onExtendActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showExtendSheet.value = true
                                  },
            onReuniteActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showReuniteSheet.value = true
            },
            onClick = { tsk ->  navController.navigate(Destination.GroupTaskChannelRoom.createRoute(tsk.channelId!!))}
        )

        GroupTaskList(
            title = "Đã hoàn tất",
            taskList = doneTaskChannel.value,
            onExtendActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showExtendSheet.value = true
                                  },
            onReuniteActionClick = {tsk ->
                vm.selectedTask.value = tsk
                showReuniteSheet.value = true
            },
            onClick = { tsk ->  navController.navigate(Destination.GroupTaskChannelRoom.createRoute(tsk.channelId!!))}
        )
        if (showExtendSheet.value) {
            ExtendTaskBottomSheet(
                vm = vm,
                scope = scope,
                sheetState = extendSheetState,
                onDismiss = {
                    showExtendSheet.value = false
                            },
                onConfirm = {  deadline ->
                    vm.extendTask(vm.selectedTask.value!!, deadline)
                })
        }

        if (showTaskSheet.value) {
            TaskBottomSheet(
                scope = scope,
                vm = vm,
                navController = navController,
                sheetState = taskSheetState,
                onDismiss = { showTaskSheet.value = false },
                onConfirm = { name, leaderList, memberList, deadline ->
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.createNewTaskChannel(name, leaderList, memberList, deadline)
                    }
                })
        }

        if (showReuniteSheet.value) {
            ReuniteTaskBottomSheet(
                vm = vm,
                scope = scope,
                listMember = listOf(),
                sheetState = reuniteSheetState,
                onDismiss = { showReuniteSheet.value = false },
                onConfirm = {name, deadline ->
                    vm.createNewTaskChannel(name, vm.selectedTask.value!!, deadline)
                })
        }
    }
}

@Composable
fun GroupTaskList(
    title: String,
    taskList: List<ATaskChannel>,
    onExtendActionClick: (tsk: ATaskChannel) -> Unit,
    onReuniteActionClick: (tsk: ATaskChannel) -> Unit,
    showSheetButton: @Composable() (()->Unit) = {},
    onClick: (tsk: ATaskChannel) -> Unit,
) {
    val showIcon = listOf(Icons.Default.KeyboardArrowDown, Icons.Filled.KeyboardArrowUp)
    var isShow by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isShow = 1 - isShow },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Image(imageVector = showIcon[isShow], contentDescription = null)
                    CustomText(
                        text = "$title - ${taskList.size}",
                        modifier = Modifier.padding(start = 32.dp)
                    )
                }
                showSheetButton()
            }
        }
        if (isShow == 1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                taskList.forEach { task ->
                    GroupTaskCard(
                        task = task,
                        onExtendActionClick = { tsk -> onExtendActionClick(tsk) },
                        onReuniteActionClick = { tsk -> onReuniteActionClick(tsk) },
                        onClick = {tsk -> onClick(tsk) }
                    )
                }
            }
    }
}