package com.palrasp.myapplication.utils


import androidx.compose.ui.graphics.Color
import com.palrasp.myapplication.CalendarClasses.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val sampleEvent = Event(
    id = generateRandomId(),
    name = "",
    color = Color(0xFF7DC1FF),
    start = LocalDateTime.of(
        LocalDate.now(),
        LocalTime.of(12, 0)
    ),
    end = LocalDateTime.of(
        LocalDate.now(),
        LocalTime.of(13, 30)
    ),
    description = "",
    className = "",
    recurrenceJson = "",
    compulsory = true,
    dayOfTheWeek = 1
)