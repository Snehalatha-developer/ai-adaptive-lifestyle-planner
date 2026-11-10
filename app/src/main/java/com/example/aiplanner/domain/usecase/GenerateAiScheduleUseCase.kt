package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.repository.ScheduleRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * AI-backed sibling of [GenerateLocalScheduleUseCase]: builds a schedule for [date] from the
 * user's profile via the AI planner proxy and persists it. Returns failure (without touching the
 * existing schedule) if the proxy is unreachable or returns invalid output — callers should fall
 * back to [GenerateLocalScheduleUseCase] in that case.
 */
class GenerateAiScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(profile: UserProfile, date: LocalDate = LocalDate.now()): Result<Unit> =
        scheduleRepository.generateAiSchedule(profile, date).map { }
}
