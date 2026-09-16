package com.example.aiplanner.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.usecase.GetTodayTasksUseCase
import com.example.aiplanner.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val isLoading: Boolean = true,
    val completionPercent: Int = 0,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val skippedCount: Int = 0,
    val exerciseCompleted: Boolean = false,
    val studyMinutesCompleted: Int = 0,
    val studyMinutesTarget: Int = 0
)

/**
 * Today-only progress for now. Weekly consistency and completion history are added in Phase 6
 * once there's more than a single day of Room data to summarize.
 */
@HiltViewModel
class ProgressViewModel @Inject constructor(
    getTodayTasksUseCase: GetTodayTasksUseCase,
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        getTodayTasksUseCase(),
        getUserProfileUseCase()
    ) { tasks, profile ->
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val skipped = tasks.count { it.status == TaskStatus.SKIPPED }
        val exerciseCompleted = tasks.any { it.category == TaskCategory.EXERCISE && it.status == TaskStatus.COMPLETED }
        val studyMinutesCompleted = tasks
            .filter { it.category == TaskCategory.STUDY && it.status == TaskStatus.COMPLETED }
            .sumOf { it.durationMinutes }
            .toInt()
        ProgressUiState(
            isLoading = false,
            completionPercent = if (tasks.isEmpty()) 0 else completed * 100 / tasks.size,
            completedCount = completed,
            totalCount = tasks.size,
            skippedCount = skipped,
            exerciseCompleted = exerciseCompleted,
            studyMinutesCompleted = studyMinutesCompleted,
            studyMinutesTarget = profile.studyTargetMinutes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}
