package com.palrasp.myapplication.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.palrasp.myapplication.data.local.dao.EventDao
import com.palrasp.myapplication.data.local.entities.EventEntity

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}