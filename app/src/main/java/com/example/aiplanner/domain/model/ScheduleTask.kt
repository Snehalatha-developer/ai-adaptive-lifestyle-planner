package com.example.aiplanner.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/**
 * A single item on a user's daily schedule. Mirrors the structured task shape the backend
 * will return in later phases (see `POST /api/v1/schedule/generate`), so the same model can be
 * populated either by the local rule-based generator (Phase 1) or by the AI service (Phase 4+).
 */
data class ScheduleTask(
    val id: String,
    val scheduleId: String,
    val title: String,
    val description: String?,
    val category: TaskCategory,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val notificationEnabled: Boolean,
    val status: TaskStatus,
    val plannedStartTime: LocalTime?,
    val actualCompletionTime: Instant?,
    val snoozeCount: Int
) {
    val durationMinutes: Long
        get() = java.time.Duration.between(startTime, endTime).toMinutes().coerceAtLeast(0)

    fun displayStatus(now: LocalTime): TaskDisplayStatus = when {
        status == TaskStatus.COMPLETED -> TaskDisplayStatus.COMPLETED
        status == TaskStatus.SKIPPED -> TaskDisplayStatus.SKIPPED
        !now.isBefore(startTime) && now.isBefore(endTime) -> TaskDisplayStatus.CURRENT
        else -> TaskDisplayStatus.UPCOMING
    }
}

/** A full day's plan: the fixed/flexible tasks generated for one date. */
data class DailySchedule(
    val scheduleId: String,
    val date: LocalDate,
    val tasks: List<ScheduleTask>
)
