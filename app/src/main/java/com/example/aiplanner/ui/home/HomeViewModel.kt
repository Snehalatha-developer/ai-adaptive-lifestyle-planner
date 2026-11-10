package com.example.aiplanner.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.usecase.GenerateAiScheduleUseCase
import com.example.aiplanner.domain.usecase.GenerateLocalScheduleUseCase
import com.example.aiplanner.domain.usecase.GetTodayTasksUseCase
import com.example.aiplanner.domain.usecase.GetUserProfileUseCase
import com.example.aiplanner.utils.minutesBetween
import com.example.aiplanner.utils.toDisplayString
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val todayDateDisplay: String = "",
    val completionPercent: Int = 0,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val nextTask: ScheduleTask? = null,
    val nextTaskMinutesUntil: Long = 0,
    val nextTaskInProgress: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val generateLocalScheduleUseCase: GenerateLocalScheduleUseCase,
    private val generateAiScheduleUseCase: GenerateAiScheduleUseCase
) : ViewModel() {

    // Re-evaluates "next activity" / countdown periodically without requiring a data change.
    private val ticker: Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(30_000)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        getUserProfileUseCase(),
        getTodayTasksUseCase(),
        ticker
    ) { profile, tasks, _ ->
        val now = LocalTime.now()
        val next = tasks
            .filter { it.status == TaskStatus.PENDING && it.endTime.isAfter(now) }
            .minByOrNull { it.startTime }
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        HomeUiState(
            isLoading = false,
            userName = profile.name.ifBlank { "there" },
            todayDateDisplay = LocalDate.now().toDisplayString(),
            completionPercent = if (tasks.isEmpty()) 0 else completed * 100 / tasks.size,
            completedCount = completed,
            totalCount = tasks.size,
            nextTask = next,
            nextTaskMinutesUntil = next?.let { minutesBetween(now, it.startTime) } ?: 0,
            nextTaskInProgress = next?.let { !now.isBefore(it.startTime) } ?: false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    private val _events = Channel<String>(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    /** Rebuilds today's schedule via the AI planner, falling back to the local generator offline. */
    fun regenerateSchedule() {
        viewModelScope.launch {
            val profile = getUserProfileUseCase().first()
            generateAiScheduleUseCase(profile)
                .onSuccess { _events.send("Schedule regenerated") }
                .onFailure {
                    generateLocalScheduleUseCase(profile)
                    _events.send("Couldn't reach AI planner — used offline schedule instead")
                }
        }
    }

    fun notifyComingSoon(message: String) {
        viewModelScope.launch { _events.send(message) }
    }
}
