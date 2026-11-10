package com.example.aiplanner.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.usecase.GenerateAiScheduleUseCase
import com.example.aiplanner.domain.usecase.GenerateLocalScheduleUseCase
import com.example.aiplanner.domain.usecase.GetUserProfileUseCase
import com.example.aiplanner.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val form: OnboardingFormState = OnboardingFormState(),
    val isLoadingExisting: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

/** Backs both first-time onboarding and the "Edit Profile" screen reached from Settings. */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val generateLocalScheduleUseCase: GenerateLocalScheduleUseCase,
    private val generateAiScheduleUseCase: GenerateAiScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var hasLoadedExisting = false

    /** Pre-fills the form from the saved profile, if any. Safe to call multiple times. */
    fun loadExistingProfileIfNeeded() {
        if (hasLoadedExisting) return
        hasLoadedExisting = true
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingExisting = true) }
            val profile = getUserProfileUseCase().first()
            _uiState.update {
                if (profile.isOnboardingComplete) {
                    it.copy(form = OnboardingFormState.fromUserProfile(profile), isLoadingExisting = false)
                } else {
                    it.copy(isLoadingExisting = false)
                }
            }
        }
    }

    fun updateForm(transform: (OnboardingFormState) -> OnboardingFormState) {
        _uiState.update { it.copy(form = transform(it.form), errorMessage = null) }
    }

    /** Validates, persists the profile, (re)generates today's schedule, then invokes [onSaved]. */
    fun save(onSaved: () -> Unit) {
        val form = _uiState.value.form
        val error = form.validate()
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val profile = form.toUserProfile()
            saveUserProfileUseCase(profile)
            generateAiScheduleUseCase(profile).onFailure { generateLocalScheduleUseCase(profile) }
            _uiState.update { it.copy(isSaving = false) }
            onSaved()
        }
    }
}
