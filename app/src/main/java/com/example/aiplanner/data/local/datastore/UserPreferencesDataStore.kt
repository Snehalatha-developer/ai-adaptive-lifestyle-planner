package com.example.aiplanner.data.local.datastore

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.model.WeekendPreference
import com.example.aiplanner.utils.toLocalTimeOrNull
import com.example.aiplanner.utils.toStorageString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.userPreferencesStore by preferencesDataStore(name = "user_preferences")

/**
 * Local data source for the user's onboarding profile, backed by Jetpack DataStore
 * (Preferences). [com.example.aiplanner.data.repository.UserPreferencesRepositoryImpl] is the
 * only caller — everything else in the app talks to the domain repository interface.
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NAME = stringPreferencesKey("name")
        val WAKE_TIME = stringPreferencesKey("wake_time")
        val SLEEP_TIME = stringPreferencesKey("sleep_time")
        val OFFICE_START = stringPreferencesKey("office_start")
        val OFFICE_END = stringPreferencesKey("office_end")
        val COMMUTE_MINUTES = intPreferencesKey("commute_minutes")
        val EXERCISE_MINUTES = intPreferencesKey("exercise_minutes")
        val STUDY_MINUTES = intPreferencesKey("study_minutes")
        val BREAKFAST_TIME = stringPreferencesKey("breakfast_time")
        val LUNCH_TIME = stringPreferencesKey("lunch_time")
        val DINNER_TIME = stringPreferencesKey("dinner_time")
        val WEEKEND_PREFERENCE = stringPreferencesKey("weekend_preference")
        val GOALS = stringSetPreferencesKey("goals")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val userProfile: Flow<UserProfile> = context.userPreferencesStore.data
        .catch { exception ->
            // A corrupted preferences file surfaces as IOException; fall back to defaults
            // instead of crashing the app.
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it.toUserProfile() }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.userPreferencesStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.WAKE_TIME] = profile.wakeUpTime.toStorageString()
            prefs[Keys.SLEEP_TIME] = profile.sleepTime.toStorageString()
            prefs[Keys.OFFICE_START] = profile.officeStartTime.toStorageString()
            prefs[Keys.OFFICE_END] = profile.officeEndTime.toStorageString()
            prefs[Keys.COMMUTE_MINUTES] = profile.commuteMinutes
            prefs[Keys.EXERCISE_MINUTES] = profile.exerciseMinutes
            prefs[Keys.STUDY_MINUTES] = profile.studyTargetMinutes
            prefs[Keys.BREAKFAST_TIME] = profile.breakfastTime.toStorageString()
            prefs[Keys.LUNCH_TIME] = profile.lunchTime.toStorageString()
            prefs[Keys.DINNER_TIME] = profile.dinnerTime.toStorageString()
            prefs[Keys.WEEKEND_PREFERENCE] = profile.weekendPreference.name
            prefs[Keys.GOALS] = profile.goals
            prefs[Keys.ONBOARDING_COMPLETE] = true
        }
    }

    private fun Preferences.toUserProfile(): UserProfile {
        val defaults = UserProfile.default()
        return UserProfile(
            name = this[Keys.NAME] ?: defaults.name,
            wakeUpTime = this[Keys.WAKE_TIME]?.toLocalTimeOrNull() ?: defaults.wakeUpTime,
            sleepTime = this[Keys.SLEEP_TIME]?.toLocalTimeOrNull() ?: defaults.sleepTime,
            officeStartTime = this[Keys.OFFICE_START]?.toLocalTimeOrNull() ?: defaults.officeStartTime,
            officeEndTime = this[Keys.OFFICE_END]?.toLocalTimeOrNull() ?: defaults.officeEndTime,
            commuteMinutes = this[Keys.COMMUTE_MINUTES] ?: defaults.commuteMinutes,
            exerciseMinutes = this[Keys.EXERCISE_MINUTES] ?: defaults.exerciseMinutes,
            studyTargetMinutes = this[Keys.STUDY_MINUTES] ?: defaults.studyTargetMinutes,
            breakfastTime = this[Keys.BREAKFAST_TIME]?.toLocalTimeOrNull() ?: defaults.breakfastTime,
            lunchTime = this[Keys.LUNCH_TIME]?.toLocalTimeOrNull() ?: defaults.lunchTime,
            dinnerTime = this[Keys.DINNER_TIME]?.toLocalTimeOrNull() ?: defaults.dinnerTime,
            weekendPreference = this[Keys.WEEKEND_PREFERENCE]?.let {
                runCatching { WeekendPreference.valueOf(it) }.getOrNull()
            } ?: defaults.weekendPreference,
            goals = this[Keys.GOALS] ?: defaults.goals,
            isOnboardingComplete = this[Keys.ONBOARDING_COMPLETE] ?: false
        )
    }
}
