package com.example.aiplanner.utils

import com.example.aiplanner.domain.model.ScheduleTask
import com.example.aiplanner.domain.model.TaskCategory
import com.example.aiplanner.domain.model.TaskStatus
import com.example.aiplanner.domain.model.UserProfile
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Builds a realistic, non-overlapping day plan from a [UserProfile] using simple scheduling
 * rules (no AI/network call). This stands in for the AI schedule generator that arrives in
 * Phase 4 — it follows the same principles the AI will later be asked to follow:
 *
 *  - Fixed commitments (office hours) are never moved or shortened.
 *  - Activities never overlap; each task starts where the previous one ends.
 *  - Getting ready, commute, meals and a wind-down buffer before sleep are always reserved.
 *  - If there isn't enough time for a flexible activity (e.g. morning workout), it is dropped
 *    rather than forced in and overlapping something else.
 */
object SampleScheduleGenerator {

    fun generate(profile: UserProfile, date: LocalDate): List<ScheduleTask> {
        val scheduleId = "local-${date}-${UUID.randomUUID().toString().take(8)}"
        val tasks = mutableListOf<ScheduleTask>()

        fun addTask(
            title: String,
            category: TaskCategory,
            start: LocalTime,
            end: LocalTime,
            notification: Boolean = true,
            description: String? = null
        ) {
            if (!end.isAfter(start)) return // guard: never emit a zero/negative-length task
            tasks += ScheduleTask(
                id = UUID.randomUUID().toString(),
                scheduleId = scheduleId,
                title = title,
                description = description,
                category = category,
                date = date,
                startTime = start,
                endTime = end,
                notificationEnabled = notification,
                status = TaskStatus.PENDING,
                plannedStartTime = start,
                actualCompletionTime = null,
                snoozeCount = 0
            )
        }

        val departTime = profile.officeStartTime.safeMinusMinutes(profile.commuteMinutes.toLong())
        var studyMinutesUsed = 0L

        // 1. Wake up
        addTask("Wake Up", TaskCategory.HEALTH, profile.wakeUpTime, profile.wakeUpTime.safePlusMinutes(10))
        var cursor = profile.wakeUpTime.safePlusMinutes(10)

        // 2. Morning workout — only if it fits before the user needs to leave for the office
        val workoutEnd = cursor.safePlusMinutes(profile.exerciseMinutes.toLong())
        if (profile.exerciseMinutes > 0 && !workoutEnd.isAfter(departTime)) {
            addTask("Morning Workout", TaskCategory.EXERCISE, cursor, workoutEnd)
            cursor = workoutEnd
        }

        // 3. Get ready
        val getReadyEnd = cursor.safePlusMinutes(20)
        if (!getReadyEnd.isAfter(departTime)) {
            addTask("Get Ready", TaskCategory.PERSONAL, cursor, getReadyEnd, notification = false)
            cursor = getReadyEnd
        }

        // 4. Breakfast
        val breakfastStart = if (profile.breakfastTime.isAfter(cursor)) profile.breakfastTime else cursor
        val breakfastEnd = breakfastStart.safePlusMinutes(25)
        if (!breakfastEnd.isAfter(departTime)) {
            addTask("Breakfast", TaskCategory.MEAL, breakfastStart, breakfastEnd)
            cursor = breakfastEnd
        }

        // 5. Morning study block with whatever time remains before commute
        val minutesBeforeCommute = minutesBetween(cursor, departTime)
        if (minutesBeforeCommute >= 20) {
            val block = minOf(minutesBeforeCommute - 5, profile.studyTargetMinutes.toLong())
            if (block >= 15) {
                val studyEnd = cursor.safePlusMinutes(block)
                addTask("Study", TaskCategory.STUDY, cursor, studyEnd)
                cursor = studyEnd
                studyMinutesUsed += block
            }
        }

        // 6. Commute to office (fixed, ends exactly at office start)
        if (profile.commuteMinutes > 0 && departTime.isBefore(profile.officeStartTime)) {
            addTask("Commute to Office", TaskCategory.PERSONAL, departTime, profile.officeStartTime)
        }

        // 7. Office — a fixed commitment; split around lunch if lunch falls inside office hours
        if (profile.lunchTime.isAfter(profile.officeStartTime) && profile.lunchTime.isBefore(profile.officeEndTime)) {
            addTask("Office", TaskCategory.WORK, profile.officeStartTime, profile.lunchTime, notification = false)
            val lunchEnd = profile.lunchTime.safePlusMinutes(30)
            val cappedLunchEnd = if (lunchEnd.isAfter(profile.officeEndTime)) profile.officeEndTime else lunchEnd
            addTask("Lunch", TaskCategory.MEAL, profile.lunchTime, cappedLunchEnd)
            if (cappedLunchEnd.isBefore(profile.officeEndTime)) {
                addTask("Office", TaskCategory.WORK, cappedLunchEnd, profile.officeEndTime, notification = false)
            }
        } else {
            addTask("Office", TaskCategory.WORK, profile.officeStartTime, profile.officeEndTime, notification = false)
        }
        cursor = profile.officeEndTime

        // 8. Commute home (fixed length, right after office ends)
        if (profile.commuteMinutes > 0) {
            val commuteHomeEnd = cursor.safePlusMinutes(profile.commuteMinutes.toLong())
            addTask("Commute Home", TaskCategory.PERSONAL, cursor, commuteHomeEnd, notification = false)
            cursor = commuteHomeEnd
        }

        // 9. Short walk / unwind, if there's room before dinner
        val walkEnd = cursor.safePlusMinutes(15)
        if (walkEnd.isBefore(profile.dinnerTime)) {
            addTask("Short Walk", TaskCategory.PERSONAL, cursor, walkEnd, notification = false)
            cursor = walkEnd
        }

        // 10. Dinner
        val dinnerStart = if (profile.dinnerTime.isAfter(cursor)) profile.dinnerTime else cursor
        val dinnerEnd = dinnerStart.safePlusMinutes(30)
        addTask("Dinner", TaskCategory.MEAL, dinnerStart, dinnerEnd)
        cursor = dinnerEnd

        // 11. Evening study — whatever of the daily target hasn't been used yet, capped so a
        //     20-minute wind-down buffer before sleep is always protected.
        val windDownStart = profile.sleepTime.safeMinusMinutes(20)
        val remainingStudyTarget = (profile.studyTargetMinutes - studyMinutesUsed).coerceAtLeast(0)
        if (remainingStudyTarget > 0 && cursor.isBefore(windDownStart)) {
            val available = minutesBetween(cursor, windDownStart)
            val block = minOf(remainingStudyTarget, available)
            if (block >= 15) {
                val studyEnd = cursor.safePlusMinutes(block)
                addTask("DSA Study", TaskCategory.STUDY, cursor, studyEnd)
                cursor = studyEnd
            }
        }

        // 12. Wind down
        if (cursor.isBefore(windDownStart)) {
            addTask("Wind Down", TaskCategory.PERSONAL, windDownStart, profile.sleepTime, notification = false)
        } else if (cursor.isBefore(profile.sleepTime)) {
            addTask("Wind Down", TaskCategory.PERSONAL, cursor, profile.sleepTime, notification = false)
        }

        // 13. Sleep marker
        val sleepEnd = profile.sleepTime.safePlusMinutes(10)
        addTask("Sleep", TaskCategory.HEALTH, profile.sleepTime, sleepEnd)

        return tasks.sortedWith(compareBy({ it.startTime }, { it.title }))
    }
}
