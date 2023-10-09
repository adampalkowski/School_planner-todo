package com.palrasp.myapplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.data.local.entities.EventEntity

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity)


    @Query("SELECT * FROM events")
    fun getAllEventsLiveData(): List<EventEntity> // Return a List of EventEntity


    // Add other queries and operations as needed
}