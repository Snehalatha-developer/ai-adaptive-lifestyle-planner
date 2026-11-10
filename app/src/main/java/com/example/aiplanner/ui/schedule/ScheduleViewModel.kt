package com.example.aiplanner.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.usecase.CompleteTaskUseCase
import com.example.aiplanner.domain.usecase.GetTodayTasksUseCase
import com.example.aiplanner.domain.usecase.ReplanScheduleUseCase
import com.example.aiplanner.domain.usecase.SkipTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val tasks: List<ScheduleTask> = emptyList(),
    val now: LocalTime = LocalTime.now(),
    val selectedTask: ScheduleTask? = null,
    val isReplanning: Boolean = false
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val skipTaskUseCase: SkipTaskUseCase,
    private val replanScheduleUseCase: ReplanScheduleUseCase
) : ViewModel() {

    private val ticker: Flow<LocalTime> = flow {
        while (true) {
            emit(LocalTime.now())
            delay(30_000)
        }
    }
    private val selectedTaskId = MutableStateFlow<String?>(null)
    private val isReplanning = MutableStateFlow(false)

    val uiState: StateFlow<ScheduleUiState> = combine(
        getTodayTasksUseCase(),
        ticker,
        selectedTaskId,
        isReplanning
    ) { tasks, now, selectedId, replanning ->
        val sorted = tasks.sortedBy { it.startTime }
        ScheduleUiState(
            isLoading = false,
            tasks = sorted,
            now = now,
            selectedTask = sorted.firstOrNull { it.id == selectedId },
            isReplanning = replanning
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ScheduleUiState())

    private val _events = Channel<String>(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    fun selectTask(taskId: String?) {
        selectedTaskId.value = taskId
    }

    fun completeSelectedTask() {
        val task = uiState.value.selectedTask ?: return
        viewModelScope.launch {
            completeTaskUseCase(task.id)
            selectTask(null)
        }
    }

    fun skipSelectedTask() {
        val task = uiState.value.selectedTask ?: return
        viewModelScope.launch {
            skipTaskUseCase(task.id)
            selectTask(null)
        }
    }

    /** Notification-backed snoozing arrives in Phase 2; for now this just informs the user. */
    fun remindLater() {
        selectTask(null)
        viewModelScope.launch { _events.send("Reminders arrive in Phase 2") }
    }

    /** Asks the AI planner to reflow the rest of today's tasks around the selected task. */
    fun reschedule() {
        val task = uiState.value.selectedTask ?: return
        selectTask(null)
        viewModelScope.launch {
            isReplanning.value = true
            val instruction = if (task.status.name == "SKIPPED") {
                "I just skipped '${task.title}' (${task.startTime}-${task.endTime}). " +
                    "Reflow the remaining tasks for the rest of today accordingly."
            } else {
                "Reschedule '${task.title}' (currently ${task.startTime}-${task.endTime}) to a better " +
                    "slot later today, adjusting surrounding tasks minimally."
            }
            replanScheduleUseCase(instruction)
                .onSuccess { _events.send(it.assistantMessage) }
                .onFailure { _events.send("Couldn't reach the AI planner — your schedule is unchanged") }
            isReplanning.value = false
        }
    }
}
