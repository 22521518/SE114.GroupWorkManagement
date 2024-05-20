package com.example.groupworkmanagement.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


fun handleException(exception: Exception? = null, customMessage: String? = "") {
    exception?.printStackTrace()
    val errorMsg = exception?.localizedMessage ?: ""
    val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

    Log.e("CUSTOM ERROR", message)
}

fun truncateText(text: String, maxLength: Int): String {
    return if (text.length > maxLength) {
        text.substring(0, maxLength) + "..."
    } else {
        text
    }
}

fun getToday(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
fun showList(flag: Int): Int = 1 - flag
fun convertDateTimeGetRidOfSecond(dateTime: String): String {
    val dateTimeInstance = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
    return dateTimeInstance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}

fun popUpNavigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}