package com.example.aiplanner.domain.usecase

import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.repository.ScheduleRepository
import com.example.aiplanner.utils.SampleScheduleGenerator
import java.time.LocalDate
import javax.inject.Inject

/**
 * Builds a schedule for [date] from the user's profile using the local, rule-based generator
 * and persists it as the active schedule for that day.
 *
 * This is a placeholder for the AI schedule generator introduced in Phase 4 — it deliberately
 * implements the same contract (profile in, validated non-overlapping schedule out) so swapping
 * it for a real backend call later is a one-line change at the call sites.
 */
class GenerateLocalScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(profile: UserProfile, date: LocalDate = LocalDate.now()) {
        val tasks = SampleScheduleGenerator.generate(profile, date)
        scheduleRepository.replaceScheduleForDate(date, tasks)
    }
}
