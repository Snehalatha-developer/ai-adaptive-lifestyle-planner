package com.example.aiplanner.data.repository

import com.example.aiplanner.data.local.datastore.UserPreferencesDataStore
import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserPreferencesRepository {

    override val userProfile: Flow<UserProfile> = dataStore.userProfile

    override suspend fun saveUserProfile(profile: UserProfile) = dataStore.saveUserProfile(profile)
}
