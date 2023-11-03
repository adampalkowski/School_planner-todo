package com.palrasp.myapplication.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.CalendarClasses.BasicClass
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.CalendarClasses.Schedule
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.TopBar
import com.palrasp.myapplication.view.BottomSheet.BottomSheetContent
import com.palrasp.myapplication.view.BottomSheet.BottomSheetEvents
import com.palrasp.myapplication.view.CreateScreen.CreateDivider
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

sealed class CalendarEvents {
    class GetEventsForWeek(val firstDayOfWeek: String, val endOfWeek: String) : CalendarEvents()
    object GoToLesson : CalendarEvents()
    object GoToSettings : CalendarEvents()
    object GoToCreate : CalendarEvents()
    class GoToEvent(val event: Event) : CalendarEvents()
    class UpdateEvent(val event: Event, val eventIndex: Int) : CalendarEvents()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarScreen(
    firstDay: MutableState<LocalDate>,
    selectedMonth: MutableState<String>,
    calendarOption: CalendarOption,
    onEvent: (CalendarEvents) -> Unit,
    classes: List<Event>,
    startHour: Float,
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed, animationSpec = tween(
                durationMillis = 300, // Adjust the duration as needed
                easing = LinearOutSlowInEasing
            )
        )
    )
    var daysLength = remember { mutableStateOf(6) }

    when (calendarOption) {
        CalendarOption.HALF_WEEK -> {
            daysLength.value = 4
        }
        CalendarOption.FULL_WEEK -> {
            daysLength.value = 6

        }
        CalendarOption.WEEKEND -> {
            daysLength.value = 2
        }
    }
    var endOfWeek = firstDay.value.plusDays(daysLength.value.toLong())

    LaunchedEffect(daysLength.value) {
        endOfWeek = firstDay.value.plusDays(daysLength.value.toLong())
    }


    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topEnd = 48.dp),
        drawerBackgroundColor = PlannerTheme.colors.uiBackground,
        drawerScrimColor = Color.Black.copy(alpha = 0.2f),
        drawerGesturesEnabled = true,
        sheetContent = {
            BottomSheetContent(
                classes = classes,
                onEvent = onEvent,
                bottomSheetEvent = { event ->
                    when (event) {
                        is BottomSheetEvents.Close -> {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }
                        }
                        is BottomSheetEvents.Expand -> {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                },
                isExpanded = bottomSheetScaffoldState.bottomSheetState.isExpanded
            )
        },
        sheetPeekHeight = 80.dp,
        sheetElevation = 0.dp,
        sheetContentColor =  PlannerTheme.colors.uiBackground,
        sheetBackgroundColor = Color.Transparent,
        sheetGesturesEnabled = true,
        modifier = Modifier.fillMaxSize(),
        contentColor = Color.Transparent,
        scaffoldState = bottomSheetScaffoldState
    ) { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PlannerTheme.colors.uiBackground)
        ) {
            LaunchedEffect(firstDay.value) {
                onEvent(
                    CalendarEvents.GetEventsForWeek(
                        firstDay.value.toString(),
                        endOfWeek.plusDays(1).toString()

                    )
                )
            }
            Column {
                TopBar(ChangeDiv = {
                    onEvent(CalendarEvents.GoToSettings)
                }, OpenDrawer = {
                    coroutineScope.launch {
                        onEvent(CalendarEvents.GoToLesson)
                    }
                },
                    iconColor = PlannerTheme.colors.textSecondary,
                    lessonsClicked = {
                        coroutineScope.launch() {
                            bottomSheetScaffoldState.drawerState.open()
                        }
                    },
                    NextWeek = {
                        firstDay.value = firstDay.value.plusWeeks(1)
                        selectedMonth.value =
                            firstDay.value.month.getDisplayName(
                                java.time.format.TextStyle.FULL_STANDALONE,
                                Locale.getDefault()
                            )  // Update selectedMonth when firstDayOfWeek changes


                    },
                    PrevWeek = {
                        firstDay.value = firstDay.value.minusWeeks(1)
                        selectedMonth.value =
                            firstDay.value.month.getDisplayName(
                                java.time.format.TextStyle.FULL_STANDALONE,
                                Locale.getDefault()
                            )
                    },
                    openMonthPicker = { },
                    selectedMonth = selectedMonth.value
                )
                CreateDivider()
                Schedule(modifier = Modifier.fillMaxSize(),
                    events = classes,
                    minDate = firstDay.value,
                    maxDate = firstDay.value.plusDays(daysLength.value.toLong()),
                    classesContent = {
                        BasicClass(
                            event = it,
                            modifier = Modifier.clickable(onClick = {
                                onEvent(CalendarEvents.GoToEvent(it))

                            })
                        )
                    }, startHour = startHour
                )
            }


        }
    }

}