package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.groupworkmanagement.ui.components.utils.ActionButton
import com.example.groupworkmanagement.ui.components.utils.ChannelPersonalTaskCard
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.PersonalTaskBottomSheet
import com.example.groupworkmanagement.utils.showList
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.TaskChannelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChannelList(
    vm: TaskChannelViewModel,
    group: GroupViewModel,
    navController: NavController,
    showButton: Boolean = true,
) {
    //
    val taskChannel = mutableStateOf(vm.taskChannelInstance.value)
    val showIcon = listOf(Icons.Default.KeyboardArrowDown, Icons.Filled.KeyboardArrowUp)
    val myList = mutableStateOf(vm.myTaskList.value)
    val lateList = mutableStateOf(vm.lateTaskList.value)
    val doneList = mutableStateOf(vm.doneTaskList.value)
    val inProgressList = mutableStateOf(vm.inProcessTaskList.value)

    //
    val showTaskSheet = remember { mutableStateOf(false) }
    var isMineShow by remember { mutableStateOf(0) }
    var isInProgressShow by remember { mutableStateOf(0) }
    var isLateShow by remember { mutableStateOf(0) }
    var isDoneShow by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(key1 = sheetState.currentValue) {
        showTaskSheet.value = sheetState.isVisible
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 14.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomText(text = "Hoàn tất - ${taskChannel.value?.progress}%")
            if (showButton)
                ActionButton(onClick = { showTaskSheet.value = true }, description = "Tạo tác vụ")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showList(isMineShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.clickable { isMineShow = showList(isMineShow) }
            ) {
                Image(imageVector = showIcon[isMineShow], contentDescription = null)
                CustomText(
                    text = "Của tôi - ${myList.value.size}",
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
        if (isMineShow == 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                myList.value.forEach { tsk ->
                    ChannelPersonalTaskCard(ATask = tsk, true,
                        onReverseAction = {
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.reverseTask(tsk)
                            }
                        },
                        onDoneAction = {
                            CoroutineScope(Dispatchers.IO).launch {
                                vm.completeTask(tsk)
                            }
                        })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { showList(isInProgressShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.clickable { isInProgressShow = showList(isInProgressShow) }
            ) {
                Image(imageVector = showIcon[isInProgressShow], contentDescription = null)
                CustomText(
                    text = "Đang thực hiện - ${inProgressList.value.size}",
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
        if (isInProgressShow == 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                inProgressList.value.forEach { tsk ->
                    ChannelPersonalTaskCard(ATask = tsk)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { showList(isDoneShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.clickable { isDoneShow = showList(isDoneShow) }
            ){
                Image(imageVector = showIcon[isDoneShow], contentDescription = null)
                CustomText(
                    text = "Đã hoàn thành - ${doneList.value.size}",
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
        if (isDoneShow == 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                doneList.value.forEach { tsk ->
                    ChannelPersonalTaskCard(ATask = tsk)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { showList(isLateShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.clickable { isLateShow = showList(isLateShow) }
            ) {
                Image(imageVector = showIcon[isLateShow], contentDescription = null)
                CustomText(
                    text = "Trễ hẹn - ${lateList.value.size}",
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }
        if (isLateShow == 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                lateList.value.forEach { tsk ->
                    ChannelPersonalTaskCard(ATask = tsk)
                }
            }
        }

        if (showTaskSheet.value) {
           PersonalTaskBottomSheet(
                vm = vm,
                navController = navController,
                scope = scope,
                sheetState = sheetState,
                onDismiss = { showTaskSheet.value = false },
                onConfirm = { name, member, deadline ->
                    vm.createPersonalTask(name, member, deadline)
               })
        }
    }
}
