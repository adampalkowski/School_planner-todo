package com.palrasp.myapplication

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.ads.mediationtestsuite.viewmodels.ViewModelFactory
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.data.local.database.AppDatabase
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.generateRandomId
import com.palrasp.myapplication.utils.sampleEvent
import com.palrasp.myapplication.view.*
import com.palrasp.myapplication.view.SettingsScreen.getSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.getSelectedHour
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedHour
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.EventViewModelFactory
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModelFactory
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.random.Random




class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Access room database
        val database = AppDatabase.getInstance(applicationContext)
        val eventDao = database.eventDao()
        //only Instance of eventViewModel
        val eventViewModel =
            ViewModelProvider(this, EventViewModelFactory(eventDao)).get(EventViewModel::class.java)

        setContent {
            PlannerTheme(darkTheme = false) {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(application = LocalContext.current.applicationContext as Application)
                )
                NavigationComponent(
                    eventViewModel=eventViewModel,
                    settingsViewModel=settingsViewModel
                )


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PlannerTheme.colors.uiBackground
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val coroutineScope = rememberCoroutineScope()
                        val eventState = remember {
                            mutableStateOf(eventViewModel.currentClass.value)
                        }

                        // Convert the saved route back to the Screen instance when needed
                        var currentScreen: Screen by remember {
                            mutableStateOf(eventViewModel.currentScreen.value)
                        }
                        val context= LocalContext.current

                        val currentDate = LocalDate.now()
                        var calendarOption= remember {
                            mutableStateOf(CalendarOption.HALF_WEEK)
                        }

                        var option=getSelectedCalendarOption(context)
                        var hour= getSelectedHour(context)
                        if (option!=null && option.label.isNotEmpty()){
                            calendarOption.value=option
                        }
                        var firstDayOfWeek = rememberSaveable {
                            mutableStateOf(
                                currentDate.with(
                                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                                )
                            )
                        }
                        var startHour = rememberSaveable {
                            mutableStateOf(hour)
                        }
                        if(calendarOption.value==CalendarOption.WEEKEND){
                            firstDayOfWeek.value=
                                currentDate.with(
                                    TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY)
                                )

                        }else{
                            firstDayOfWeek.value=
                                currentDate.with(
                                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                                )
                        }


                        var selectedMonth = remember {
                            mutableStateOf(
                                firstDayOfWeek.value.month.getDisplayName(
                                    java.time.format.TextStyle.FULL_STANDALONE,
                                    Locale.getDefault()
                                )
                            )
                        }
                        val classes by eventViewModel.allEvents.collectAsState(emptyList())

                        DisposableEffect(currentScreen) {
                            onDispose {
                                eventViewModel.currentScreen.value = currentScreen
                                eventViewModel.currentClass.value = eventState.value
                            }
                        }

                        Crossfade(
                            targetState = currentScreen,
                            animationSpec = tween(durationMillis = 300)
                        ) { screen ->
                            when (screen) {
                                is Screen.Settings->{

                                    SettingsScreen(onEvent = {
                                        when(it){
                                            is SettingsScreenEvents.GoBack->{
                                                currentScreen=Screen.Calendar
                                            }
                                            is SettingsScreenEvents.ChangeCalendarOption->{
                                                saveSelectedCalendarOption(context = context,it.option)
                                                calendarOption.value=it.option
                                            }
                                            is SettingsScreenEvents.ChangeStartHour->{
                                                saveSelectedHour(context = context,it.hour)
                                                startHour.value=it.hour
                                            }
                                            else->{}
                                        }
                                    }, modifier = Modifier,calendarOption=calendarOption.value,startHour=startHour)

                                }
                                is Screen.Calendar -> {
                                    CalendarScreen(startHour=startHour.value,
                                        firstDay = firstDayOfWeek,
                                        selectedMonth = selectedMonth,
                                        calendarOption=calendarOption.value,
                                        onEvent = { event ->
                                            when (event) {
                                                is CalendarEvents.GoToSettings->{

                                                    currentScreen = Screen.Settings

                                                }
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
                                                        val updatedDescription =
                                                            event.event.description
                                                                .split("\n") // Split the description into lines
                                                                .mapIndexed { index, line ->
                                                                    if (index == eventIndex && line.startsWith(
                                                                            "[-]"
                                                                        )
                                                                    ) {
                                                                        // Replace "[-]" with "[x]" only at the specified index
                                                                        line.replaceFirst(
                                                                            "[-]",
                                                                            "[x]"
                                                                        )
                                                                    } else {
                                                                        line
                                                                    }
                                                                }
                                                                .joinToString("\n") // Join the modified lines back together
                                                        val updatedEvent =
                                                            event.event.copy(description = updatedDescription)
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
                                        },
                                        classes = classes
                                    )


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
                                            when (it) {
                                                is DisplayEventScreenEvents.GoToEditClass -> {
                                                    eventViewModel.currentClass.value = it.event
                                                    eventState.value = it.event
                                                    currentScreen = Screen.Update
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
                                                val recurrance=eventState.value.getRecurrence()!!

                                                val events: List<com.palrasp.myapplication.CalendarClasses.Event> =
                                                    (0 until totalWeeks).map { week ->
                                                        when(recurrance.pattern){
                                                            RecurrencePattern.WEEKLY->{
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
                                                            RecurrencePattern.TWOWEEKS->{
                                                                val currentDate = startDate.plusDays(week * 14 + daysUntilSelectedDay.toLong())

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
                                                            RecurrencePattern.MONTHLY->{
                                                                val currentDate = startDate.plusMonths(week)

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
                                                            RecurrencePattern.DAILY->{
                                                                val currentDate = startDate.plusDays(week)
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
                                                            else -> {
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
                                                        }

                                                    }

                                                eventViewModel.insertEvents(events)
                                                currentScreen = Screen.Calendar
                                                eventViewModel.resetCurrentClass()
                                                eventState.value = sampleEvent

                                            }

                                        }, isUpdate = false
                                    )


                                }
                                is Screen.Update -> {

                                    val oldEvent = remember {
                                        mutableStateOf(eventState.value)
                                    }

                                    CreateScreen(
                                        onBack = {
                                            eventState.value = sampleEvent
                                            currentScreen = Screen.Calendar
                                        },
                                        eventState = eventState,
                                        onCreateEvent = { createdEvent ->
                                            coroutineScope.launch {
                                                eventViewModel.updateEvents(
                                                    oldEvent.value,
                                                    createdEvent
                                                )

                                                currentScreen = Screen.Calendar
                                                eventViewModel.resetCurrentClass()
                                                eventState.value = sampleEvent
                                            }

                                        }, isUpdate = true
                                    )


                                }
                                is Screen.Lessons -> {
                                    LessonsScreen(
                                        modifier = Modifier,
                                        classes,
                                        onBack = { currentScreen = Screen.Calendar },
                                        deleteEvent = { event ->
                                            coroutineScope.launch {
                                                eventViewModel.deleteAllEvents(event)
                                            }
                                        }, onEvent = {
                                            when(it){
                                                is LessonsScreenEvents.GoToEvent->{

                                                    currentScreen = Screen.Event(it.event)
                                                }
                                            }
                                        })
                                }

                            }

                        }

                    }

                }
            }
        }
    }
}

