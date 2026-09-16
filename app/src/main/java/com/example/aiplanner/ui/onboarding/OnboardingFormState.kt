package com.example.aiplanner.ui.onboarding

import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.model.WeekendPreference
import java.time.LocalTime

/** Editable, in-progress version of [UserProfile] used while the onboarding/edit form is open. */
data class OnboardingFormState(
    val name: String = "",
    val wakeUpTime: LocalTime = LocalTime.of(6, 30),
    val sleepTime: LocalTime = LocalTime.of(23, 0),
    val officeStartTime: LocalTime = LocalTime.of(10, 0),
    val officeEndTime: LocalTime = LocalTime.of(19, 0),
    val commuteMinutes: Int = 30,
    val exerciseMinutes: Int = 45,
    val studyTargetMinutes: Int = 60,
    val breakfastTime: LocalTime = LocalTime.of(8, 0),
    val lunchTime: LocalTime = LocalTime.of(13, 0),
    val dinnerTime: LocalTime = LocalTime.of(20, 30),
    val weekendPreference: WeekendPreference = WeekendPreference.RELAXED,
    val goals: Set<String> = emptySet()
) {
    /** Returns a user-facing error message, or null if the form is ready to submit. */
    fun validate(): String? = when {
        name.isBlank() -> "Please enter your name."
        wakeUpTime == sleepTime -> "Wake-up time and sleep time can't be the same."
        !officeEndTime.isAfter(officeStartTime) -> "Office end time must be after office start time."
        commuteMinutes !in 0..180 -> "Commute time should be between 0 and 180 minutes."
        exerciseMinutes !in 0..180 -> "Exercise time should be between 0 and 180 minutes."
        studyTargetMinutes !in 0..600 -> "Study target should be between 0 and 600 minutes."
        else -> null
    }

    fun toUserProfile(): UserProfile = UserProfile(
        name = name.trim(),
        wakeUpTime = wakeUpTime,
        sleepTime = sleepTime,
        officeStartTime = officeStartTime,
        officeEndTime = officeEndTime,
        commuteMinutes = commuteMinutes,
        exerciseMinutes = exerciseMinutes,
        studyTargetMinutes = studyTargetMinutes,
        breakfastTime = breakfastTime,
        lunchTime = lunchTime,
        dinnerTime = dinnerTime,
        weekendPreference = weekendPreference,
        goals = goals,
        isOnboardingComplete = true
    )

    companion object {
        fun fromUserProfile(profile: UserProfile): OnboardingFormState = OnboardingFormState(
            name = profile.name,
            wakeUpTime = profile.wakeUpTime,
            sleepTime = profile.sleepTime,
            officeStartTime = profile.officeStartTime,
            officeEndTime = profile.officeEndTime,
            commuteMinutes = profile.commuteMinutes,
            exerciseMinutes = profile.exerciseMinutes,
            studyTargetMinutes = profile.studyTargetMinutes,
            breakfastTime = profile.breakfastTime,
            lunchTime = profile.lunchTime,
            dinnerTime = profile.dinnerTime,
            weekendPreference = profile.weekendPreference,
            goals = profile.goals
        )
    }
}
