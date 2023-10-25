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
import com.palrasp.myapplication.utils.generateRandomId
import com.palrasp.myapplication.utils.sampleEvent
import com.palrasp.myapplication.viewmodel.eventViewModel.toEvent
import com.palrasp.myapplication.viewmodel.eventViewModel.toEventEntity
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

    private val _currentClass = mutableStateOf(Event(
        id = generateRandomId(),
        name = "",
        color = Color(0xFF7DC1FF),
        start = LocalDateTime.of(LocalDate.now(), LocalTime.of(12,0)),
        end = LocalDateTime.of(LocalDate.now(), LocalTime.of(13,30)),
        description = "\n\n\n\n",
        className = "",
        recurrenceJson = "",
        compulsory = true,
        dayOfTheWeek = 1
    ))
    val currentClass: MutableState<Event> = _currentClass
    suspend fun getEvents()  {
        withContext(Dispatchers.IO) {
            val eventEntities = eventDao.getAllEventsLiveData()
            _allEvents.value = eventEntities.map { it.toEvent() }
        }
    }
    suspend fun getEventsForWeek(startDate:String,endDate:String)  {
        withContext(Dispatchers.IO) {
            val eventEntities = eventDao.getEventsWithinWeek(startDate,endDate)
            _allEvents.value = eventEntities.map { it.toEvent() }
        }
    }
    suspend fun updateEvents(event: Event,newEvent: Event)  {
        withContext(Dispatchers.IO) {
            var filteredEvents = eventDao.getEventsByColorClassNameCompulsory(event.color.toArgb(), event.name, event.compulsory)
            val updatedEvents = filteredEvents.map { existingEvent ->
                existingEvent.copy(
                    color = newEvent.color.toArgb(),
                    name = newEvent.name,
                    className = newEvent.className
                )
            }
            eventDao.updateEvents(updatedEvents)

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
    fun setCurrentClass(event: Event){
        _currentClass.value=event
    }
    suspend fun resetCurrentClass() {
        _currentClass.value=Event(
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
        )
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
                    val updatedEvents = events.toMutableList()

                    updatedEvents[updatedEventIndex] = updatedEvent
                    updatedEvents.toList()
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
