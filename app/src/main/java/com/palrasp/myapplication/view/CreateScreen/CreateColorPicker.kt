package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.ui.theme.Lexend

val createTextStyle=TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 16.sp)
@Composable
fun CreateColorPicker(eventState:MutableState<Event>){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(color = eventState.value.color).padding(horizontal = 24.dp, vertical = 12.dp)){
        Text(text = "Color", style = createTextStyle, color = Color.White)
    }
}