package com.example.aiplanner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A single schedule item, persisted with adaptive-scheduling fields for later phases. */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["scheduleId"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scheduleId"), Index("date")]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val scheduleId: String,
    val title: String,
    val description: String?,
    /** [com.example.aiplanner.domain.model.TaskCategory] name. */
    val category: String,
    /** ISO date, e.g. "2026-09-16". */
    val date: String,
    /** "HH:mm" 24-hour time. */
    val startTime: String,
    /** "HH:mm" 24-hour time. */
    val endTime: String,
    val notificationEnabled: Boolean,
    /** [com.example.aiplanner.domain.model.TaskStatus] name. */
    val status: String,
    /** Original planned start time ("HH:mm"), preserved across reschedules for adaptive learning. */
    val plannedStartTime: String?,
    /** Epoch millis the task was actually marked complete, if any. */
    val actualCompletionTime: Long?,
    val snoozeCount: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)
