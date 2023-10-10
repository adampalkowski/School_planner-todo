package com.palrasp.myapplication.CalendarClasses

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

import com.google.gson.Gson

data class Event( val id: Long,
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    var description: String,
    val className: String,
    var recurrenceJson: String // Store Recurrence as JSON string

){
    constructor() : this(0,"", Color.Unspecified, LocalDateTime.now(), LocalDateTime.now(), "","","")
    val extractedLines: List<String>
        get() = description.lines()
            .filter { it.startsWith("[-]") }
            .map { it.removePrefix("[-]").trim() }
}
fun Event.getRecurrence(): Recurrence? {
    return recurrenceJson.let {
        if (it.isNotEmpty()){
            val gson = Gson()
            gson.fromJson(it, Recurrence::class.java)
        }else{
            null
        }

    }
}

fun Event.setRecurrence(recurrence: Recurrence?) {
    val gson = Gson()
    recurrenceJson = gson.toJson(recurrence)
}