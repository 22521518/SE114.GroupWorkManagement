package com.example.groupworkmanagement.ui.components.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.robotoLight
import com.example.groupworkmanagement.utils.smallFontSize

@Composable
fun ImageAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberAsyncImagePainter(model = "$imageUrl")
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
fun ImageAvatarRow(
    title: String = "thành viên",
    list: List<AUser>,
    imageSize: Dp = 24.dp,
    fontSize: TextUnit = smallFontSize,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomText(
            text = "${title}: ",
            fontFamily = robotoLight,
            fontSize = fontSize,
            color = fontColor
        )
        Row(
            modifier = Modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy((-8).dp)
        ) {
            list.forEachIndexed { index, mem ->
                if (index < 3)
                    ImageAvatar(
                        imageUrl = "${mem.imageUrl}", modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
            }
            if (list.size >= 3)
                Box(
                    modifier = Modifier
                        .padding(start = (1).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(1.dp, Color.White, CircleShape),

                    ) {
                    CustomText(
                        text = "...",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
        }
    }
}