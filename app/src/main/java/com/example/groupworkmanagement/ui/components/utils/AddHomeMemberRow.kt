package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.smallFontSize
import com.example.groupworkmanagement.viewmodel.HomeViewModel


@Composable
fun AddHomeMemberRow(
    vm : HomeViewModel,
    navController: NavController,
    title: String = "thành viên",
    imageSize: Dp = 24.dp,
    fontSize: TextUnit = smallFontSize,
): List<AUser> {
    val list by remember { mutableStateOf(vm.insertList.value) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageAvatarRow(list = list, title = title, imageSize = imageSize, fontSize = fontSize)
        OutlinedButton(
            onClick = {
                navController.navigate(Destination.HomeAdd.route)
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
                if (list.isNotEmpty())
                    CustomText(text = list.size.toString(), color = Color.Black, fontSize = 14.sp)
            }
        }
    }

    return list
}