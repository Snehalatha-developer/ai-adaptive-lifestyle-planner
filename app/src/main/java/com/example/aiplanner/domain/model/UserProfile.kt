package com.example.aiplanner.domain.model

import java.time.LocalTime

/** How the user wants weekends handled relative to their weekday routine. */
enum class WeekendPreference(val label: String) {
    SAME_AS_WEEKDAY("Same as weekdays"),
    RELAXED("Sleep in / relaxed pace"),
    FULL_REST("Full rest day")
}

/**
 * The user's lifestyle preferences collected during onboarding. This is the single source of
 * truth the schedule generator (local rule-based today, AI-backed from Phase 4) plans around.
 */
data class UserProfile(
    val name: String,
    val wakeUpTime: LocalTime,
    val sleepTime: LocalTime,
    val officeStartTime: LocalTime,
    val officeEndTime: LocalTime,
    val commuteMinutes: Int,
    val exerciseMinutes: Int,
    val studyTargetMinutes: Int,
    val breakfastTime: LocalTime,
    val lunchTime: LocalTime,
    val dinnerTime: LocalTime,
    val weekendPreference: WeekendPreference,
    val goals: Set<String>,
    val isOnboardingComplete: Boolean
) {
    companion object {
        /** Sensible defaults shown the first time a user opens onboarding. */
        fun default(): UserProfile = UserProfile(
            name = "",
            wakeUpTime = LocalTime.of(6, 30),
            sleepTime = LocalTime.of(23, 0),
            officeStartTime = LocalTime.of(10, 0),
            officeEndTime = LocalTime.of(19, 0),
            commuteMinutes = 30,
            exerciseMinutes = 45,
            studyTargetMinutes = 60,
            breakfastTime = LocalTime.of(8, 0),
            lunchTime = LocalTime.of(13, 0),
            dinnerTime = LocalTime.of(20, 30),
            weekendPreference = WeekendPreference.RELAXED,
            goals = emptySet(),
            isOnboardingComplete = false
        )
    }
}

/** Fixed, predefined lifestyle goal choices shown as selectable chips in onboarding/settings. */
object LifestyleGoals {
    val PREDEFINED: List<String> = listOf(
        "Exercise regularly",
        "Study consistently",
        "Maintain consistent sleep",
        "Improve daily routine",
        "Reduce screen time",
        "Eat healthier"
    )
}
