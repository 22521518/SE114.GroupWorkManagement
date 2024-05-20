package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.groupworkmanagement.data.model.AMessage
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.utils.CommonDivider
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.ImageAvatar
import com.example.groupworkmanagement.ui.theme.GroupWorkManagementTheme
import com.example.groupworkmanagement.utils.convertDateTimeGetRidOfSecond
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.truncateText

@Composable
fun SingleChatScreen(
    currentUserId: String,
    messageList: List<AMessage>,
    onSend: (String) -> Unit
) {
    var reply by remember {
        mutableStateOf("")
    }
    val chatMessage = mutableStateOf(messageList)

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        MessageBox(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            chatMessage = chatMessage.value,
            currentUserId = currentUserId
        )
        InputTextReply(
            text = reply,
            onReplyTextChange={ reply = it },
            onSend = {
                onSend(it)
                reply = ""
            }
        )
    }
}


@Composable
fun MessageBox(
    modifier: Modifier, chatMessage: List<AMessage> = listOf(),
    currentUserId: String
) {
    LazyColumn(modifier) {
        items(chatMessage) { msg ->
            if(msg.creator.uid == currentUserId)
                MyMessageBox(mess = msg)
            else
                OtherMessageBox(mess = msg)
        }

    }
}

@Composable
fun InputTextReply(
    text: String,
    onReplyTextChange: (String) -> Unit,
    onSend: (String) -> Unit = {},
){
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        CommonDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(value = text, onValueChange = {onReplyTextChange(it)},
                textStyle = TextStyle(lineBreak = LineBreak.Paragraph),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .heightIn(min = 40.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30.dp)
            )
            Box(
                modifier = Modifier.clickable {
                    onSend(text)
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "", tint = fontColor,
                    modifier = Modifier
                        .rotate(-25f)
                        .size(50.dp)
                        .wrapContentWidth()
                        .fillMaxSize()

                )
            }
        }

    }
}

@Composable
fun OtherMessageBox(
    mess: AMessage,
) {
    val imageSize = 40.dp
    val imagePaddingEnd = 16.dp
    Column (
        modifier = Modifier.padding(start = 8.dp, end = 36.dp, top = 16.dp),
    ) {
            CustomText(text = truncateText(mess.creator.name, 10),
                modifier = Modifier
                .padding(start = imageSize + imagePaddingEnd + 16.dp)
                .padding(vertical = 4.dp))
        Row (
        ) {
            ImageAvatar(
                imageUrl = mess.creator.imageUrl,
                modifier = Modifier
                    .padding(end = imagePaddingEnd, top = 4.dp)
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .widthIn(0.dp, 180.dp)
                        .background(Color.Gray)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    CustomText(text = mess.content)
                }
                CustomText(
                    text = convertDateTimeGetRidOfSecond(mess.timestamp),
                    modifier = Modifier
                    .padding(start = 4.dp)
                    .width(90.dp)
                )
            }
        }

    }
}

@Composable
fun MyMessageBox(
    mess: AMessage,
) {
    val imageSize = 40.dp
    val imagePaddingEnd = 16.dp
    Column (
        modifier = Modifier
            .padding(start = 36.dp, end = 8.dp, top = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        CustomText(
            text = truncateText(mess.creator.name, 10),
            modifier = Modifier
                .padding(end = imageSize + imagePaddingEnd + 16.dp)
                .padding(vertical = 4.dp))
        Row (
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                CustomText(
                    text = convertDateTimeGetRidOfSecond(mess.timestamp),
                    modifier = Modifier
                    .width(90.dp)
                    .padding(end = 4.dp), textAlign = TextAlign.End
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .widthIn(0.dp, 180.dp)
                        .background(Color.Gray)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .align(Alignment.Top),

                ) {
                    CustomText(text = mess.content, textAlign = TextAlign.Start,)
                }
                ImageAvatar(
                    imageUrl = mess.creator.imageUrl,
                    modifier = Modifier
                        .padding(start = imagePaddingEnd, top = 4.dp)
                        .size(imageSize)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatReview() {
    GroupWorkManagementTheme {
       SingleChatScreen(
           currentUserId =  "",
           messageList = listOf(
               AMessage(content = "abc", creator = AUser(uid="123", name="dm")),
               AMessage(content = "abc", creator = AUser(uid="", name="dma")),
               ),
        onSend = {}
       )
    }
}
