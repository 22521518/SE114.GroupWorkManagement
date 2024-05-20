package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.groupworkmanagement.utils.actionButtonColor
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoBold

@Composable
fun CustomButton(onClick: () -> Unit, content: @Composable()(() -> Unit)) {
    Button(
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally, unbounded = true)
            .width(82.dp)
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
        shape = RoundedCornerShape(999.dp),
        onClick = { onClick() },
    ) {
        content()
    }
}

@Composable
fun ActionButton(onClick: () -> Unit, description: String) {
    Button(
        modifier = Modifier.wrapContentHeight(),
        colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
    ) {
        CustomText(
            text = description,
            fontFamily = robotoBold,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
        )
    }
}

@Composable
fun CancelButton(onClick: () -> Unit, description: String) {
    OutlinedButton(
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally, unbounded = true)
            .width(174.dp)
            .height(64.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, fontColor)

    ) {
        CustomText(
            text = description,
            fontFamily = robotoBold,
            fontSize = bigFontSize,
        )
    }
}

@Composable
fun ConfirmButton(onClick: () -> Unit, description: String) {
    Button(
        modifier = Modifier
            .wrapContentWidth(Alignment.CenterHorizontally, unbounded = true)
            .width(174.dp)
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),

        ) {
        CustomText(
            text = description,
            fontFamily = robotoBold,
            fontSize = bigFontSize,
            color = fontColor
        )
    }
}

@Composable
fun DoneButton(
    onActionClick: () -> Unit
) {
    OutlinedButton(
        onClick = onActionClick,
        modifier = Modifier
            .size(80.dp)
            .padding(0.dp),
        border = BorderStroke(1.5.dp, Color.Black),
    ) {
        Column {
            Image(imageVector = Icons.Filled.Done, contentDescription = "Done")
        }
    }
}

@Composable
fun ReverseButton(
    onActionClick: () -> Unit
) {
    OutlinedButton(
        onClick = onActionClick,
        modifier = Modifier
            .size(80.dp)
            .padding(0.dp),
        border = BorderStroke(1.5.dp, Color.Black),
    ) {
        Column {
            Image(imageVector = Icons.Filled.Refresh, contentDescription = "Done")
        }
    }
}