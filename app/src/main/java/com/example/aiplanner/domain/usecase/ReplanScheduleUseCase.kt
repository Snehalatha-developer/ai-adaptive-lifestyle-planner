package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.repository.ReplanResult
import com.example.aiplanner.domain.repository.ScheduleRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Applies a free-text [instruction] to today's schedule via the AI planner proxy — the shared
 * backbone for ad-hoc task inserts, conversational rearranges, and reschedule-on-skip. Leaves the
 * existing schedule untouched if the call fails.
 */
class ReplanScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTodayTasksUseCase: GetTodayTasksUseCase
) {
    suspend operator fun invoke(
        instruction: String,
        date: LocalDate = LocalDate.now(),
        now: LocalTime = LocalTime.now()
    ): Result<ReplanResult> {
        val profile = getUserProfileUseCase().first()
        val currentTasks = getTodayTasksUseCase(date).first()
        return scheduleRepository.replan(profile, currentTasks, now, instruction)
    }
}
