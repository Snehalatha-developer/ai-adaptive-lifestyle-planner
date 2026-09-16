package com.example.aiplanner.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Decides whether the app should open on Onboarding (first run) or Home (returning user).
 * Kept minimal on purpose: this is a one-time decision made at process start, not a live redirect
 * — once the user is past onboarding, editing their profile later never bounces them back here.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val startDestination: StateFlow<String?> = userPreferencesRepository.userProfile
        .map { profile -> if (profile.isOnboardingComplete) Routes.HOME else Routes.ONBOARDING }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
