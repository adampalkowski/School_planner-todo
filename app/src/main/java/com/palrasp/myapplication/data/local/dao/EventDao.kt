package com.palrasp.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.palrasp.myapplication.data.local.entities.EventEntity

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: EventEntity)

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<EventEntity>

    // Add other queries and operations as needed
}