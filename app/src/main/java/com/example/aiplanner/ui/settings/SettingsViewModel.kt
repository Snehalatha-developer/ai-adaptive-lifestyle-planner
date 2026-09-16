package com.example.aiplanner.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SettingsUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile = UserProfile.default()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getUserProfileUseCase()
        .map { SettingsUiState(isLoading = false, profile = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())
}
