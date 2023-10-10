package com.palrasp.myapplication.CalendarClasses

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime


data class Event( val id: Long,
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    var description: String,
    val className: String,
){
    constructor() : this(0,"", Color.Unspecified, LocalDateTime.now(), LocalDateTime.now(), "","")

}
