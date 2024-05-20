package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.ATask
import com.example.groupworkmanagement.data.model.ATaskChannel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.data.model.TASK_STATUS
import com.example.groupworkmanagement.ui.theme.GroupWorkManagementTheme
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.normalFontSize
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.utils.robotoLight
import com.example.groupworkmanagement.utils.smallFontSize
import com.example.groupworkmanagement.utils.taskDoneColor
import com.example.groupworkmanagement.utils.taskInProgressColor
import com.example.groupworkmanagement.utils.taskLateColor
import com.example.groupworkmanagement.utils.truncateText

//TASK CARD SECTION
@Composable
fun GroupTaskCard(
    task: ATaskChannel = ATaskChannel(),
    onExtendActionClick: (tsk: ATaskChannel) -> Unit,
    onReuniteActionClick: (tsk: ATaskChannel) -> Unit,
    onClick: (tsk: ATaskChannel) -> Unit,
) {
    var color by remember {
        mutableStateOf(Color.Transparent)
    }
    var flagExtend by remember {
        mutableStateOf<Boolean?>(null)
    }
    LaunchedEffect(key1 = task.status) {
        when (task.status) {
            TASK_STATUS.DONE -> {
                color = taskDoneColor
                flagExtend = false
            }
            TASK_STATUS.LATE -> {
                color = taskLateColor
                flagExtend = true
            }
            else -> {
                color = fontColor
            }
        }
    }

    Card(
        modifier = Modifier
            .size(height = 130.dp, width = 350.dp)
            .clickable { onClick(task) },
        border = BorderStroke(1.dp, Color.LightGray),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xffF6EDE4))
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomText(text = "tiến độ - ${task.progress}%", fontFamily = robotoLight, fontSize = smallFontSize, color = color)
                CustomText(text = "Hạn chót - ${task.deadline}", fontFamily = robotoLight, fontSize = smallFontSize, color = color)
            }
            CustomText(text = truncateText(task.channelName, 15), fontFamily = robotoBold, fontSize = bigFontSize, color = color)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row (
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        ImageAvatarRow(list = listOf(task.creator), title = "trưởng nhóm")
                        Text(text = "Thành viên - ${task.totalMember}")
                    }
                }

                flagExtend?.let {
                    if (it)
                        ActionButton(onClick = { onExtendActionClick(task) }, description = "Gia hạn")
                    else
                        ActionButton(onClick = { onReuniteActionClick(task) }, description = "Tái hợp")
                }
            }
        }
    }
}

@Composable
fun UserPersonalTaskCard(
    ATask: ATask,
    showButton: Boolean = false,
    onReverseAction: (tsk: ATask) -> Unit = {},
    onDoneAction: (tsk: ATask) -> Unit = {},
) {
    var borderColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var bgColor by remember {
        mutableStateOf(Color.Transparent)
    }

    LaunchedEffect(key1 = ATask.status) {
        when (ATask.status) {
            TASK_STATUS.DONE -> {
                borderColor = taskDoneColor
                bgColor = Color(0xFFF0FFF0)
            }
            TASK_STATUS.LATE -> {
                borderColor = taskLateColor
                bgColor = Color(0xFFFFF0F0)
            }
            else -> {
                borderColor = taskInProgressColor
                bgColor = Color.White
            }
        }
    }

    Card(
        modifier = Modifier
            .size(height = 110.dp, width = 350.dp),
        border = BorderStroke(0.7.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.SpaceEvenly

            ) {
                CustomText(
                    text = "${
                        truncateText(
                            ATask.group.groupName.toString(),
                            12
                        )
                    } - ${truncateText(ATask.taskChannel.channelName.toString(), 10)}"
                )
                Column {
                    CustomText(
                        text = truncateText(ATask.name, 16),
                        fontFamily = robotoBold,
                        fontSize = bigFontSize
                    )
                    CustomText("Hạn chót: ${ATask.deadline}")
                }
            }
            if (showButton) {
                when (ATask.status) {
                    TASK_STATUS.DONE -> {
                        ReverseButton(onActionClick = {onReverseAction(ATask)})
                    }
                    TASK_STATUS.IN_PROGRESS -> {
                        DoneButton(onActionClick =  {onDoneAction(ATask)})
                    } else -> {}
                }
            }
        }
    }
}

@Composable
fun ChannelPersonalTaskCard(
    ATask: ATask,
    showButton: Boolean = false,
    onReverseAction: () -> Unit = {},
    onDoneAction: () -> Unit = {},
) {
    var borderColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var bgColor by remember {
        mutableStateOf(Color.Transparent)
    }

    LaunchedEffect(key1 = ATask.status) {
        when (ATask.status) {
            TASK_STATUS.DONE -> {
                borderColor = taskDoneColor
                bgColor = Color(0xFFF0FFF0)
            }
            TASK_STATUS.LATE -> {
                borderColor = taskLateColor
                bgColor = Color(0xFFFFF0F0)
            }
            else -> {
                borderColor = taskInProgressColor
                bgColor = Color.White
            }
        }
    }

    Card(
        modifier = Modifier
            .size(height = 110.dp, width = 350.dp),
        border = BorderStroke(0.7.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 6.dp),
                verticalArrangement = Arrangement.SpaceEvenly

            ) {
                UserCard(user = ATask.member)
                Column {
                    CustomText(text = ATask.name, fontFamily = robotoBold, fontSize = bigFontSize)
                    CustomText("Hạn chót: ${ATask.deadline}")
                }
            }
            if (showButton) {
                when (ATask.status) {
                    TASK_STATUS.DONE -> {
                        ReverseButton(onActionClick = {onReverseAction()})
                    }
                    TASK_STATUS.IN_PROGRESS -> {
                        DoneButton(onActionClick = {onDoneAction()})
                    } else -> {}
                }
            }

        }
    }
}

//REUSED CARD
@Composable
fun UserCard(
    user: AUser,
    imageSize: Dp = 24.dp,
    textSize: TextUnit = normalFontSize,
    showEmail: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageAvatar(
            imageUrl = user.imageUrl,
            modifier = Modifier
                .padding(4.dp)
                .padding(end = 16.dp)
                .size(imageSize)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        CustomText(
            text = "${truncateText(user.name, 10) ?: "----"} ${if (showEmail) user.email else ""}",
            fontSize = textSize
        )
        if (showEmail) {
            CustomText(
                text = user.email,
                fontSize = textSize,
                modifier = Modifier.padding(start = 15.dp)
            )
        }
    }
}
//GROUP CARD LIST
@Composable
fun GroupCard(
    group: AGroup,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(350.dp)
            .height(96.dp)
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        border = BorderStroke(0.7.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4DABE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageAvatar(
                imageUrl = group.imageUrl, modifier = Modifier
                    .padding(4.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
            )
            Column(
                modifier = Modifier
                    .padding(start = 25.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomText(
                        text = truncateText(group.groupName.toString(), 8),
                        fontSize = bigFontSize,
                        fontFamily = robotoBold,
                    )
                    CustomText(
                        text = "${group.totalMember} thành viên",
                        fontSize = smallFontSize,
                        fontFamily = robotoLight,
                    )
                }
                CustomText(
                    text = truncateText(group.creator.name, 20),
                )
            }
        }
    }
}
