package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.ATaskChannel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.viewmodel.HomeViewModel


@Composable
fun HomeHeader(
    title: String,
    home: HomeViewModel,
) {
    Column (
        modifier = Modifier.fillMaxWidth()
            .height(110.dp)
    )  {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageAvatar(imageUrl = home.currentUser.value?.imageUrl,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            CommonHeader(title = home.currentUser.value?.name.toString(), haveTask = false, haveMemberAdd = false)
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            CustomText(text = title, fontSize = 30.sp, fontFamily = robotoBold)
        }
        CommonDivider()
    }
}

@Composable
fun ChatHeader(
    chatter: AUser,
    onBackHandClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = null,
            tint = fontColor,
            modifier = Modifier.clickable { onBackHandClick() }
        )
        Spacer(modifier = Modifier.padding(end = 16.dp))
        CommonHeader(title = chatter.name, haveTask = false, haveMemberAdd = false)
    }
}

@Composable
fun GroupHeader(
    group: AGroup,
    addMember: () -> Unit,
    addTask: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageAvatar(
            imageUrl = "${group.imageUrl}",
            modifier = Modifier
                .padding(end = 12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        CommonHeader(title = group.groupName.toString(), addMember = addMember, addTask = addTask)
    }
}

@Composable
fun ChannelHeader(
    channel: AChannel,
    onBackHandClick: () -> Unit,
    haveMemberAdd: Boolean = true,
    addMember: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = null,
            tint = fontColor,
            modifier = Modifier.clickable { onBackHandClick() }
        )
        Spacer(modifier = Modifier.padding(end = 16.dp))
        CommonHeader(title = channel.channelName.toString(), addMember = addMember, haveTask = false, haveMemberAdd = haveMemberAdd)
    }
}

@Composable
fun TaskChannelHeader(
    channel: ATaskChannel,
    onBackHandClick: () -> Unit,
    addMember: () -> Unit,
    addTask: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = null,
            tint = fontColor,
            modifier = Modifier.clickable { onBackHandClick() }
        )
        Spacer(modifier = Modifier.padding(end = 16.dp))
        CommonHeader(title = channel.channelName, addMember = addMember, haveTask = true, addTask = addTask)
    }
}

@Composable
fun CommonHeader(
    title: String,
    addMember: () -> Unit = {},
    addTask: () -> Unit = {},
    haveTask: Boolean = true,
    haveMemberAdd: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(text = title, fontSize = bigFontSize)
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (haveMemberAdd)
                Icon(imageVector = Icons.Outlined.PersonAdd, contentDescription = null, tint = fontColor,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { addMember() }
                )
            if (haveTask) {
                Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                Icon(imageVector = Icons.Outlined.PostAdd, contentDescription = null, tint = fontColor,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { addTask() }
                )
            }
        }
    }
}
