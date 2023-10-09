package com.palrasp.myapplication.data.local

import androidx.room.TypeConverter
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.data.local.dao.EventDao
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsRepository(private val eventDao: EventDao) {


}

class LocalDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }
}