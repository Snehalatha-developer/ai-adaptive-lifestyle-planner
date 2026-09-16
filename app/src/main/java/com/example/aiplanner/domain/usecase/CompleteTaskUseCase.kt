package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.repository.ScheduleRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Marks a task as done. In Phase 2 this will also cancel any pending notification for the task
 * and roll the completion into the day's progress totals.
 */
class CompleteTaskUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(taskId: String) {
        scheduleRepository.updateTaskStatus(taskId, TaskStatus.COMPLETED, Instant.now())
    }
}
