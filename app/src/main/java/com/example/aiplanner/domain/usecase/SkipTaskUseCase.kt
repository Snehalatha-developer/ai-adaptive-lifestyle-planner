package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.repository.ScheduleRepository
import javax.inject.Inject

class SkipTaskUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(taskId: String) {
        scheduleRepository.updateTaskStatus(taskId, TaskStatus.SKIPPED, null)
    }
}
