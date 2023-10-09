package com.palrasp.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.palrasp.myapplication.Calendar.ClassesGraph
import com.palrasp.myapplication.Calendar.Event
import com.palrasp.myapplication.Calendar.SleepGraphData
import com.palrasp.myapplication.CalendarClasses.CreateClassesDialog
import com.palrasp.myapplication.CalendarClasses.Schedule
import com.palrasp.myapplication.CalendarClasses.fakeClasses
import com.palrasp.myapplication.data.local.database.AppDatabase
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.EventViewModelFactory
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val eventDao = database.eventDao()
        val eventViewModel = ViewModelProvider(this, EventViewModelFactory(eventDao)).get( EventViewModel::class.java)
        setContent {
            PlannerTheme {
                val coroutineScope = rememberCoroutineScope()
                val classes by eventViewModel.allEvents.collectAsState(emptyList())

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PlannerTheme.colors.uiBackground
                ) {

                    var addClassDialog by remember {
                        mutableStateOf(false)
                    }

                    Box(modifier = Modifier.fillMaxSize()){
                        val currentDate = LocalDate.now()
                        val firstDayOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        eventViewModel.allEvents.value
                        Schedule(modifier=Modifier, events = classes, minDate =firstDayOfWeek )


                        if (addClassDialog){
                           CreateClassesDialog( modifier=Modifier.align(Alignment.Center),onClick={dayOfTheWeek,startTime,endTime->
                               coroutineScope.launch {


                               val currentYearMonthDay = LocalDate.now() // Get the current date (year, month, day)
                               val currentDayOfWeek = currentYearMonthDay.dayOfWeek.value // Get the current day of the week (1 for Monday, 7 for Sunday)

                               val daysUntilSelectedDay = (dayOfTheWeek.value - currentDayOfWeek + 7) % 7
                               val selectedDate = currentYearMonthDay.plusDays(daysUntilSelectedDay.toLong())
                               val endDateTime = selectedDate.atTime(endTime)
                               val startDateTime = selectedDate.atTime(startTime)


                             // here we ahve th event
                             val newEvent= com.palrasp.myapplication.CalendarClasses.Event(

                                   name = "sample",
                                   color = Color(0xFFF4BFDB),
                                   start = startDateTime,
                                   end = endDateTime,
                                   description = "",
                               )
                               eventViewModel.insertEvent(newEvent)
                               }

                           })
                        }
                        Box(modifier = Modifier
                            .padding(bottom = 24.dp, end = 24.dp)
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .clickable(onClick = {

                                //Add class

                                addClassDialog = true
                            })
                            .padding(16.dp)){
                            Icon(painter = painterResource(id = R.drawable.double_arrow_left), contentDescription =null, tint = Color.White )
                        }

                    }

                }
            }
        }
    }
}

@Composable
private fun ClassesGraphMain(sleepGraphData: SleepGraphData) {
    // Create a state to hold the day names and numbers
    var weekDays by remember { mutableStateOf(listOf<Pair<String, Int>>()) }

    // Use LaunchedEffect to update the day names and numbers
    LaunchedEffect(key1 = weekDays) {
        val today = LocalDate.now()
        val currentDayOfWeek = today.dayOfWeek // The current day of the week

        val days = mutableListOf<Pair<String, Int>>()

        // Calculate and add the day name and number for each day of the week
        for (i in 1..7) {
            val day = today.plusDays(i.toLong() - currentDayOfWeek.value)
            val dayName = day.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
            val dayNumber = day.dayOfMonth
            days.add(Pair(dayName, dayNumber))
        }

        weekDays = days
    }
    val scrollState= rememberScrollState()
    val hours = (sleepGraphData.earliestStartHour..23) + (0..sleepGraphData.latestEndHour)

    ClassesGraph(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .wrapContentSize(),
        hoursItemsCount = sleepGraphData.sleepDayData.size,
        daysHeader = {
            DaysHeader(weekDays)
        },
        hourLabel = { index ->

            val data = sleepGraphData.sleepDayData[index]
            HourLabel(index)
        },
        bar = { index ->
            val data = sleepGraphData.sleepDayData[index]
            // We have access to Modifier.timeGraphBar() as we are now in TimeGraphScope
            ClassesBar(
                sleepData = data,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .classesGraphBar(
                        start = data.firstSleepStart,
                        end = data.lastSleepEnd,
                        hours = hours,
                    )
            )
        }
    )
}


@Composable
private fun HourLabel(hour:Int) {
    Text(
        text=hour.toString(),
        Modifier.run {
            height(24.dp)
                .padding(start = 8.dp, end = 24.dp)
        },
        textAlign = TextAlign.Center
    )
}

@Composable
private fun DaysHeader(days: List<Pair<String, Int>>) {
    Row(
        Modifier
            .padding(bottom = 16.dp)
            .drawBehind {
                val brush = Brush.linearGradient(listOf(Yellow, Yellow))
                drawRoundRect(
                    brush,
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                )
            }
    ) {
        days.forEach {(dayName,dayNumber)->
            Column() {
                Text(
                    text = "$dayName",
                    fontSize = 8.sp ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = "$dayNumber",
                    fontSize = 6.sp ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(vertical = 4.dp),
                )
            }

        }
    }
}