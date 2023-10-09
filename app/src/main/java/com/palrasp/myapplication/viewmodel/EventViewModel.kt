package com.palrasp.myapplication.viewmodel

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


class EventViewModel(private val eventDao: EventDao) : ViewModel() {

    // LiveData to hold the list of events
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    val allEvents: StateFlow<List<Event>> = _allEvents.asStateFlow()

    init {
        viewModelScope.launch {
            // Fetch initial data from the database and emit it to the StateFlow

            val eventEntities = eventDao.getAllEventsLiveData()

            _allEvents.value = eventEntities.map { it.toEvent() }

        }
    }

    // Function to insert an event into the database
    // Function to insert an event into the database
    suspend fun insertEvent(event: Event) {
        withContext(Dispatchers.IO) {
            val entity = event.toEventEntity()
            eventDao.insertEvent(entity)
        }
    }
}
fun Event.toEventEntity(): EventEntity {
    return EventEntity(
        name = this.name,
        color = this.color.toArgb(), // Convert Color to Int
        start = this.start,
        end = this.end,
        description = this.description
    )
}

fun EventEntity.toEvent(): Event {
    return Event(
        name = this.name,
        color = Color(this.color), // Convert Int to Color
        start = this.start,
        end = this.end,
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