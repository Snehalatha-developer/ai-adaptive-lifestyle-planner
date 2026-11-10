package com.example.aiplanner.data.remote.dto

import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.model.UserProfile
import com.example.aiplanner.domain.model.WeekendPreference
import com.example.aiplanner.utils.toLocalDateOrNull
import com.example.aiplanner.utils.toLocalTimeOrNull
import com.example.aiplanner.utils.toStorageString
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlinx.serialization.Serializable

/** Wire format for the AI planner proxy (see /server). Mirrors the backend's zod schema. */

@Serializable
data class UserProfileDto(
    val name: String,
    val wakeUpTime: String,
    val sleepTime: String,
    val officeStartTime: String,
    val officeEndTime: String,
    val commuteMinutes: Int,
    val exerciseMinutes: Int,
    val studyTargetMinutes: Int,
    val breakfastTime: String,
    val lunchTime: String,
    val dinnerTime: String,
    val weekendPreference: String,
    val goals: List<String>
)

@Serializable
data class ScheduleTaskDto(
    val id: String,
    val title: String,
    val description: String?,
    val category: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val notificationEnabled: Boolean,
    val status: String
)

@Serializable
data class GenerateRequest(val profile: UserProfileDto, val date: String)

@Serializable
data class GenerateResponse(val tasks: List<ScheduleTaskDto>)

@Serializable
data class ReplanRequest(
    val profile: UserProfileDto,
    val currentTasks: List<ScheduleTaskDto>,
    val now: String,
    val instruction: String
)

@Serializable
data class ReplanResponse(val tasks: List<ScheduleTaskDto>, val assistantMessage: String)

fun UserProfile.toDto(): UserProfileDto = UserProfileDto(
    name = name,
    wakeUpTime = wakeUpTime.toStorageString(),
    sleepTime = sleepTime.toStorageString(),
    officeStartTime = officeStartTime.toStorageString(),
    officeEndTime = officeEndTime.toStorageString(),
    commuteMinutes = commuteMinutes,
    exerciseMinutes = exerciseMinutes,
    studyTargetMinutes = studyTargetMinutes,
    breakfastTime = breakfastTime.toStorageString(),
    lunchTime = lunchTime.toStorageString(),
    dinnerTime = dinnerTime.toStorageString(),
    weekendPreference = weekendPreference.name,
    goals = goals.toList()
)

fun ScheduleTask.toDto(): ScheduleTaskDto = ScheduleTaskDto(
    id = id,
    title = title,
    description = description,
    category = category.name,
    date = date.toStorageString(),
    startTime = startTime.toStorageString(),
    endTime = endTime.toStorageString(),
    notificationEnabled = notificationEnabled,
    status = status.name
)

/** Converts an AI response task into a domain [ScheduleTask] attached to [scheduleId]. */
fun ScheduleTaskDto.toDomain(scheduleId: String, fallbackDate: LocalDate): ScheduleTask {
    val startTime = startTime.toLocalTimeOrNull() ?: LocalTime.MIDNIGHT
    return ScheduleTask(
        id = id.ifBlank { UUID.randomUUID().toString() },
        scheduleId = scheduleId,
        title = title,
        description = description,
        category = runCatching { TaskCategory.valueOf(category) }.getOrDefault(TaskCategory.PERSONAL),
        date = date.toLocalDateOrNull() ?: fallbackDate,
        startTime = startTime,
        endTime = endTime.toLocalTimeOrNull() ?: startTime,
        notificationEnabled = notificationEnabled,
        status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.PENDING),
        plannedStartTime = startTime,
        actualCompletionTime = null,
        snoozeCount = 0
    )
}
