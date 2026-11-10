package com.example.aiplanner.ui.aiplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.usecase.ReplanScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ChatRole { USER, ASSISTANT }

data class ChatMessage(val id: String, val role: ChatRole, val text: String)

data class AiPlannerUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            role = ChatRole.ASSISTANT,
            text = "Tell me what to change — e.g. \"move my workout to the evening\" or " +
                "\"I'm sick, clear my afternoon\". You can also quick-add a task with the + button."
        )
    ),
    val inputText: String = "",
    val isSending: Boolean = false
)

/**
 * Backs the conversational "Ask AI" screen. Every send — whether typed chat text or a quick-add
 * form — flows through the same [ReplanScheduleUseCase], just with a different instruction string.
 */
@HiltViewModel
class AiPlannerViewModel @Inject constructor(
    private val replanScheduleUseCase: ReplanScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiPlannerUiState())
    val uiState: StateFlow<AiPlannerUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isSending) return
        submit(userVisibleText = text, instruction = text)
    }

    /** Quick-add an ad-hoc task; synthesizes an instruction so no separate backend flow is needed. */
    fun addAdHocTask(title: String, durationMinutes: Int, category: TaskCategory) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank() || _uiState.value.isSending) return
        val userVisibleText = "Add \"$trimmedTitle\" ($durationMinutes min, ${category.name.lowercase()})"
        val instruction = "Add a new task titled \"$trimmedTitle\" lasting $durationMinutes minutes, " +
            "category ${category.name}, at the best available slot today. Adjust surrounding tasks minimally."
        submit(userVisibleText, instruction)
    }

    private fun submit(userVisibleText: String, instruction: String) {
        val userMessage = ChatMessage(UUID.randomUUID().toString(), ChatRole.USER, userVisibleText)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            inputText = "",
            isSending = true
        )
        viewModelScope.launch {
            replanScheduleUseCase(instruction)
                .onSuccess { result -> appendAssistantMessage(result.assistantMessage) }
                .onFailure {
                    appendAssistantMessage(
                        "Couldn't reach the AI planner — your schedule is unchanged. Try again in a moment."
                    )
                }
            _uiState.value = _uiState.value.copy(isSending = false)
        }
    }

    private fun appendAssistantMessage(text: String) {
        val message = ChatMessage(UUID.randomUUID().toString(), ChatRole.ASSISTANT, text)
        _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + message)
    }
}
