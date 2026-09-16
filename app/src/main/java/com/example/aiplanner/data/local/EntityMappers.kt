package com.example.aiplanner.data.local

import com.example.aiplanner.data.local.entity.TaskEntity
import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.utils.toLocalDateOrNull
import com.example.aiplanner.utils.toLocalTimeOrNull
import com.example.aiplanner.utils.toStorageString
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/** Maps between Room entities and domain models, isolating the rest of the app from schema details. */

fun TaskEntity.toDomain(): ScheduleTask = ScheduleTask(
    id = id,
    scheduleId = scheduleId,
    title = title,
    description = description,
    category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.PERSONAL),
    date = date.toLocalDateOrNull() ?: LocalDate.now(),
    startTime = startTime.toLocalTimeOrNull() ?: LocalTime.MIDNIGHT,
    endTime = endTime.toLocalTimeOrNull() ?: LocalTime.MIDNIGHT,
    notificationEnabled = notificationEnabled,
    status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.PENDING),
    plannedStartTime = plannedStartTime?.toLocalTimeOrNull(),
    actualCompletionTime = actualCompletionTime?.let { Instant.ofEpochMilli(it) },
    snoozeCount = snoozeCount
)

fun ScheduleTask.toEntity(now: Long = System.currentTimeMillis()): TaskEntity = TaskEntity(
    id = id,
    scheduleId = scheduleId,
    title = title,
    description = description,
    category = category.name,
    date = date.toStorageString(),
    startTime = startTime.toStorageString(),
    endTime = endTime.toStorageString(),
    notificationEnabled = notificationEnabled,
    status = status.name,
    plannedStartTime = plannedStartTime?.toStorageString(),
    actualCompletionTime = actualCompletionTime?.toEpochMilli(),
    snoozeCount = snoozeCount,
    createdAt = now,
    updatedAt = now
)
