package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.groupworkmanagement.utils.fontColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onAccept: (Long?) -> Unit,
    onCancel: () -> Unit
) {
    val state = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = { onAccept(state.selectedDateMillis) }) {
                CustomText("Accept")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                CustomText("Cancel")
            }
        },
        colors = DatePickerDefaults.colors(
            titleContentColor = fontColor,
            headlineContentColor = fontColor,
            subheadContentColor = fontColor,
            containerColor = Color(0xFFFFEDDB),
            disabledDayContentColor = fontColor,
            disabledSelectedDayContentColor = fontColor,
            disabledSelectedDayContainerColor = fontColor,
            selectedDayContainerColor = fontColor,
            selectedDayContentColor = fontColor,
            selectedYearContainerColor = fontColor,
            selectedYearContentColor = fontColor,
            dayInSelectionRangeContainerColor = fontColor,
            dayInSelectionRangeContentColor = fontColor,
            dayContentColor = fontColor,
            weekdayContentColor = fontColor,
            currentYearContentColor = fontColor,
            todayContentColor = fontColor,
            todayDateBorderColor = fontColor
        )
    ) {
        DatePicker(state = state)
    }
}