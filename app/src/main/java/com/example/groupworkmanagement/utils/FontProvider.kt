package com.example.groupworkmanagement.utils

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.groupworkmanagement.R


val ggProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontRoboto = GoogleFont("Roboto")
val robotoRegular =
    FontFamily(Font(googleFont = fontRoboto, fontProvider = ggProvider, weight = FontWeight(400)))
val robotoLight =
    FontFamily(Font(googleFont = fontRoboto, fontProvider = ggProvider, weight = FontWeight(450)))
val robotoBold =
    FontFamily(Font(googleFont = fontRoboto, fontProvider = ggProvider, weight = FontWeight(700)))
val robotoExtremeBold =
    FontFamily(Font(googleFont = fontRoboto, fontProvider = ggProvider, weight = FontWeight(900)))

val bigFontSize: TextUnit = 24.sp
val normalFontSize: TextUnit = 16.sp
val smallFontSize: TextUnit = 12.sp

