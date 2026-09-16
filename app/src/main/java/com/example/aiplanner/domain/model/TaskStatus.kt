package com.example.aiplanner.domain.model

/** Lifecycle state of a single scheduled task. */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    RESCHEDULED
}

/**
 * How a task should be *displayed* on the Today's Schedule timeline. This is derived from
 * [TaskStatus] plus the current time and is intentionally kept separate from [TaskStatus]:
 * the persisted status only changes on explicit user action, while the display state also
 * reacts to the clock (e.g. a PENDING task becomes "CURRENT" once its time window starts).
 */
enum class TaskDisplayStatus {
    COMPLETED,
    SKIPPED,
    CURRENT,
    UPCOMING
}
