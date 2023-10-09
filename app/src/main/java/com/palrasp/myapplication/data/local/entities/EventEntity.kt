package com.palrasp.myapplication.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val color: Int, // You may need to convert Color to Int

    @ColumnInfo(name = "start")
    val start: String, // Changed to String

    @ColumnInfo(name = "end")
    val end: String, // Changed to String

    @ColumnInfo(name = "description")
    val description: String?
)