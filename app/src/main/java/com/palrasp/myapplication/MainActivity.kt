package com.palrasp.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.random.Random

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    class Event(val event: com.palrasp.myapplication.CalendarClasses.Event) : Screen("event")
    object Create : Screen("create")
    object Update : Screen("create")
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
            PlannerTheme (darkTheme = false){

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
                        val eventState = remember {
                            mutableStateOf(eventViewModel.currentClass.value)
                        }

                        // Convert the saved route back to the Screen instance when needed
                        var currentScreen: Screen by remember {
                            mutableStateOf(eventViewModel.currentScreen.value)
                        }
                        val currentDate = LocalDate.now()

                        var firstDayOfWeek = rememberSaveable {
                            mutableStateOf(
                                currentDate.with(
                                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                                )
                            )
                        }
                        var selectedMonth = remember { mutableStateOf(firstDayOfWeek.value.month.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, Locale.getDefault())) }

                        DisposableEffect(currentScreen) {
                            onDispose {
                                eventViewModel.currentScreen.value = currentScreen
                                eventViewModel.currentClass.value = eventState.value
                            }
                        }
                        val classes by eventViewModel.allEvents.collectAsState(emptyList())
                        Crossfade(
                            targetState = currentScreen,
                            animationSpec = tween(durationMillis = 300)
                        ) { screen ->
                            when (screen) {
                                is Screen.Calendar -> {
                                    CalendarScreen(firstDay=firstDayOfWeek,selectedMonth=selectedMonth,onEvent = { event ->
                                        when (event) {
                                            is CalendarEvents.GetEventsForWeek -> {
                                                coroutineScope.launch {
                                                    eventViewModel.getEventsForWeek(
                                                        event.firstDayOfWeek,
                                                        event.endOfWeek
                                                    )

                                                }

                                            }
                                            is CalendarEvents.GoToEvent -> {

                                                currentScreen = Screen.Event(event.event)

                                            }
                                            is CalendarEvents.UpdateEvent -> {
                                                coroutineScope.launch {
                                                    val eventIndex = event.eventIndex
                                                    Log.d(
                                                        "CALENDARCLASS",
                                                        "UPDATE EVENT" + eventIndex
                                                    )
                                                    val updatedDescription = event.event.description
                                                        .split("\n") // Split the description into lines
                                                        .mapIndexed { index, line ->
                                                            if (index == eventIndex && line.startsWith(
                                                                    "[-]"
                                                                )
                                                            ) {
                                                                // Replace "[-]" with "[x]" only at the specified index
                                                                line.replaceFirst("[-]", "[x]")
                                                            } else {
                                                                line
                                                            }
                                                        }
                                                        .joinToString("\n") // Join the modified lines back together
                                                    val updatedEvent =
                                                        event.event.copy(description = updatedDescription)
                                                    Log.d(
                                                        "CALENDARCLASS",
                                                        "UPDATE EVENT" + event.event
                                                            .description + "   " + updatedEvent.description
                                                    )
                                                    eventViewModel.updateEvent(updatedEvent = updatedEvent)
                                                }


                                            }
                                            is CalendarEvents.GoToCreate -> {
                                                currentScreen = Screen.Create

                                            }
                                            is CalendarEvents.GoToLesson -> {
                                                currentScreen = Screen.Lessons

                                            }

                                        }
                                    }, classes = classes)


                                }

                                is Screen.Event -> {
                                    val event: com.palrasp.myapplication.CalendarClasses.Event =
                                        screen.event


                                    DisplayEventScreen(
                                        event,
                                        GoBack = { currentScreen = Screen.Calendar },
                                        SaveNotes = { event ->
                                            coroutineScope.launch {
                                                eventViewModel.updateEvent(event)
                                            }
                                        }, onDeleteEvent = { event ->
                                            coroutineScope.launch {

                                                eventViewModel.deleteAllEvents(event)

                                            }

                                        }, onEvent = {
                                            when(it){
                                                is DisplayEventScreenEvents.GoToEditClass->{
                                                    eventViewModel.currentClass.value=it.event
                                                    eventState.value=it.event
                                                    currentScreen=Screen.Update
                                                }
                                            }
                                        })
                                }
                                is Screen.Create -> {

                                    CreateScreen(
                                        onBack = { currentScreen = Screen.Calendar },
                                        eventState = eventState,
                                        onCreateEvent = { createdEvent ->
                                            coroutineScope.launch {
                                                val startDate = LocalDate.now()
                                                val endDate = startDate.plusMonths(6)

                                                val totalDays =
                                                    ChronoUnit.DAYS.between(startDate, endDate)
                                                val totalWeeks = totalDays / 7
                                                val selectedDayOfWeekValue =
                                                    createdEvent.dayOfTheWeek

                                                val daysUntilSelectedDay =
                                                    (selectedDayOfWeekValue - startDate.dayOfWeek.value + 7) % 7

                                                val events: List<com.palrasp.myapplication.CalendarClasses.Event> =
                                                    (0 until totalWeeks).map { week ->
                                                        val currentDate =
                                                            startDate.plusDays(week * 7 + daysUntilSelectedDay.toLong())
                                                        val startTime =
                                                            createdEvent.start.toLocalTime()
                                                        val endTime = createdEvent.end.toLocalTime()
                                                        val startDateTime =
                                                            currentDate.atTime(startTime)
                                                        val endDateTime =
                                                            currentDate.atTime(endTime)
                                                        com.palrasp.myapplication.CalendarClasses.Event(
                                                            id = generateRandomId(),
                                                            name = createdEvent.name,
                                                            color = createdEvent.color,
                                                            start = startDateTime,
                                                            end = endDateTime,
                                                            description = "\n\n\n\n\n\n\n\n",
                                                            className = createdEvent.className,
                                                            recurrenceJson = "",
                                                            compulsory = createdEvent.compulsory,
                                                            dayOfTheWeek = createdEvent.dayOfTheWeek
                                                        )
                                                    }
                                                events.forEach { event ->
                                                    event.setRecurrence(Recurrence(RecurrencePattern.WEEKLY))
                                                }
                                                eventViewModel.insertEvents(events)
                                                currentScreen = Screen.Calendar
                                                eventViewModel.resetCurrentClass()
                                                eventState.value = Event(
                                                    id = generateRandomId(),
                                                    name = "",
                                                    color = Color(0xFF7DC1FF),
                                                    start = LocalDateTime.of(
                                                        LocalDate.now(),
                                                        LocalTime.of(12, 0)
                                                    ),
                                                    end = LocalDateTime.of(
                                                        LocalDate.now(),
                                                        LocalTime.of(13, 30)
                                                    ),
                                                    description = "",
                                                    className = "",
                                                    recurrenceJson = "",
                                                    compulsory = true,
                                                    dayOfTheWeek = 1
                                                )

                                            }

                                        }, isUpdate = false)


                                }
                                is Screen.Update -> {
                                    Log
                                        .d("UPDATEEVENTS","ipad22222e")

                                    val oldEvent = remember {
                                        mutableStateOf(eventState.value)
                                    }

                                    CreateScreen(
                                    onBack = { currentScreen = Screen.Calendar },
                                    eventState = eventState,
                                    onCreateEvent = { createdEvent ->
                                        coroutineScope.launch {
                                            Log

                                                .d("UPDATEEVENTS","ipade")
                                            Log
                                                .d("UPDATEEVENTS",oldEvent.value.toString())
                                            Log
                                                .d("UPDATEEVENTS",createdEvent.toString())
                                            eventViewModel.updateEvents(oldEvent.value,createdEvent)

                                            currentScreen = Screen.Calendar
                                            eventViewModel.resetCurrentClass()
                                            eventState.value = Event(
                                                id = generateRandomId(),
                                                name = "",
                                                color = Color(0xFF7DC1FF),
                                                start = LocalDateTime.of(
                                                    LocalDate.now(),
                                                    LocalTime.of(12, 0)
                                                ),
                                                end = LocalDateTime.of(
                                                    LocalDate.now(),
                                                    LocalTime.of(13, 30)
                                                ),
                                                description = "",
                                                className = "",
                                                recurrenceJson = "",
                                                compulsory = true,
                                                dayOfTheWeek = 1
                                            )

                                        }

                                    },isUpdate=true)


                            }
                                is Screen.Lessons -> {
                                    Log.d("LessonScreen", classes.size.toString())
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
                                else -> {}

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
