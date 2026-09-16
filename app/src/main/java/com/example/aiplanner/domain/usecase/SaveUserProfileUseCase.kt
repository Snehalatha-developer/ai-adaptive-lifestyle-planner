package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(profile: UserProfile) = userPreferencesRepository.saveUserProfile(profile)
}
