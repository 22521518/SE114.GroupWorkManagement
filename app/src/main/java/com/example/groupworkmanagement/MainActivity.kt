package com.example.groupworkmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groupworkmanagement.data.model.AMessage
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.ui.components.MyMessageBox
import com.example.groupworkmanagement.ui.components.OtherMessageBox
import com.example.groupworkmanagement.ui.components.utils.CustomText
import com.example.groupworkmanagement.ui.screens.HomeScreen
import com.example.groupworkmanagement.ui.screens.LoginScreen
import com.example.groupworkmanagement.ui.screens.SignUpScreen
import com.example.groupworkmanagement.ui.theme.GroupWorkManagementTheme
import com.example.groupworkmanagement.utils.Destination
import com.example.groupworkmanagement.utils.backgroundColor
import com.example.groupworkmanagement.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupWorkManagementTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {
                    val navController = rememberNavController()
                    val main = hiltViewModel<MainViewModel>()
                    val auth = hiltViewModel<AuthViewModel>()
                    NavHost(navController = navController, startDestination = Destination.Home.route ) {
                        composable(Destination.LoginScreen.route) {
                            Column ( modifier = Modifier.fillMaxSize(),) {
                                LoginScreen(vm = auth, showToast = {showToast(it)}, navController = navController)
                            }
                        }
                        composable(Destination.SignUpScreen.route) {
                            SignUpScreen(vm = auth, showToast = { showToast(it) }, navController = navController)
                        }
                        composable(Destination.Home.route) {
                            Column ( modifier = Modifier.fillMaxSize(),) {
                                HomeScreen(
                                    main = main,
                                    auth = auth,
                                    showToast = { showToast(it) },
                                    rootNavController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}