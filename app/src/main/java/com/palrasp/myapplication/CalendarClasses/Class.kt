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
        map["dayOfTheWeek"] = value.dayOfTheWeek // Save dayOfTheWeek

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
                compulsory = value["compulsory"] as? Boolean ?: true,
                dayOfTheWeek = (value["dayOfTheWeek"] as? Int) ?: 0 // Restore dayOfTheWeek

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
    var compulsory: Boolean,
    var dayOfTheWeek: Int // New property for day of the week
) {
    constructor() : this(
        0, "", Color.Unspecified, LocalDateTime.now(), LocalDateTime.now(), "", "", "", true, 0
    )
    val extractedLines: List<Pair<Int, String>>
        get() = description.lines()
            .mapIndexedNotNull { index, line ->
                if (line.startsWith("[-]") || line.startsWith("[x]")) {
                    index to line.removePrefix("[-]").removePrefix("[x]")
                } else {
                    null
                }
            }
    val extractedLinesWithIndices: List<Triple<Int, String,Boolean>>
        get() = description.lines()
            .mapIndexedNotNull { index, line ->
                if (line.startsWith("[-]") || line.startsWith("[x]")) {
                    val isChecked = line.startsWith("[x]")

                    val cleanedLine = line.removePrefix("[-]").removePrefix("[x]")
                    Triple(index, cleanedLine, isChecked)
                } else {
                    null
                }
            }
    val checkedLinesRatio: Float
        get() {
            val extractedLines = extractedLinesWithIndices
            val totalLines = extractedLines.size
            val checkedLines = extractedLines.count { it.third }
            return if (totalLines > 0) {
                checkedLines.toFloat() / totalLines
            } else {
                0.0f // Return 0 if there are no lines
            }
        }
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