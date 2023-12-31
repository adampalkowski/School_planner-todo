package com.palrasp.myapplication.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.palrasp.myapplication.data.local.LocalDateTimeConverter
import com.palrasp.myapplication.data.local.dao.EventDao
import com.palrasp.myapplication.data.local.entities.EventEntity
@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                )
                    .fallbackToDestructiveMigration() // Add this line
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
