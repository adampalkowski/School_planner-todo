package com.palrasp.myapplication.view

import android.app.usage.UsageEvents.Event
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

sealed class LessonsScreenEvents{
    class GoToEvent(val event: com.palrasp.myapplication.CalendarClasses.Event):LessonsScreenEvents()
}
@Composable
fun LessonsScreen(
    modifier: Modifier = Modifier,
    events: List<com.palrasp.myapplication.CalendarClasses.Event>,
    onBack: () -> Unit,    onEvent: (LessonsScreenEvents) -> Unit,
    deleteEvent: (com.palrasp.myapplication.CalendarClasses.Event) -> Unit
) {
    BackHandler() {
        onBack()
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column() {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            LazyColumn {
                items(DayOfWeek.values()) { dayOfWeek ->
                        // Display the day of the week
                        Text(
                            text = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, Locale.getDefault()).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x2AE2E2E2))
                                .padding(8.dp)
                                .padding(start = 24.dp),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            textAlign = TextAlign.Start
                        )
                    val eventsForDayOfWeek = events.filter { it.start.dayOfWeek == dayOfWeek }

                    eventsForDayOfWeek.forEach { event ->
                        EventItem(event, deleteEvent = deleteEvent,GoToEvent={
                            onEvent(LessonsScreenEvents.GoToEvent(it))
                        })
                    }

                    }

                }

            }
        }
}
@Composable
fun EventItem(event: com.palrasp.myapplication.CalendarClasses.Event,deleteEvent:(com.palrasp.myapplication.CalendarClasses.Event)->Unit,GoToEvent:(com.palrasp.myapplication.CalendarClasses.Event)->Unit) {
    val color = if(event.compulsory){
        event.color
    }else{
        PlannerTheme.colors.uiBackground
    }
    val textColor = if(event.compulsory){
        Color.White
    }else{
     textColor
    }
    val modifier = if(event.compulsory){
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(6.dp)
            .clip(RoundedCornerShape(8.dp)).clickable(onClick = {GoToEvent(event)})
            .background(color)
            .padding(vertical = 12.dp)
    }else{
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(6.dp)
            .clip(RoundedCornerShape(8.dp)).clickable(onClick = {GoToEvent(event)})
            .background(color)
            .border(        BorderStroke(1.dp, Color(0x2DAFAFAF)), shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp)
    }
    Box(
        modifier = modifier
    ) {
     
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = event.name,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = Lexend,
                    fontWeight = FontWeight.SemiBold
                ),
                color=textColor
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.start.toString().takeLast(5),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light
                    ),
                    color=textColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(painter = painterResource(id = R.drawable.ic_long_right), contentDescription =null, tint =textColor )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = event.end.toString().takeLast(5),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light
                    ),
                    color=textColor
                )
            }
        }

    }
}