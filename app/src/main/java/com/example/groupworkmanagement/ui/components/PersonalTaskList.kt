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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groupworkmanagement.data.model.ATask
import com.example.groupworkmanagement.ui.components.utils.CommonProgressBar
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.UserPersonalTaskCard
import com.example.groupworkmanagement.utils.showList
import com.example.groupworkmanagement.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PersonalTaskList(
    home: HomeViewModel
) {
    val showIcon = listOf(Icons.Default.KeyboardArrowDown, Icons.Filled.KeyboardArrowUp)
    var isInProgressShow by remember { mutableStateOf(1) }
    var isLateShow by remember { mutableStateOf(1) }
    var isDoneShow by remember { mutableStateOf(1) }

    val lateList = mutableStateOf(home.lateTask.value)
    val doneList = mutableStateOf(home.doneTask.value)
    val inProgressList = mutableStateOf(home.inProgressTask.value)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(end = 14.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { isInProgressShow = showList(isInProgressShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
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
                inProgressList.value.forEach { rtsk ->
                    UserPersonalTaskCard(ATask = rtsk, true,
                        onDoneAction = {tsk ->
                            CoroutineScope(Dispatchers.IO).launch {
                                home.completeTask(tsk)
                            }
                        })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { isDoneShow = showList(isDoneShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
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
                doneList.value.forEach { rtsk ->
                    UserPersonalTaskCard(ATask = rtsk, true,
                        onReverseAction = {tsk ->
                            CoroutineScope(Dispatchers.IO).launch {
                                home.reverseTask(tsk)
                            }
                        })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clickable { isLateShow = showList(isLateShow) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
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
                    UserPersonalTaskCard(ATask = tsk, true)
                }
            }
        }
    }
}

