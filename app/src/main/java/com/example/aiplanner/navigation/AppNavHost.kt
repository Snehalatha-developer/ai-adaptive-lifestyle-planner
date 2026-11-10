package com.example.aiplanner.navigation

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

    // Shared by the bottom nav tabs AND any in-screen button that jumps to a top-level
    // destination (e.g. Home's "View Full Schedule"). Using the same popUpTo/saveState/
    // restoreState options everywhere keeps the back stack shape consistent regardless of how a
    // destination was reached — mixing this with a plain navigate() elsewhere left the back
    // stack in a state where returning to the start destination silently no-opped.
    fun navigateToTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { navigateToTopLevel(item.route) },
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
                    onViewSchedule = { navigateToTopLevel(Routes.SCHEDULE) },
                    onOpenAiPlanner = { navigateToTopLevel(Routes.AI_PLANNER) },
                    onOpenProgress = { navigateToTopLevel(Routes.PROGRESS) }
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
