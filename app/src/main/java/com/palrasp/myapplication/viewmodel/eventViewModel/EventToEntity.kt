package com.palrasp.myapplication.viewmodel.eventViewModel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.data.local.entities.EventEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Event.toEventEntity(): EventEntity {
    return EventEntity(
        id=this.id,
        name = this.name,
        color = this.color.toArgb(), // Convert Color to Int
        start = this.start.toString(),
        end = this.end.toString(),
        description = this.description,
        className = this.className,
        recurrenceJson=this.recurrenceJson,
        compulsory = this.compulsory,
        dayOfTheWeek=this.dayOfTheWeek
    )
}

fun EventEntity.toEvent(): Event {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    return Event(id=this.id,
        name = this.name,
        color = Color(this.color), // Convert Int to Color
        start = LocalDateTime.parse(this.start, formatter), // Parse String to LocalDateTime
        end = LocalDateTime.parse(this.end, formatter), // Parse String to LocalDateTime
        description = this.description,
        className = this.className,
        recurrenceJson=this.recurrenceJson,
        compulsory = this.compulsory,
        dayOfTheWeek=this.dayOfTheWeek

    )
}

