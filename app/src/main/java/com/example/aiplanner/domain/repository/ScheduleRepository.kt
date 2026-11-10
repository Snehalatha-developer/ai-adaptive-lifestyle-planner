package com.example.aiplanner.domain.repository

import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.model.UserProfile
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

/** Result of an AI replan call: the updated tasks plus a chat-style summary of what changed. */
data class ReplanResult(val tasks: List<ScheduleTask>, val assistantMessage: String)

/**
 * Source of truth for schedules and tasks. Backed by Room today; the AI integration flows
 * through this same interface (generate/replan -> validate -> replaceScheduleForDate).
 */
interface ScheduleRepository {

    /** Emits the current list of tasks for [date], sorted by start time, and updates live. */
    fun observeTasksForDate(date: LocalDate): Flow<List<ScheduleTask>>

    suspend fun getTaskById(taskId: String): ScheduleTask?

    /** Atomically replaces any existing schedule/tasks for [date] with [tasks]. */
    suspend fun replaceScheduleForDate(date: LocalDate, tasks: List<ScheduleTask>, source: String = "LOCAL_RULE_BASED")

    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, actualCompletionTime: Instant?)

    suspend fun hasScheduleForDate(date: LocalDate): Boolean

    /** Asks the AI planner proxy to generate a full day plan from [profile], and persists it. */
    suspend fun generateAiSchedule(profile: UserProfile, date: LocalDate): Result<List<ScheduleTask>>

    /**
     * Asks the AI planner proxy to apply [instruction] (a free-text change, e.g. a chat message,
     * an ad-hoc task to insert, or a skip/reschedule event) to today's schedule, and persists the
     * result. Leaves the existing schedule untouched if the call fails.
     */
    suspend fun replan(
        profile: UserProfile,
        currentTasks: List<ScheduleTask>,
        now: LocalTime,
        instruction: String
    ): Result<ReplanResult>
}
