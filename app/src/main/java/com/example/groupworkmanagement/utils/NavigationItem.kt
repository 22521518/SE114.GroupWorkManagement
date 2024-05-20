package com.example.groupworkmanagement.utils

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItems (
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)
