package com.palrasp.myapplication.view.CreateScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog (eventState: MutableState<Event>,onDismissRequest:()->Unit,isStart:Boolean){
    val startState = rememberTimePickerState(is24Hour = true)

    val textLabel =if(isStart){
        "Start time"
    }else{
        "End time"
    }


    Dialog(onDismissRequest = onDismissRequest) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(PlannerTheme.colors.uiBackground)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = textLabel,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                )

            }
            Column(horizontalAlignment = Alignment.End) {

                Spacer(modifier = Modifier.height(16.dp))

                TimeInput(
                    state = startState,
                    modifier = Modifier.padding(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Cancel",
                        modifier = Modifier.clickable(onClick =onDismissRequest),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Confirm",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.clickable(onClick = {
                            val newHour = startState.hour
                            val newMinute = startState.minute
                            val newTime = LocalTime.of(newHour, newMinute)
                            val currentDateTime = LocalDateTime.of(eventState.value.start.toLocalDate(),newTime)

                            val newDateTime =currentDateTime

                            Log.d("TIMEPUNTN",newDateTime.toString()+" "+eventState.value.end.toString()+" "+eventState.value.start.toString())
                           if(isStart){
                                    eventState.value=eventState.value.copy(start = newDateTime)
                                    onDismissRequest()

                            }else{
                                   eventState.value=eventState.value.copy(end = newDateTime)
                                   onDismissRequest()

                            }
                        })
                    )
                }
            }

        }

        }

    }
}