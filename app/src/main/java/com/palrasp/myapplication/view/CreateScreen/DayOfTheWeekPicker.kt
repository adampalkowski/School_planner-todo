package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.view.CreateScreenEvent
import com.palrasp.myapplication.view.confirmColor
import com.palrasp.myapplication.view.dividerColor
import com.palrasp.myapplication.view.textColor
import java.time.DayOfWeek
import java.time.format.TextStyle

val selectedTextStyle= androidx.compose.ui.text.TextStyle(fontFamily = Lexend, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xff292929))

@Composable
fun DayOfTheWeekPicker(event: MutableState<Event>,onEvent:(CreateScreenEvent)->Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Text(text = "Day of the week", style = createTextStyle, color = textColor, modifier = Modifier.align(
            Alignment.CenterStart))

        Box(modifier = Modifier
            .align(Alignment.CenterEnd)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = {
                onEvent(CreateScreenEvent.OpenDayOfWeekPicker)
            })
            .border(BorderStroke(1.dp, color = Color(0xFFD6D6D6)), shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)){
            Text(text =DayOfWeek.of(event.value.dayOfTheWeek).name ,style=selectedTextStyle)
        }
    }
}


@Composable
fun TimePickerSection(event: MutableState<Event>,onEvent:(CreateScreenEvent)->Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Row(verticalAlignment = Alignment.CenterVertically) {

        Text(text = "Time", style = createTextStyle, color = textColor)
        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(BorderStroke(1.dp, color = Color(0xFFD6D6D6)), shape = RoundedCornerShape(6.dp))
            .clickable(onClick = {
                onEvent(CreateScreenEvent.OpenTimeStartPicker)
            })
            .padding(horizontal = 16.dp, vertical = 8.dp)){
            val startTimeText = String.format("%02d:%02d", event.value.start.hour, event.value.start.minute)
            Text(text = startTimeText, style = selectedTextStyle)
        }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(painter = painterResource(id = com.palrasp.myapplication.R.drawable.ic_long_right), contentDescription =null,tint= selectedTextStyle.color )
            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(
                    BorderStroke(1.dp, color = Color(0xFFD6D6D6)),
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable(onClick = {
                    onEvent(CreateScreenEvent.OpenTimeEndPicker)

                })
                .padding(horizontal = 16.dp, vertical = 8.dp)){
                val endTimeText = String.format("%02d:%02d", event.value.end.hour, event.value.end.minute)
                Text(text = endTimeText, style = selectedTextStyle)
            }
        }

    }
    if (event.value.start.toLocalTime().isAfter(event.value.end.toLocalTime())){
        Text(textAlign = TextAlign.Center,modifier = Modifier.padding(horizontal = 24.dp),text = "Start of the class is after the end, please fix this in order to save the class.",style=androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Normal,color=Color(
            0xFFFF4848
        )
        ))
    }
}
@Composable
fun IsNeccesarySection(event: MutableState<Event>){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Is compulsory", style = createTextStyle, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.Switch(
                checked = event.value.compulsory,
                onCheckedChange = {   event.value = event.value.copy(compulsory = it) },
                modifier = Modifier.padding(start = 16.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = confirmColor,
                    checkedTrackColor =  dividerColor, uncheckedTrackColor =  dividerColor,
                    checkedIconColor = confirmColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedIconColor = Color.White,
                    uncheckedBorderColor = dividerColor,
                    checkedBorderColor =  dividerColor
                ), thumbContent = {
                    AnimatedVisibility(visible = event.value.compulsory) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(id = com.palrasp.myapplication.R.drawable.ic_check),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    }
}