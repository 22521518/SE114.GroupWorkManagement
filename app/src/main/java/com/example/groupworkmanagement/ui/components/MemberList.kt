package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.data.model.ROLE
import com.example.groupworkmanagement.ui.components.utils.ActionButton
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.UserCard


@Composable
fun MemberList(
    memList: List<AMember>,
    icon: @Composable() (() -> Unit) = {},
    haveActionClick: Boolean = true,
    onActionClick: () -> Unit,
    onMemberClick:(mem: AMember) -> Unit,
) {
    var number by remember { mutableStateOf(memList.size) }
    var searchInput by remember { mutableStateOf("") }

    LaunchedEffect(key1 = memList) {
        number = memList.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomText(text = "Thành viên - $number")
            if (haveActionClick)
                ActionButton(onClick = { onActionClick() }, description = "Thêm thành viên")
        }
        Spacer(modifier = Modifier.padding(vertical = 16.dp))

//        OutlinedTextField(
//            value = searchInput,
//            onValueChange = { searchInput = it },
//            placeholder = { CustomText(text = "Nhập....") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 16.dp),
//        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            items(memList) { mem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onMemberClick(mem)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserCard(user = mem.user, imageSize = 40.dp, textSize = 20.sp)
                    if (mem.role == ROLE.admin) CustomText(mem.role.toString(), modifier = Modifier.padding(end = 24.dp))
                    icon()
                }
            }
        }
    }
}