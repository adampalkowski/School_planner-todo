package com.palrasp.myapplication.Navigation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.palrasp.myapplication.CalendarClasses.getRecurrence
import com.palrasp.myapplication.Screen
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.updateEventDescription
import com.palrasp.myapplication.utils.generateEventsForPattern
import com.palrasp.myapplication.utils.sampleEvent
import com.palrasp.myapplication.view.*
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedHour
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
val durationMillis = 500

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
                when (initialState.destination.route) {
                    "Lessons" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    "Create" ->
                        fadeIn(   animationSpec = tween(durationMillis))

                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    "Lessons" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    "Settings" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    "Lessons" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    "Lessons" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    "Settings" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            val allEvents by eventViewModel.allEvents.collectAsState()

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
                            eventViewModel.setCurrentClass(event.event)
                            navController.navigate("Event")
                        }
                        is CalendarEvents.UpdateEvent -> {
                            coroutineScope.launch {
                                updateEventDescription(event, eventViewModel)
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
                classes = allEvents
            )


        }

        composable(
            "Settings",
            enterTransition = {
                when (targetState.destination.route) {

                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {

                    "Settings" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
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
                modifier = Modifier.fillMaxSize().background(color=PlannerTheme.colors.uiBackground),
                calendarOption = settingsViewModel.calendarOption.value,
                startHour = settingsViewModel.startHour
            )

        }

        composable(
            "Event",
            enterTransition = {
                when (targetState.destination.route) {
                    "Event" ->
                        expandIn(animationSpec = tween(durationMillis))

                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {


                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Event" ->
                        expandIn(animationSpec = tween(durationMillis))
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {


                    else -> null
                }
            }
        ) { backStackEntry ->

            DisplayEventScreen(
                event=eventViewModel.currentClass.value,
                onEvent = {
                    when (it) {
                        is DisplayEventScreenEvents.GoToEditClass -> {
                            eventViewModel.currentClass.value = it.event
                            navController.navigate("Update")
                        }
                        is DisplayEventScreenEvents.DeleteEvents -> {
                            coroutineScope.launch {
                                eventViewModel.deleteAllEvents(it.event)
                            }
                        }
                        is DisplayEventScreenEvents.GoBack -> {
                            navController.navigate("Calendar")
                        }
                        is DisplayEventScreenEvents.SaveNotes -> {
                            coroutineScope.launch {
                                eventViewModel.updateEvent(it.event)

                            }
                        }
                    }
                },modifier=Modifier.fillMaxSize().background(color=PlannerTheme.colors.uiBackground))

        }

        composable(
            "Create",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {

                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {

                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {

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
                        val daysUntilSelectedDay =
                            (createdEvent.dayOfTheWeek - startDate.dayOfWeek.value + 7) % 7
                        val recurrance = eventViewModel.currentClass.value.getRecurrence()!!

                        val events = generateEventsForPattern(
                            recurrance.pattern,
                            startDate,
                            daysUntilSelectedDay,
                            createdEvent
                        )

                        eventViewModel.insertEvents(events)
                        navController.navigate("Calendar")

                        eventViewModel.resetCurrentClass()
                        eventViewModel.currentClass.value = sampleEvent
                    }

                }, isUpdate = false,modifier=Modifier.fillMaxSize().background(PlannerTheme.colors.uiBackground)
            )
        }

        composable(
            "Update",
            enterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            val oldEvent = remember {
                mutableStateOf(eventViewModel.currentClass.value)
            }

            CreateScreen(
                onBack = {
                    coroutineScope.launch {
                        eventViewModel.resetCurrentClass()
                    }
                    navController.navigate("Calendar")
                },
                eventState = eventViewModel.currentClass,
                onCreateEvent = { createdEvent ->
                    coroutineScope.launch {
                        eventViewModel.updateEvents(
                            oldEvent.value,
                            createdEvent
                        )
                        navController.navigate("Calendar")
                        eventViewModel.resetCurrentClass()
                    }
                }, isUpdate = true,modifier=Modifier.fillMaxSize().background(PlannerTheme.colors.uiBackground)
            )
        }

        composable(
            "Lessons",
            enterTransition = {
                when (targetState.destination.route) {
                    "Lessons" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    "Lessons" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popEnterTransition = {
                when (targetState.destination.route) {
                    "Lessons" ->
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            },
            popExitTransition = {
                when (targetState.destination.route) {
                    "Lessons" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    "Calendar" ->
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(durationMillis)
                        )
                    else -> null
                }
            }
        ) { backStackEntry ->
            var currentScreen: Screen by remember {
                mutableStateOf(eventViewModel.currentScreen.value)
            }
            LessonsScreen(
                modifier=Modifier.fillMaxSize().background(PlannerTheme.colors.uiBackground),
                eventViewModel.allEvents.value,
                onBack = { navController.navigate("Calendar") },
                deleteEvent = { event ->
                    coroutineScope.launch {
                        eventViewModel.deleteAllEvents(event)
                    }
                }, onEvent = {
                    when (it) {
                        is LessonsScreenEvents.GoToEvent -> {
                            eventViewModel.setCurrentClass(it.event)
                            navController.navigate("Event")
                        }
                    }
                })
        }
    }
}