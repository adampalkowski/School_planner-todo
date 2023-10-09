package com.palrasp.myapplication.CalendarClasses

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime


data class Event(
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
){
    constructor() : this("", Color.Unspecified, LocalDateTime.now(), LocalDateTime.now(), "")

}
