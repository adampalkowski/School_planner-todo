package com.palrasp.myapplication.view.BottomSheet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.view.CalendarEvents
import com.palrasp.myapplication.view.CreateScreen.ButtonCreate
import com.palrasp.myapplication.view.CreateScreen.CreateDivider
import java.time.DayOfWeek

val daysOfWeek = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

sealed class BottomSheetEvents{
    object Close:BottomSheetEvents()
    object Expand:BottomSheetEvents()
}

@Composable
fun BottomSheetContent(
    classes: List<Event>, // Replace 'classes' with the actual data type
    onEvent: (CalendarEvents) -> Unit ,
    bottomSheetEvent:(BottomSheetEvents)->Unit,
    isExpanded:Boolean
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        val ratio = remember { mutableStateOf(0f) }
        Row(
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {

            Card(
                modifier = Modifier
                    .border(
                        BorderStroke(1.dp, Color(0xADE2E2E2)),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = Color.White)
                    .clickable(onClick = {
                        if (isExpanded) {
                            bottomSheetEvent(BottomSheetEvents.Close)
                        } else {
                            bottomSheetEvent(BottomSheetEvents.Expand)
                        }
                    })
                    .zIndex(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "SVG Image",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.FillBounds, // Adjust contentScale as needed
                )

            }

            ButtonCreate(onCreate = {
                onEvent(CalendarEvents.GoToCreate)
            })

        }
        Spacer(modifier = Modifier.height(24.dp))
        CreateDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
        ) {

                Text( modifier= Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                    text = stringResource(id = R.string.weekly) + " todo",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        fontFamily = Lexend, color = Color(0xFF353535)
                    ),
                    textAlign = TextAlign.Center
                )

            // Find events that match the current day of the week
            val checkedCount = classes.sumBy { event ->
                event.extractedLinesWithIndices.count { it.third == false }
            }
            val totalItemCount = classes.sumBy { event ->
                event.extractedLinesWithIndices.count { it.third == true }
            }
            LaunchedEffect(key1 = totalItemCount + checkedCount) {
                // Update the ratio based on the checked and total lines for the week
                val weekRatio = if (totalItemCount + checkedCount > 0) {
                    totalItemCount.toFloat() / (totalItemCount + checkedCount)
                } else {
                    0.0f
                }
                ratio.value = weekRatio
            }
            var previousEventName = remember { mutableStateOf<String?>(null) }

            LazyColumn {
                for (day in daysOfWeek) {
                    // Display the day of the week as a header
                    item {
                        DayItem(day)
                    }
                    val eventsForDay = classes.filter { it.dayOfTheWeek == day.value }
                    // Display the events for the current day
                    items(eventsForDay) { event ->
                        TodosItem(event = event,previousEventName=previousEventName,onEvent=onEvent)
                    }
                }
            }

        }

    }
}