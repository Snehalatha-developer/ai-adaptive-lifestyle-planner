package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<UserProfile> = userPreferencesRepository.userProfile
}
