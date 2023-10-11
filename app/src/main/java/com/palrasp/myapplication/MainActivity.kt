package com.palrasp.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.palrasp.myapplication.Calendar.ClassesGraph
import com.palrasp.myapplication.Calendar.SleepGraphData
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.data.local.database.AppDatabase
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.*
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.EventViewModelFactory
import kotlinx.coroutines.launch
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    class Event(val event: com.palrasp.myapplication.CalendarClasses.Event) : Screen("event")
    object Create : Screen("create")
    object Lessons : Screen("lessons")
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val eventDao = database.eventDao()
        val eventViewModel =
            ViewModelProvider(this, EventViewModelFactory(eventDao)).get(EventViewModel::class.java)


        setContent {
            PlannerTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PlannerTheme.colors.uiBackground
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        val coroutineScope = rememberCoroutineScope()
                        var addClassDialog by remember {
                            mutableStateOf(false)
                        }
                        var currentScreen: Screen by remember {
                            mutableStateOf(Screen.Calendar)
                        }
                        val classes by eventViewModel.allEvents.collectAsState(emptyList())
                        when (currentScreen) {
                            is Screen.Calendar -> {
                                CalendarScreen(onEvent={event->
                                    when(event){
                                        is CalendarEvents.GetEventsForWeek->{
                                            coroutineScope.launch {
                                                eventViewModel.getEventsForWeek(
                                                    event.firstDayOfWeek,
                                                    event.endOfWeek
                                                )
                                        }

                                        }
                                        is CalendarEvents.GoToEvent->{
                                            currentScreen = Screen.Event(event .event)

                                        }
                                        is CalendarEvents.UpdateEvent->{
                                            coroutineScope.launch {
                                                val eventIndex = event.eventIndex
                                                Log.d("CALENDARCLASS","UPDATE EVENT"+eventIndex)
                                                val updatedDescription = event.event.description
                                                    .split("\n") // Split the description into lines
                                                    .mapIndexed { index, line ->
                                                        if (index == eventIndex && line.startsWith("[-]")) {
                                                            // Replace "[-]" with "[x]" only at the specified index
                                                            line.replaceFirst("[-]", "[x]")
                                                        } else {
                                                            line
                                                        }
                                                    }
                                                    .joinToString("\n") // Join the modified lines back together
                                                val updatedEvent = event.event.copy(description = updatedDescription)
                                                Log.d("CALENDARCLASS","UPDATE EVENT"+event.event
                                                    .description+"   "+updatedEvent.description)
                                                eventViewModel.updateEvent(updatedEvent = updatedEvent)
                                            }


                                        }
                                        is CalendarEvents.GoToCreate->{
                                            currentScreen = Screen.Create

                                        }
                                        is CalendarEvents.GoToLesson->{
                                            currentScreen = Screen.Lessons

                                        }
                                    }
                                },classes=classes)


                            }
                            is Screen.Event -> {
                                val event: com.palrasp.myapplication.CalendarClasses.Event = (currentScreen as Screen.Event).event
                                DisplayEventScreen(
                                    event,
                                    GoBack = { currentScreen = Screen.Calendar },
                                    SaveNotes = { event ->
                                        coroutineScope.launch {
                                            eventViewModel.updateEvent(event)
                                        }
                                    })
                            }
                            is Screen.Create -> {
                                CreateScreen(
                                    modifier = Modifier,
                                    onBack = {
                                        currentScreen = Screen.Calendar
                                    },
                                    onAccept = { selectedDayOfWeek, startTime, endTime, name, color, className,compulsory ->
                                        coroutineScope.launch {
                                            // Calculate the start date (today) and end date (6 months from today)
                                            val startDate = LocalDate.now()
                                            val endDate = startDate.plusMonths(6)

// Calculate the selected day of the week
                                            val selectedDayOfWeekValue = selectedDayOfWeek.value

// Calculate the number of days between the selected day of the week and today
                                            val daysUntilSelectedDay = (selectedDayOfWeekValue - startDate.dayOfWeek.value + 7) % 7

// Calculate the number of days between today and the end date
                                            val totalDays = ChronoUnit.DAYS.between(startDate, endDate)

// Calculate the number of weeks between today and the end date
                                            val totalWeeks = totalDays / 7

// Create a list of events
                                            val events: List<com.palrasp.myapplication.CalendarClasses.Event> = (0 until totalWeeks).map { week ->
                                                val currentDate = startDate.plusDays(week * 7 + daysUntilSelectedDay.toLong())
                                                val startDateTime = currentDate.atTime(startTime)
                                                val endDateTime = currentDate.atTime(endTime)

                                                com.palrasp.myapplication.CalendarClasses.Event(
                                                    id = generateRandomId(),
                                                    name = name,
                                                    color = color,
                                                    start = startDateTime,
                                                    end = endDateTime,
                                                    description = "",
                                                    className = className,
                                                    recurrenceJson = "",
                                                    compulsory = compulsory
                                                )
                                            }
                                            events.forEach { event ->
                                                event.setRecurrence(Recurrence(RecurrencePattern.WEEKLY))
                                            }

                                            eventViewModel.insertEvents(events)

// After creating and inserting events, navigate back to the Calendar screen
                                            currentScreen = Screen.Calendar
                                        }
                                    })
                            }
                            is Screen.Lessons -> {
                                Log.d("LessonScreen",classes.size.toString())
                                LessonsScreen(
                                    modifier = Modifier,
                                    classes,
                                    onBack = { currentScreen = Screen.Calendar },
                                    deleteEvent = { event ->
                                        coroutineScope.launch {
                                            eventViewModel.deleteAllEvents(event)
                                        }
                                    })
                            }
                        }



                        if (addClassDialog) {
                            CreateClassesDialog(
                                modifier = Modifier.align(Alignment.Center),
                                onClick = { dayOfTheWeek, startTime, endTime ->
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
            val dayName =
                day.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
            val dayNumber = day.dayOfMonth
            days.add(Pair(dayName, dayNumber))
        }

        weekDays = days
    }
    val scrollState = rememberScrollState()
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
private fun HourLabel(hour: Int) {
    Text(
        text = hour.toString(),
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
        days.forEach { (dayName, dayNumber) ->
            Column() {
                Text(
                    text = "$dayName",
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(50.dp)
                        .padding(vertical = 4.dp),
                )
                Text(
                    text = "$dayNumber",
                    fontSize = 6.sp,
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
