package com.example.aiplanner.domain.repository

import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Source of truth for schedules and tasks. Backed by Room today; the AI integration in later
 * phases still flows through this same interface (generate -> validate -> replaceScheduleForDate).
 */
interface ScheduleRepository {

    /** Emits the current list of tasks for [date], sorted by start time, and updates live. */
    fun observeTasksForDate(date: LocalDate): Flow<List<ScheduleTask>>

    suspend fun getTaskById(taskId: String): ScheduleTask?

    /** Atomically replaces any existing schedule/tasks for [date] with [tasks]. */
    suspend fun replaceScheduleForDate(date: LocalDate, tasks: List<ScheduleTask>)

    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, actualCompletionTime: Instant?)

    suspend fun hasScheduleForDate(date: LocalDate): Boolean
}
