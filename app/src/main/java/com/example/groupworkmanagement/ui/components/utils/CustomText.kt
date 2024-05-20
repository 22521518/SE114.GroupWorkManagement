package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.normalFontSize
import com.example.groupworkmanagement.utils.robotoRegular


@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = fontColor,
    fontSize: TextUnit = normalFontSize,
    fontFamily: FontFamily? = robotoRegular,
    textAlign: TextAlign? = TextAlign.Start,
    softWrap: Boolean = true,
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = fontFamily,
        modifier = modifier,
        textAlign = textAlign,
        color = color,
        softWrap = softWrap,
        style = TextStyle(lineHeight = 20.sp, lineBreak = LineBreak.Paragraph),

    )
}