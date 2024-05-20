package com.example.groupworkmanagement.ui.components

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
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.ui.components.utils.ActionButton
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.GroupBottomSheet
import com.example.groupworkmanagement.ui.components.utils.GroupCard
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupList(
    navController: NavController,
    homeVM: HomeViewModel,
) {
    val myGroup = mutableStateOf(homeVM.userGroup.value)
    val otherGroup = mutableStateOf(homeVM.otherGroup.value)
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val showBottomSheet = remember { mutableStateOf(sheetState.isVisible) }

    LaunchedEffect(key1 = sheetState.currentValue) {
        showBottomSheet.value = sheetState.isVisible
        println("sheetState ${showBottomSheet.value}")
    }

    LaunchedEffect(key1 = myGroup.value.size) {
        println("dm my fou ${myGroup.value.size}")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GroupList(
            navController = navController,
            group = myGroup.value,
            title = "Nhóm của bạn",
            showButton = true,
            onActionClick = {
                showBottomSheet.value = true
            })
        GroupList(
            navController = navController,
            group = otherGroup.value,
            title = "Nhóm khác",
            onActionClick = {
                showBottomSheet.value = true
            })
    }
    if (showBottomSheet.value) {
        GroupBottomSheet(
            owner = homeVM.currentUser.value!!,
            vm = homeVM,
            navController = navController,

            sheetState = sheetState,
            scope = scope,
            onDismiss = {
                showBottomSheet.value = false
            },
            onConfirm = { name, ls ->
                CoroutineScope(Dispatchers.IO).launch {
                    homeVM.createGroup(name, ls)
                }
            }
        )
    }
}

@Composable
fun GroupList(
    navController: NavController,
    group: List<AGroup>,
    title: String,
    showButton: Boolean = false,
    onActionClick: () -> Unit = {}
) {
    val showIcon = listOf(Icons.Default.KeyboardArrowUp, Icons.Filled.KeyboardArrowDown)
    var isShow by remember { mutableStateOf(1) }
    var number by remember { mutableStateOf(group.size) }

    LaunchedEffect(key1 = group) {
        number = group.size
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.clickable { isShow = 1 - isShow }
            ) {
                Image(imageVector = showIcon[isShow], contentDescription = null)
                CustomText(text = "$title - ${group.size}", modifier = Modifier.padding(start = 32.dp))
            }
            if (showButton)
                ActionButton(onClick = { onActionClick() }, description = "Tạo nhóm")
        }
        if (isShow == 1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                group.forEach { gr ->
                    GroupCard(group = gr) {
                        navController.navigate(Destination.GroupRoom.createRoute(gr.groupId!!))
                    }
                }
            }
    }

}