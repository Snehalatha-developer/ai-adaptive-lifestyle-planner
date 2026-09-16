package com.example.aiplanner.data.repository

import com.example.aiplanner.data.local.dao.ScheduleDao
import com.example.aiplanner.data.local.dao.TaskDao
import com.example.aiplanner.data.local.entity.ScheduleEntity
import com.example.aiplanner.data.local.toDomain
import com.example.aiplanner.data.local.toEntity
import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.repository.ScheduleRepository
import com.example.aiplanner.utils.toStorageString
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao,
    private val taskDao: TaskDao
) : ScheduleRepository {

    override fun observeTasksForDate(date: LocalDate): Flow<List<ScheduleTask>> =
        taskDao.observeTasksForDate(date.toStorageString()).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTaskById(taskId: String): ScheduleTask? =
        taskDao.getTaskById(taskId)?.toDomain()

    override suspend fun replaceScheduleForDate(date: LocalDate, tasks: List<ScheduleTask>) {
        val dateKey = date.toStorageString()
        // Clear whatever schedule/tasks currently occupy this date before inserting the new set,
        // so regenerating or re-syncing a day never leaves stale or overlapping leftovers.
        taskDao.deleteTasksForDate(dateKey)
        scheduleDao.deleteScheduleForDate(dateKey)

        val scheduleId = tasks.firstOrNull()?.scheduleId ?: "local-$dateKey"
        scheduleDao.insertSchedule(
            ScheduleEntity(
                scheduleId = scheduleId,
                date = dateKey,
                source = "LOCAL_RULE_BASED",
                createdAt = System.currentTimeMillis()
            )
        )
        taskDao.insertTasks(tasks.map { it.toEntity() })
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, actualCompletionTime: Instant?) {
        val existing = taskDao.getTaskById(taskId) ?: return
        taskDao.updateTask(
            existing.copy(
                status = status.name,
                actualCompletionTime = actualCompletionTime?.toEpochMilli() ?: existing.actualCompletionTime,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun hasScheduleForDate(date: LocalDate): Boolean =
        scheduleDao.getScheduleByDate(date.toStorageString()) != null
}
