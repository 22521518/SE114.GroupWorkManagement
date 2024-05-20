package com.example.groupworkmanagement.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.groupworkmanagement.ui.components.utils.CommonDivider
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.actionButtonColor
import com.example.groupworkmanagement.utils.backgroundColor
import com.example.groupworkmanagement.utils.fontColor
import com.example.groupworkmanagement.utils.handleException
import com.example.groupworkmanagement.utils.robotoBold
import com.example.groupworkmanagement.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    vm: AuthViewModel,
    navController: NavController,
    showToast: (String) -> Unit,
) {
    vm.auth.currentUser?.uid?.let {
        navController.navigate(Destination.Home.route)
    }

    val title = "Quản lý nhóm"
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    fun signUp() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                vm.signUp(email = email.value, password = password.value, name = name.value)
            } catch (ex: Exception) {
                handleException(ex, ex.message)
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 64.dp)
            .background(backgroundColor),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CustomText(text = title, fontSize = 26.sp)
            Spacer(modifier = Modifier.padding(vertical = 24.dp))
            Column (
                modifier = Modifier
                    .padding(horizontal = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = {name.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "", tint = fontColor)
                    },
                    label = {
                        CustomText(text = "name")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = fontColor,
                        containerColor = Color.Transparent,
                        unfocusedTextColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = {email.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "", tint = fontColor)
                    },
                    label = {
                        CustomText(text = "email")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = fontColor,
                        containerColor = Color.Transparent,
                        unfocusedTextColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                OutlinedTextField(
                    value = changePassword(password.value),//password.value,
                    onValueChange = { password.value = it},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Password, contentDescription = "", tint = fontColor)
                    },
                    label = {
                        CustomText(text = "password")
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = Color.Transparent,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.padding(vertical = 32.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        signUp()
                        navController.navigate(Destination.Home.route)
                    }
                ) {
                    CustomText(text = "Đăng kí ", fontFamily = robotoBold, modifier = Modifier.padding(8.dp))
                }
            }
        }
        Column (
            modifier = Modifier.height(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonDivider()
            CustomText(
                text = "Đã có tài khoản?",
                modifier = Modifier
                    .clickable {
                        navController.navigate(Destination.LoginScreen.route)
                    }
            )
            Spacer(modifier = Modifier.padding(vertical = 24.dp))
        }
    }
}
