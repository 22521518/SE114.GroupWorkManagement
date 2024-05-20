package com.example.groupworkmanagement.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.utils.CommonDivider
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.ImageAvatar
import com.example.groupworkmanagement.ui.components.utils.UserCard
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.utils.robotoLight
import com.example.groupworkmanagement.utils.smallFontSize
import com.example.groupworkmanagement.utils.truncateText

@Composable
fun AddMemberScreenGroup(
    clearList: Boolean = true,
    list: List<AUser>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
): List<AUser> {
    var searchInput by remember { mutableStateOf("") }
    var orgList by remember { mutableStateOf(list) }
    var addedList by remember { mutableStateOf<List<AUser>>( listOf()) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = null,
                    tint = fontColor,
                    modifier = Modifier.clickable {
                        orgList = listOf()
                        addedList = listOf()
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.padding(end = 16.dp))
                CustomText(text = "Thêm thành viên")
            }
            if(addedList.isNotEmpty())
                CustomText(text = "Thêm", fontFamily = robotoBold, modifier = Modifier.clickable {
                    orgList = listOf()
                    onConfirm()
                    if (clearList)
                        addedList = listOf()
                }.padding(end = 30.dp))
            else
                CustomText(text = "Thêm", fontFamily = robotoLight, modifier = Modifier.padding(end = 30.dp))
        }
        CommonDivider()
//        OutlinedTextField(
//            value = searchInput,
//            onValueChange = { searchInput = it },
//            placeholder = { CustomText(text = "Nhập....") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp, horizontal = 16.dp),
//        )
        if (addedList.isNotEmpty()) {
            CustomText(
                text = "Thêm - ${addedList.size}",
                modifier = Modifier.align(Alignment.Start).padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                addedList.forEach {user ->
                    Column (
                        modifier = Modifier.width(70.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageAvatar(imageUrl = user.imageUrl,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .clickable {
                                    addedList = addedList - user
                                    orgList = orgList + user
                                }
                        )
                        CustomText(text = truncateText(text = user.name, maxLength = 5))
                    }
                }
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            CustomText(text = "Gợi ý", fontSize = smallFontSize)
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            orgList.forEach {user ->
                Box(modifier = Modifier.clickable {
                    orgList = orgList - user
                    addedList = addedList + user
                }) {
                    UserCard(user = user, imageSize = 60.dp, textSize = 24.sp)
                }
            }
        }
    }
    return addedList
}