package com.palrasp.myapplication
import kotlin.random.Random

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Switch
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
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.data.local.database.AppDatabase
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.TopBar
import com.palrasp.myapplication.view.CreateScreen
import com.palrasp.myapplication.view.DisplayEventScreen
import com.palrasp.myapplication.view.LessonsScreen
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.EventViewModelFactory
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    class Event(val event:com.palrasp.myapplication.CalendarClasses.Event):Screen("event")
    object Create : Screen("create")
    object Lessons : Screen("lessons")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val eventDao = database.eventDao()
        val eventViewModel = ViewModelProvider(this, EventViewModelFactory(eventDao)).get(EventViewModel::class.java)


        setContent {
            val coroutineScope = rememberCoroutineScope()

            PlannerTheme {
                var currentScreen:Screen by remember {
                    mutableStateOf(Screen.Calendar)
                }
                coroutineScope.launch {
                    eventViewModel.getEvents()
                }
                val classes by eventViewModel.allEvents.collectAsState(emptyList())

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PlannerTheme.colors.uiBackground
                ) {
                    var addClassDialog by remember {
                        mutableStateOf(false)
                    }
                    val currentDate = LocalDate.now()
                    var firstDayOfWeek by remember { mutableStateOf(currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) }
                    Box(modifier = Modifier.fillMaxSize()){

                        when (currentScreen) {
                            is Screen.Calendar -> {
                                Column() {
                                    TopBar(iconColor=Color(0xFF2A1A61), lessonsClicked = {
                                        currentScreen = Screen.Lessons
                                    },NextWeek={
                                        firstDayOfWeek=firstDayOfWeek.plusWeeks(1)
                                    }, PrevWeek = {
                                        firstDayOfWeek=firstDayOfWeek.minusWeeks(1)
                                    })
                                    Schedule(modifier=Modifier, events = classes, minDate =firstDayOfWeek, maxDate = firstDayOfWeek.plusDays(4),
                                    classesContent = { BasicClass(event = it, modifier = Modifier.clickable(onClick = {
                                        currentScreen=Screen.Event(it)
                                    }), important = true) })
                                }

                                Box(modifier = Modifier
                                    .padding(bottom = 24.dp, end = 24.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0EABFF))
                                    .clickable(onClick = {

                                        currentScreen = Screen.Create
                                    })
                                    .padding(16.dp)){
                                    Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription =null, tint = Color.White )
                                }

                            }
                            is Screen.Event->{
                                val event:com.palrasp.myapplication.CalendarClasses.Event=
                                    (currentScreen as Screen.Event).event
                                DisplayEventScreen(event, GoBack = {currentScreen=Screen.Calendar}, SaveNotes = {event->
                                    coroutineScope.launch {
                                        eventViewModel.updateEvent(event)
                                    }

                                })
                            }
                            is Screen.Create -> {

                                CreateScreen(modifier=Modifier, onBack = {
                                    currentScreen=Screen.Calendar
                                },  onAccept = { selectedDayOfWeek,startTime,endTime,name,color,className->
                                    coroutineScope.launch {
                                        val currentYearMonthDay =
                                            LocalDate.now() // Get the current date (year, month, day)

                                        // Calculate the end date, which is 6 months from the current date
                                        val endDate = currentYearMonthDay.plusMonths(6)

                                        // Loop through the date range from current date to end date
                                        var currentDate = currentYearMonthDay
                                        while (currentDate <= endDate) {
                                            val currentDayOfWeek = currentDate.dayOfWeek.value
                                            val daysUntilSelectedDay =
                                                (selectedDayOfWeek.value - currentDayOfWeek + 7) % 7
                                            val selectedDate = currentDate.plusDays(daysUntilSelectedDay.toLong())
                                            val endDateTime = selectedDate.atTime(endTime)
                                            val startDateTime = selectedDate.atTime(startTime)

                                            // Create a new event for this date
                                            val newEvent = com.palrasp.myapplication.CalendarClasses.Event(
                                                id = generateRandomId(),
                                                name = name,
                                                color = color,
                                                start = startDateTime,
                                                end = endDateTime,
                                                description = "",
                                                className = className,
                                                recurrenceJson = ""
                                            )

                                            // Set the recurrence pattern (e.g., WEEKLY)
                                            newEvent.setRecurrence(Recurrence(RecurrencePattern.WEEKLY))

                                            // Insert the event into the Room database
                                            eventViewModel.insertEvent(newEvent)

                                            // Move to the next date
                                            currentDate = currentDate.plusDays(1)
                                        }

                                        // After creating and inserting events, navigate back to the Calendar screen
                                        currentScreen = Screen.Calendar
                                    }
                                })
                            }
                            is Screen.Lessons -> {
                                LessonsScreen(modifier=Modifier,classes, onBack = {        currentScreen=Screen.Calendar}, deleteEvent = {
                                    event->
                                    coroutineScope.launch {
                                        eventViewModel.deleteAllEvents(event)
                                    }
                                })
                            }
                        }



                        if (addClassDialog){
                           CreateClassesDialog( modifier=Modifier.align(Alignment.Center),onClick={dayOfTheWeek,startTime,endTime->
                           })
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

fun generateRandomId(): Long {
    val random = Random.Default
    // Define the range for your random ID. Adjust the values as needed.
    val minIdValue = 1L
    val maxIdValue = Long.MAX_VALUE
    return random.nextLong(minIdValue, maxIdValue)
}
