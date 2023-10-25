package com.palrasp.myapplication

import com.palrasp.myapplication.view.CalendarEvents
import com.palrasp.myapplication.viewmodel.EventViewModel

suspend fun updateEventDescription(event: CalendarEvents.UpdateEvent, eventViewModel: EventViewModel) {
    val eventIndex = event.eventIndex
    val updatedDescription = event.event.description
        .split("\n") // Split the description into lines
        .mapIndexed { index, line ->
            if (index == eventIndex && line.startsWith(
                    "[-]"
                )
            ) {
                // Replace "[-]" with "[x]" only at the specified index
                line.replaceFirst(
                    "[-]",
                    "[x]"
                )
            } else {
                line
            }
        }
        .joinToString("\n") // Join the modified lines back together
    val updatedEvent = event.event.copy(description = updatedDescription)
    eventViewModel.updateEvent(updatedEvent = updatedEvent)
}