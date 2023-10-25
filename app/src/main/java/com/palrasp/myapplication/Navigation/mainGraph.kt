package com.palrasp.myapplication.Navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.CalendarClasses.RecurrencePattern
import com.palrasp.myapplication.CalendarClasses.getRecurrence
import com.palrasp.myapplication.Screen
import com.palrasp.myapplication.utils.generateRandomId
import com.palrasp.myapplication.utils.sampleEvent
import com.palrasp.myapplication.view.*
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedHour
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executor

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.mainGraph(
    navController: NavController,
    eventViewModel: EventViewModel,
    settingsViewModel: SettingsViewModel,
    coroutineScope: CoroutineScope,
    context: Context,

    ) {

    navigation(startDestination = "Calendar", route = "Main") {
        composable(
            "Calendar",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            CalendarScreen(
                startHour = settingsViewModel.startHour.value,
                firstDay = settingsViewModel.firstDayOfWeek,
                selectedMonth = settingsViewModel.selectedMonth,
                calendarOption = settingsViewModel.calendarOption.value,
                onEvent = { event ->
                    when (event) {
                        is CalendarEvents.GoToSettings -> {
                            navController.navigate("Settings")
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
                            navController.navigate("Event")

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
                            navController.navigate("Create")
                        }
                        is CalendarEvents.GoToLesson -> {
                            navController.navigate("Lessons")
                        }

                    }
                },
                classes = eventViewModel.allEvents.value
            )


        }

        composable(
            "Settings",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            SettingsScreen(
                onEvent = {
                    when (it) {
                        is SettingsScreenEvents.GoBack -> {
                            navController.navigate("Calendar")
                        }
                        is SettingsScreenEvents.ChangeCalendarOption -> {
                            saveSelectedCalendarOption(context = context, it.option)
                            settingsViewModel.updateCalendarOption(it.option)
                        }
                        is SettingsScreenEvents.ChangeStartHour -> {
                            saveSelectedHour(context = context, it.hour)
                            settingsViewModel.updateStartHour(it.hour)
                        }
                        else -> {}
                    }
                },
                modifier = Modifier,
                calendarOption = settingsViewModel.calendarOption.value,
                startHour = settingsViewModel.startHour
            )

        }

        composable(
            "Event",
            enterTransition = {
                when (targetState.destination.route) {
                    "Event" ->
                        expandIn(   animationSpec = tween(700))

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Event" ->
                        expandIn(   animationSpec = tween(700))
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            var currentEvent: Event by remember {
                mutableStateOf(eventViewModel.currentClass.value)
            }

            DisplayEventScreen(
                currentEvent,
                GoBack = { navController.navigate("Calendar") },
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
                            navController.navigate("Update")
                        }
                    }
                })

        }

        composable(
            "Create",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            CreateScreen(
                onBack = {
                    navController.navigate("Calendar")
                },
                eventState = eventViewModel.currentClass,
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
                        val recurrance = eventViewModel.currentClass.value.getRecurrence()!!

                        val events: List<com.palrasp.myapplication.CalendarClasses.Event> =
                            (0 until totalWeeks).map { week ->
                                when (recurrance.pattern) {
                                    RecurrencePattern.WEEKLY -> {
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
                                    RecurrencePattern.TWOWEEKS -> {
                                        val currentDate =
                                            startDate.plusDays(week * 14 + daysUntilSelectedDay.toLong())

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
                                    RecurrencePattern.MONTHLY -> {
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
                                    RecurrencePattern.DAILY -> {
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
                        navController.navigate("Calendar")

                        eventViewModel.resetCurrentClass()
                        eventViewModel.currentClass.value = sampleEvent

                    }

                }, isUpdate = false
            )
        }

        composable(
            "Update",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            val eventState = remember {
                mutableStateOf(eventViewModel.currentClass.value)
            }

            val oldEvent = remember {
                mutableStateOf(eventState.value)
            }

            CreateScreen(
                onBack = {
                    eventState.value = sampleEvent
                    navController.navigate("Calendar")
                },
                eventState = eventState,
                onCreateEvent = { createdEvent ->
                    coroutineScope.launch {
                        eventViewModel.updateEvents(
                            oldEvent.value,
                            createdEvent
                        )

                        navController.navigate("Calendar")
                        eventViewModel.resetCurrentClass()
                        eventState.value = sampleEvent
                    }

                }, isUpdate = true
            )
        }

        composable(
            "Lessons",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(700)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            var currentScreen: Screen by remember {
                mutableStateOf(eventViewModel.currentScreen.value)
            }
            LessonsScreen(
                modifier = Modifier,
                eventViewModel.allEvents.value,
                onBack = { currentScreen = Screen.Calendar },
                deleteEvent = { event ->
                    coroutineScope.launch {
                        eventViewModel.deleteAllEvents(event)
                    }
                }, onEvent = {
                    when (it) {
                        is LessonsScreenEvents.GoToEvent -> {
                            currentScreen = Screen.Event(it.event)
                        }
                    }
                })
        }
    }
}