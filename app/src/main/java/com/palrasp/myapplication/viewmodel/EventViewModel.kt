package com.palrasp.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.Screen
import com.palrasp.myapplication.data.local.dao.EventDao
import com.palrasp.myapplication.data.local.entities.EventEntity
import com.palrasp.myapplication.generateRandomId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class EventViewModel(private val eventDao: EventDao) : ViewModel() {

    // LiveData to hold the list of events
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    val allEvents: StateFlow<List<Event>> = _allEvents.asStateFlow()
    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.Calendar)
    val currentClass: MutableState<Event> = mutableStateOf(Event(
        id = generateRandomId(),
        name = "",
        color = Color(0xFF7DC1FF),
        start = LocalDateTime.of(LocalDate.now(), LocalTime.of(12,0)),
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(13,30)),
        description = "",
        className = "",
        recurrenceJson = "",
        compulsory = true,
        dayOfTheWeek = 1
    ))

    suspend fun getEvents()  {
        withContext(Dispatchers.IO) {
            val eventEntities = eventDao.getAllEventsLiveData()
            _allEvents.value = eventEntities.map { it.toEvent() }
        }
    }
    suspend fun getEventsForWeek(startDate:String,endDate:String)  {
        withContext(Dispatchers.IO) {
            val eventEntities = eventDao.getEventsWithinWeek(startDate,endDate)
            Log.d("getEventsForWeek",eventEntities.size.toString()+"asdsa")
            _allEvents.value = eventEntities.map { it.toEvent() }
        }
    }

    // Function to insert an event into the database
    suspend fun insertEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = event.toEventEntity()
            eventDao.insertEvent(entity)
        }
    }  // Function to update the notes of an event in the database
    suspend fun insertEvents(events: List<Event>) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = events.map { it.toEventEntity() }
            eventDao.insertEvents(entities)
        }
    }

    suspend fun updateEvent(updatedEvent: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            // Convert the updated event to an entity
            val updatedEntity = updatedEvent.toEventEntity()

            // Update the event in the database
            eventDao.updateEvent(updatedEntity)
            _allEvents.update { events ->
                val updatedEventIndex = events.indexOfFirst { it.id == updatedEvent.id }
                if (updatedEventIndex != -1) {
                    events.toMutableList().apply {
                        set(updatedEventIndex, updatedEvent)
                    }
                } else {
                    events
                }
            }
        }
    }
    suspend fun deleteEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = event.toEventEntity()
            Log.d("EventViewModel","delete event")
            eventDao.deleteEvent(entity)

        }
    }
    suspend fun deleteAllEvents(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = event.toEventEntity()
            Log.d("EventViewModel", "delete event")

            // Delete all events with the same name and start time
            eventDao.deleteSimilarEvents(entity.name)

        }
    }
}
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

class EventViewModelFactory(private val eventDao: EventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}