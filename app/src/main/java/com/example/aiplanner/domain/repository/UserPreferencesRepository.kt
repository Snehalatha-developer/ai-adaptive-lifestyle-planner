package com.example.aiplanner.domain.repository

import com.example.aiplanner.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** Source of truth for the user's onboarding profile/preferences, backed by DataStore. */
interface UserPreferencesRepository {
    val userProfile: Flow<UserProfile>
    suspend fun saveUserProfile(profile: UserProfile)
}
