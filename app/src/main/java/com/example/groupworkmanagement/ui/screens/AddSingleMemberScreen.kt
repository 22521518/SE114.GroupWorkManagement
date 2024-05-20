package com.example.groupworkmanagement.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.utils.CommonDivider
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.UserCard
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.utils.robotoLight
import com.example.groupworkmanagement.utils.smallFontSize
import com.example.groupworkmanagement.viewmodel.TaskChannelViewModel

@Composable
fun AddSingleMemberScreenGroup(
    title: String = "Thêm thành viên",
    list: List<AUser>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
): AUser? {
    var searchInput by remember { mutableStateOf("") }
    var orgList by remember { mutableStateOf(list) }
    var inserted by remember { mutableStateOf<AUser?>(null) }

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
                        inserted = null
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.padding(end = 16.dp))
                CustomText(text = title)
            }
            if (inserted != null) {
                CustomText(text = "Thêm", fontFamily = robotoBold, modifier = Modifier
                    .clickable {
                        orgList = listOf()
                        onConfirm()
                    }
                    .padding(end = 30.dp))
            } else {
                CustomText(text = "Thêm", fontFamily = robotoLight, modifier = Modifier.padding(end = 30.dp))
            }
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
        inserted?.let {
            CustomText(
                text = "Thêm - ${inserted?.name}",
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row (
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .clickable {
                        orgList += inserted?: AUser()
                        inserted = null
                    }
            ) {
                UserCard(user = inserted?:AUser(), imageSize = 60.dp, textSize = 24.sp)
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
                    inserted?.let {
                        orgList = orgList + inserted!!
                    }
                    inserted = user
                }) {
                    UserCard(user = user, imageSize = 60.dp, textSize = 24.sp)
                }
            }
        }
    }
    return inserted
}