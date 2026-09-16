package com.example.aiplanner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One day's schedule header. Individual items live in [TaskEntity], linked by [scheduleId]. */
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val scheduleId: String,
    /** ISO date, e.g. "2026-09-16". */
    val date: String,
    /** Where this schedule came from: "LOCAL_RULE_BASED" (Phase 1) or "AI" (Phase 4+). */
    val source: String,
    val createdAt: Long
)
