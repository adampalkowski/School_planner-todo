package com.palrasp.myapplication.view

import android.app.usage.UsageEvents.Event
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import java.time.DayOfWeek

@Composable
fun LessonsScreen(
    modifier: Modifier = Modifier,
    events: List<com.palrasp.myapplication.CalendarClasses.Event>,
    onBack: () -> Unit,deleteEvent:(com.palrasp.myapplication.CalendarClasses.Event)->Unit
) {
    val eventsByDayOfWeek = events.groupBy { it.start.toLocalDate().dayOfWeek }

    Box(modifier = modifier.fillMaxSize()) {
        Column() {
            IconButton(
                onClick = onBack, modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.Black
                )
            }

            // Iterate through days of the week
            for (dayOfWeek in DayOfWeek.values()) {
                val eventsForDay = eventsByDayOfWeek[dayOfWeek] ?: emptyList()
                if (eventsForDay.isNotEmpty()) {
                    // Display the day of the week
                    Text(
                        text = dayOfWeek.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x5BE2E2E2))
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    // Display events for the day
                    eventsForDay.forEach { event ->
                        EventItem(event, deleteEvent = deleteEvent)
                    }
                }
            }
        }

    }

}

@Composable
fun EventItem(event: com.palrasp.myapplication.CalendarClasses.Event,deleteEvent:(com.palrasp.myapplication.CalendarClasses.Event)->Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(event.color)
            .padding(vertical = 12.dp)
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
                )
            )
            Row() {
                Text(
                    text = event.start.toString().takeLast(5),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light
                    )
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = event.end.toString().takeLast(5),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Lexend,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        }
        IconButton(onClick = {deleteEvent(event)}, modifier = Modifier.align(Alignment.CenterEnd) ) {
            Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription =null )
        }

    }
}