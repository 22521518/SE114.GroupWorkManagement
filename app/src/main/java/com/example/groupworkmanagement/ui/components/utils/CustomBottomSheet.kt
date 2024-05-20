package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.getToday
import com.example.groupworkmanagement.utils.normalFontSize
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.viewmodel.GroupViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel
import com.example.groupworkmanagement.viewmodel.TaskChannelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    title: String,
    height: Float = 0.7f,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        },
        modifier = Modifier.fillMaxHeight(height),
        shape = RoundedCornerShape(21.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomText(text = title, fontFamily = robotoBold, fontSize = bigFontSize)
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupBottomSheet(
    vm: HomeViewModel,
    navController: NavController,
    owner: AUser,
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, ls: List<AUser>) -> Unit,
) {
    val groupName = remember { vm.groupName }
    val listMember = remember {
        mutableStateOf<List<AUser>>(listOf())
    }

    val errorInput = mutableStateOf(0)
    val errorText = listOf("", "Vui lòng chọn tên khác")

    BottomSheet(
        title = "Tạo nhóm",
        sheetState = sheetState,
        onDismiss = {
            onDismiss()
            vm.groupName.value = ""
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            //Input field
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = groupName.value,
                    onValueChange = { groupName.value = it },
                    label = { CustomText(text = "Tên nhóm") },
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
                    UserCard(user = owner)
                }
                Spacer(modifier = Modifier.height(24.dp))
                listMember.value = AddHomeMemberRow(navController = navController, vm = vm)
            }
            //Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        onConfirm(groupName.value, listMember.value)
                        vm.groupName.value = ""
                        sheetState.hide()
                    }.invokeOnCompletion { onDismiss() }
                }, description = "Tạo")
                CancelButton(
                    onClick = {
                        scope.launch {
                            vm.groupName.value = ""
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
fun ExtendTaskBottomSheet(
    vm: GroupViewModel,
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (deadline: String) -> Unit,
) {
    val isOpenDatePicker = remember { mutableStateOf(false) }
    val deadline = remember { mutableStateOf(LocalDate.now()) }
    BottomSheet(
        title = "Gia hạn",
        height = 0.4f,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isOpenDatePicker.value = true
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        enabled = false,
                        isError = (!(deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                                && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                                )),
                        value = deadline.value.format(DateTimeFormatter.ISO_DATE),
                        onValueChange = {  },
                        label = {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                CustomText(text = "Deadline", color =
                                    if(!(deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                                        && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                                    ) )Color.Red
                                    else fontColor
                                )
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    tint = if(!(deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                                        && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                                    )) Color.Red
                                    else fontColor,
                                )
                            }
                        },
                        modifier = Modifier,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                            && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                        ) {
                            onConfirm(deadline.value.format(DateTimeFormatter.ISO_DATE))
                            sheetState.hide()
                        }
                    }.invokeOnCompletion {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                            && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                            )
                            onDismiss()
                    }
                }, description = "Gia hạn")
                CancelButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    description = "Hủy"
                )
            }

            if (isOpenDatePicker.value) {
                CustomDatePickerDialog(
                    onAccept = {
                        isOpenDatePicker.value = false
                        if (it != null) {
                            deadline.value = Instant
                                .ofEpochMilli(it)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }},
                    onCancel = { isOpenDatePicker.value = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReuniteTaskBottomSheet(
    vm: GroupViewModel,
    scope: CoroutineScope,
    listMember: List<AUser>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name:String, deadline: String) -> Unit,
) {

    val isOpenDatePicker = remember { mutableStateOf(false) }
    val deadline = remember { mutableStateOf(LocalDate.now()) }
    var taskName by remember {
        mutableStateOf("")
    }

    BottomSheet(
        title = "Tạo nhóm tác vụ",
        sheetState = sheetState,
        onDismiss = {
            onDismiss()
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 50.dp)
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { CustomText(text = "Tên tác vụ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isOpenDatePicker.value = true
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        enabled = false,
                        isError = ( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()
                                || deadline.value.format(DateTimeFormatter.ISO_DATE) <= vm.selectedTask.value?.deadline!!
                                ),
                        value = deadline.value.format(DateTimeFormatter.ISO_DATE),
                        onValueChange = {  },
                        label = {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                CustomText(text = "Deadline", color =
                                if( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()
                                    || deadline.value.format(DateTimeFormatter.ISO_DATE) <= vm.selectedTask.value?.deadline!!
                                ) Color.Red
                                else fontColor
                                )
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    tint = if(!(deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                                        && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                                    )) Color.Red
                                    else fontColor,
                                )
                            }
                        },
                        modifier = Modifier,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                            && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                        ) {
                            onConfirm(taskName, deadline.value.format(DateTimeFormatter.ISO_DATE))
                            sheetState.hide()
                        }
                    }.invokeOnCompletion {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()
                            && deadline.value.format(DateTimeFormatter.ISO_DATE) > vm.selectedTask.value?.deadline!!
                        )
                            onDismiss()
                    }
                }, description = "Gia hạn")
                CancelButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    description = "Hủy"
                )
            }

            if (isOpenDatePicker.value) {
                CustomDatePickerDialog(
                    onAccept = {
                        isOpenDatePicker.value = false
                        if (it != null) {
                            deadline.value = Instant
                                .ofEpochMilli(it)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }},
                    onCancel = { isOpenDatePicker.value = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    vm: GroupViewModel,
    navController: NavController,
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, leaderList: List<AUser>, memberList: List<AUser>, deadline: String) -> Unit,
) {
    val isOpenDatePicker = remember { mutableStateOf(false) }
    val deadline = remember { mutableStateOf(LocalDate.now()) }

    var taskName by remember { vm.taskName }
    val leaderList = remember { mutableStateOf<List<AUser>>(listOf()) }
    val memberList = remember { mutableStateOf<List<AUser>>(listOf()) }

    val errorInput = mutableStateOf(0)
    val errorText = listOf("", "Vui lòng chọn tên khác")
    BottomSheet(
        title = "Tạo nhóm tác vụ",
        sheetState = sheetState,
        onDismiss = {
            vm.taskName.value = ""
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
                    value = taskName,
                    isError = (errorInput.value == 1),
                    onValueChange = { taskName = it },
                    label = { CustomText(text = "Tên tác vụ") },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                CustomText(text = errorText[errorInput.value], color = Color.Red)
                leaderList.value = AddTaskLeaderRow(vm = vm, navController, title = "nhóm trưởng", fontSize = normalFontSize)
                Spacer(modifier = Modifier.height(6.dp))
                memberList.value = AddTaskMemberRow(vm = vm, navController, title = "thành viên", fontSize = normalFontSize)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isOpenDatePicker.value = true
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        enabled = false,
                        isError = (deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()),
                        value = deadline.value.format(DateTimeFormatter.ISO_DATE),
                        onValueChange = { },
                        label = {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                CustomText(text = "Deadline", color =
                                if( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()) Color.Red
                                else fontColor)
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    tint = if( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()) Color.Red
                                    else fontColor,
                                )
                            }
                        },
                        modifier = Modifier,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()) {
                            vm.members.value = listOf()
                            vm.leaders.value = listOf()
                            onConfirm(taskName, leaderList.value, memberList.value, deadline.value.format(
                                DateTimeFormatter.ISO_DATE))
                            vm.taskName.value = ""
                            sheetState.hide()
                        }
                    }.invokeOnCompletion { if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()) onDismiss() }
                }, description = "Tạo")
                CancelButton(
                    onClick = {
                        scope.launch {
                            vm.taskName.value = ""
                            sheetState.hide()
                        }.invokeOnCompletion { onDismiss() }
                    },
                    description = "Hủy"
                )
            }
        }
        if (isOpenDatePicker.value) {
            CustomDatePickerDialog(
                onAccept = {
                    isOpenDatePicker.value = false
                    if (it != null) {
                        deadline.value = Instant
                            .ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                    }},
                onCancel = { isOpenDatePicker.value = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalTaskBottomSheet(
    vm: TaskChannelViewModel,
    navController: NavController,
    scope: CoroutineScope,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (name: String, member: AUser, deadline: String) -> Unit,
) {
    val isOpenDatePicker = remember { mutableStateOf(false) }
    val deadline = remember { mutableStateOf(LocalDate.now()) }

    var taskName by remember { vm.taskName }
    val member = remember { mutableStateOf(vm.insertMem.value) }

    val errorInput = mutableStateOf(0)
    val errorText = listOf("", "Vui lòng chọn tên khác")
    BottomSheet(
        title = "Tạo tác vụ",
        sheetState = sheetState,
        onDismiss = {
            vm.taskName.value = ""
            member.value = null
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
                    value = taskName,
                    isError = (errorInput.value == 1),
                    onValueChange = { taskName = it },
                    label = { CustomText(text = "Tên tác vụ") },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
                CustomText(text = errorText[errorInput.value], color = Color.Red)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if(member.value != null ) Arrangement.SpaceBetween
                                else Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if(member.value != null) {
                        UserCard(user = member.value!!)
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Destination.TaskAddSingleMember.route)
                        },
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, fontColor)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(imageVector = Icons.Filled.Person, contentDescription = null)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isOpenDatePicker.value = true
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        enabled = false,
                        isError = (deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()),
                        value = deadline.value.format(DateTimeFormatter.ISO_DATE),
                        onValueChange = { },
                        label = {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                CustomText(text = "Deadline", color =
                                if( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()) Color.Red
                                else fontColor)
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    tint = if( deadline.value.format(DateTimeFormatter.ISO_DATE) <= getToday()) Color.Red
                                    else fontColor,
                                )
                            }
                        },
                        modifier = Modifier,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConfirmButton(onClick = {
                    scope.launch {
                        if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()) {
                            onConfirm(taskName, vm.insertMem.value!!, deadline.value.format(
                                DateTimeFormatter.ISO_DATE))
                            member.value = null
                            vm.taskName.value = ""
                            sheetState.hide()
                        }
                    }.invokeOnCompletion { if( deadline.value.format(DateTimeFormatter.ISO_DATE) > getToday()) onDismiss() }
                }, description = "Tạo")
                CancelButton(
                    onClick = {
                        scope.launch {
                            member.value = null
                            vm.taskName.value = ""
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                    description = "Hủy"
                )
            }
        }
        if (isOpenDatePicker.value) {
            CustomDatePickerDialog(
                onAccept = {
                    isOpenDatePicker.value = false
                    if (it != null) {
                        deadline.value = Instant
                            .ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                    }},
                onCancel = { isOpenDatePicker.value = false }
            )
        }
    }
}