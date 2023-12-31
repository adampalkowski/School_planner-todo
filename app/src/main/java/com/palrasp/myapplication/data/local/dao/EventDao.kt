package com.palrasp.myapplication.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.data.local.entities.EventEntity

@Dao
interface EventDao {
    // Update events based on name, dayOfWeek, and color
    @Update
    suspend fun updateEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE start BETWEEN :weekStart AND :weekEnd")
    fun getEventsWithinWeek(weekStart: String, weekEnd: String): List<EventEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)
    @Insert
    suspend fun insertEvent(event: EventEntity)
    @Delete
    suspend fun deleteEvent(event: EventEntity)
    @Update
    suspend fun updateEvent(event: EventEntity)
    @Query("SELECT * FROM events")
    fun getAllEventsLiveData(): List<EventEntity> // Return a List of EventEntity
    @Query("DELETE FROM events WHERE name = :eventName")
    suspend fun deleteSimilarEvents(eventName: String)
    @Query("SELECT * FROM events WHERE color = :color AND name = :name AND compulsory = :compulsory")
    fun getEventsByColorClassNameCompulsory(color: Int, name: String, compulsory: Boolean): List<EventEntity>
    // Add other queries and operations as needed
}