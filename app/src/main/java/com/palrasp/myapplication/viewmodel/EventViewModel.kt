package com.palrasp.myapplication.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.data.local.dao.EventDao
import com.palrasp.myapplication.data.local.entities.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class EventViewModel(private val eventDao: EventDao) : ViewModel() {

    // LiveData to hold the list of events
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    val allEvents: StateFlow<List<Event>> = _allEvents.asStateFlow()

    suspend fun getEvents()  {
        withContext(Dispatchers.IO) {
            val eventEntities = eventDao.getAllEventsLiveData()
            _allEvents.value = eventEntities.map { it.toEvent() }
        }
    }

    // Function to insert an event into the database
    suspend fun insertEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = event.toEventEntity()
            eventDao.insertEvent(entity)
            Log.d("EventViewModel","insert event")

        }
    }
    suspend fun deleteEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = event.toEventEntity()
            Log.d("EventViewModel","delete event")
            eventDao.deleteEvent(entity)
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
        description = this.description
    )
}

fun EventEntity.toEvent(): Event {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    return Event(id=this.id,
        name = this.name,
        color = Color(this.color), // Convert Int to Color
        start = LocalDateTime.parse(this.start, formatter), // Parse String to LocalDateTime
        end = LocalDateTime.parse(this.end, formatter), // Parse String to LocalDateTime
        description = this.description
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