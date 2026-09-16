package com.example.aiplanner.utils

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Time/date formatting and arithmetic helpers shared across the app.
 *
 * All persisted times are stored as "HH:mm" strings and dates as ISO "yyyy-MM-dd" strings so
 * they map directly onto the JSON schedule format used by the backend in later phases.
 */

private val TIME_STORAGE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)
private val TIME_DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
private val DATE_DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())

fun LocalTime.toStorageString(): String = format(TIME_STORAGE_FORMATTER)

fun String.toLocalTimeOrNull(): LocalTime? = runCatching { LocalTime.parse(this, TIME_STORAGE_FORMATTER) }.getOrNull()

fun LocalTime.toDisplayString(): String = format(TIME_DISPLAY_FORMATTER)

fun LocalDate.toStorageString(): String = this.toString() // ISO-8601 yyyy-MM-dd

fun String.toLocalDateOrNull(): LocalDate? = runCatching { LocalDate.parse(this) }.getOrNull()

fun LocalDate.toDisplayString(): String = format(DATE_DISPLAY_FORMATTER)

/** Minutes between [start] and [end], never negative (returns 0 if [end] is not after [start]). */
fun minutesBetween(start: LocalTime, end: LocalTime): Long =
    Duration.between(start, end).toMinutes().coerceAtLeast(0)

/**
 * Adds [minutes] to this time, clamping at 23:59 instead of wrapping past midnight.
 * Used by the local schedule generator so a late wake-up/sleep time never silently
 * produces a task that "starts" after it "ends" due to a day rollover.
 */
fun LocalTime.safePlusMinutes(minutes: Long): LocalTime {
    val totalSeconds = this.toSecondOfDay() + minutes * 60
    return if (totalSeconds >= 24 * 3600) LocalTime.of(23, 59) else LocalTime.ofSecondOfDay(totalSeconds)
}

/**
 * Subtracts [minutes] from this time, clamping at 00:00 instead of wrapping to the previous day.
 */
fun LocalTime.safeMinusMinutes(minutes: Long): LocalTime {
    val totalSeconds = this.toSecondOfDay() - minutes * 60
    return if (totalSeconds <= 0) LocalTime.MIDNIGHT else LocalTime.ofSecondOfDay(totalSeconds)
}
