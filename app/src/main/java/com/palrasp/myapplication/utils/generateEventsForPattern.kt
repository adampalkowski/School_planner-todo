package com.palrasp.myapplication.utils

import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.CalendarClasses.RecurrencePattern
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun generateEventsForPattern(
    pattern: RecurrencePattern,
    startDate: LocalDate,
    daysUntilSelectedDay: Int,
    createdEvent: Event
): List<com.palrasp.myapplication.CalendarClasses.Event> {
    val totalDays = ChronoUnit.DAYS.between(startDate, startDate.plusMonths(6))
    val totalWeeks = totalDays / 7

    return (0 until totalWeeks).map { week ->
        val currentDate = when (pattern) {
            RecurrencePattern.WEEKLY -> startDate.plusDays(week * 7 + daysUntilSelectedDay.toLong())
            RecurrencePattern.TWOWEEKS -> startDate.plusDays(week * 14 + daysUntilSelectedDay.toLong())
            RecurrencePattern.MONTHLY -> startDate.plusMonths(week)
            RecurrencePattern.DAILY -> startDate.plusDays(week)
            else -> startDate.plusDays(week * 7 + daysUntilSelectedDay.toLong())
        }

        val startTime = createdEvent.start.toLocalTime()
        val endTime = createdEvent.end.toLocalTime()
        val startDateTime = currentDate.atTime(startTime)
        val endDateTime = currentDate.atTime(endTime)

        com.palrasp.myapplication.CalendarClasses.Event(
            id = generateRandomId(),
            name = createdEvent.name,
            color = createdEvent.color,
            start = startDateTime,
            end = endDateTime,
            description = "\n\n\n\n\n\n\n\n",
            className = createdEvent.className,
            recurrenceJson = "",
            compulsory = createdEvent.compulsory,
            dayOfTheWeek = createdEvent.dayOfTheWeek
        )
    }
}