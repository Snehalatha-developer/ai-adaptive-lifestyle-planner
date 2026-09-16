package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.repository.ScheduleRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTodayTasksUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<List<ScheduleTask>> =
        scheduleRepository.observeTasksForDate(date)
}
