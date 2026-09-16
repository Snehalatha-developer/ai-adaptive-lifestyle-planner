package com.example.aiplanner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** The five primary destinations shown in the bottom navigation bar. */
sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home)
    data object Schedule : BottomNavItem(Routes.SCHEDULE, "Schedule", Icons.Filled.CalendarMonth)
    data object AiPlanner : BottomNavItem(Routes.AI_PLANNER, "AI Planner", Icons.Filled.AutoAwesome)
    data object Progress : BottomNavItem(Routes.PROGRESS, "Progress", Icons.AutoMirrored.Filled.TrendingUp)
    data object Settings : BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)

    companion object {
        val items: List<BottomNavItem> = listOf(Home, Schedule, AiPlanner, Progress, Settings)
    }
}
