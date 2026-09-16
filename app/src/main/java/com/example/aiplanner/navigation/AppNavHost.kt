package com.example.aiplanner.navigation

import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aiplanner.ui.aiplanner.AiPlannerScreen
import com.example.aiplanner.ui.components.LoadingScreen
import com.example.aiplanner.ui.home.HomeScreen
import com.example.aiplanner.ui.onboarding.OnboardingScreen
import com.example.aiplanner.ui.progress.ProgressScreen
import com.example.aiplanner.ui.schedule.ScheduleScreen
import com.example.aiplanner.ui.settings.SettingsScreen

/** Root composable: decides Onboarding vs Home on launch, then hosts every screen + bottom nav. */
@Composable
fun AiPlannerApp() {
    val appViewModel: AppViewModel = hiltViewModel()
    val startDestination by appViewModel.startDestination.collectAsStateWithLifecycle()

    val resolvedStart = startDestination
    if (resolvedStart == null) {
        LoadingScreen()
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = BottomNavItem.items.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = resolvedStart,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    isEditMode = false,
                    onComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.EDIT_PROFILE) {
                OnboardingScreen(
                    isEditMode = true,
                    onComplete = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onViewSchedule = { navController.navigate(Routes.SCHEDULE) { launchSingleTop = true } },
                    onOpenAiPlanner = { navController.navigate(Routes.AI_PLANNER) { launchSingleTop = true } },
                    onOpenProgress = { navController.navigate(Routes.PROGRESS) { launchSingleTop = true } }
                )
            }
            composable(Routes.SCHEDULE) { ScheduleScreen() }
            composable(Routes.AI_PLANNER) { AiPlannerScreen() }
            composable(Routes.PROGRESS) { ProgressScreen() }
            composable(Routes.SETTINGS) {
                SettingsScreen(onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) })
            }
        }
    }
}
