package com.example.groupworkmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.groupworkmanagement.ui.components.utils.CustomButton
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.components.utils.ImageAvatar
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.actionButtonColor
import com.example.groupworkmanagement.utils.bigFontSize
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.viewmodel.AuthViewModel
import com.example.groupworkmanagement.viewmodel.HomeViewModel


@Composable
fun PersonalProfile(
    auth: AuthViewModel,
    vm: HomeViewModel,
    rootNavController: NavController
) {
    var user by remember { mutableStateOf(vm.currentUser.value) }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageAvatar(imageUrl = user?.imageUrl,
            modifier = Modifier
                .padding(4.dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        val textName = user?.name ?: "unknown"
        CustomText(text = textName, fontSize = bigFontSize)

        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        Button(
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally, unbounded = true)
                .padding(vertical = 12.dp, horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
            shape = RoundedCornerShape(999.dp),
            onClick = {
            vm.logOut()
            auth.logOut()
            rootNavController.navigate(Destination.LoginScreen.route)
        }) {
            CustomText(text = "Log out", modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp), fontSize = 24.sp, fontFamily = robotoBold)
        }
    }
}