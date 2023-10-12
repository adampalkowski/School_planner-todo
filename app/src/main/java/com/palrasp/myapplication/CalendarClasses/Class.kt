package com.palrasp.myapplication.CalendarClasses

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDateTime

import com.google.gson.Gson
import java.io.Serializable

@Composable
fun rememberEvent(initialEvent: Event): MutableState<Event> {
    val savedState = rememberSaveable(saver = EventSaver) {
        initialEvent
    }

    val state = remember { mutableStateOf(savedState) }

    return state
}

object EventSaver : Saver<Event, java.io.Serializable> {
    override fun SaverScope.save(value: Event): java.io.Serializable {
        val map = mutableMapOf<String, Serializable>()
        map["id"] = value.id
        map["name"] = value.name
        map["color"] = value.color.toArgb()
        map["start"] = value.start.toString()
        map["end"] = value.end.toString()
        map["description"] = value.description
        map["className"] = value.className
        map["recurrenceJson"] = value.recurrenceJson
        map["compulsory"] = value.compulsory
        return map as java.io.Serializable
    }

    override fun restore(value: java.io.Serializable): Event {
        if (value is Map<*, *>) {
            return Event(
                id = (value["id"] as? Long) ?: 0,
                name = value["name"] as? String ?: "",
                color = Color(value["color"] as Int),
                start = LocalDateTime.parse(value["start"] as String),
                end = LocalDateTime.parse(value["end"] as String),
                description = value["description"] as? String ?: "",
                className = value["className"] as? String ?: "",
                recurrenceJson = value["recurrenceJson"] as? String ?: "",
                compulsory = value["compulsory"] as? Boolean ?: true
            )
        }
        return Event() // Default event if restoration fails
    }
}

data class Event(
    var id: Long,
    var name: String,
    var color: Color,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var description: String,
    var className: String,
    var recurrenceJson: String, // Store Recurrence as JSON string
    var compulsory:Boolean,

){
    constructor() : this(0,"", Color.Unspecified, LocalDateTime.now(), LocalDateTime.now(), "","","",true)
    val extractedLines: List<String>
        get() = description.lines()
            .filter { it.startsWith("[-]") }
            .map { it.removePrefix("[-]").trim() }
    val extractedLinesWithIndices: List<Pair<Int, String>>
        get() = description.lines()
            .filterIndexed { index, line -> line.startsWith("[-]") }
            .mapIndexed { index, line -> index to line.removePrefix("[-]").trim() }
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