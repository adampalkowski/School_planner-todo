package com.palrasp.myapplication.view

import android.util.Log
import android.widget.ProgressBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.CalendarClasses.BasicClass
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.CalendarClasses.Schedule
import com.palrasp.myapplication.Screen
import com.palrasp.myapplication.utils.TopBar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreen.ButtonCreate
import com.palrasp.myapplication.view.CreateScreen.CreateDivider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
    calendarOption:CalendarOption,
    onEvent: (CalendarEvents) -> Unit,
    classes: List<Event>,
    startHour:Float
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

    when(calendarOption){
        CalendarOption.HALF_WEEK->{
            daysLength.value=4
        }
        CalendarOption.FULL_WEEK->{
            daysLength.value=6

        }
        CalendarOption.WEEKEND->{
            daysLength.value=2
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
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                val ratio = remember { mutableStateOf(0f) }
                Row(
                    verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {

                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, Color(0xADE2E2E2)),
                                shape = RoundedCornerShape(100.dp)
                            )
                            .clip(RoundedCornerShape(100.dp))
                            .background(color = Color.White)
                            .clickable(onClick = {
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    }
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

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.weekly) + " todo",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                fontFamily = Lexend, color = Color(0xFF353535)
                            )
                        )
                    }

                    val daysOfWeek = listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY,
                        DayOfWeek.SUNDAY
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
                    var previousEventName by remember { mutableStateOf<String?>(null) }

                    LazyColumn {
                        for (day in daysOfWeek) {
                            // Display the day of the week as a header
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .background(color = dividerColor)
                                            .width(24.dp)
                                    )
                                    Text(
                                        text = day.getDisplayName(
                                            java.time.format.TextStyle.FULL_STANDALONE,
                                            Locale.getDefault()
                                        ),
                                        modifier = Modifier.padding(bottom = 5.dp),
                                        fontWeight = FontWeight.Light, fontFamily = Lexend,
                                        fontSize = 14.sp,
                                        color = Color(0x4B000003)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .background(color = dividerColor)
                                            .weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                            }
                            val eventsForDay = classes.filter { it.dayOfTheWeek == day.value }


                            // Display the events for the current day
                            items(eventsForDay) { event ->
                                event.extractedLinesWithIndices.forEach { (index, line, checked) ->
                                    Column() {
                                        val showEventName = event.name != previousEventName
                                        if (showEventName) {
                                            // Render the event name text
                                            Text(
                                                text = event.name,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 24.dp),
                                                fontWeight = FontWeight.ExtraLight,
                                                fontSize = 10.sp,
                                                color = Color(0xFFD1CFCF)
                                            )
                                        }
                                        Row(
                                            verticalAlignment = CenterVertically,
                                            modifier = Modifier.padding(horizontal = 24.dp)
                                        ) {
                                            CheckBoxPlanner(checked = checked, onCheckChanged = {
                                                onEvent(CalendarEvents.UpdateEvent(event, index))
                                            })
                                            Text(
                                                text = line,
                                                modifier = Modifier.padding(12.dp),
                                                color = textColor,
                                                style = TextStyle(
                                                    color = textColor,
                                                    fontWeight = FontWeight.Normal,
                                                    fontFamily = Lexend
                                                )
                                            )
                                        }
                                        previousEventName = event.name

                                    }
                                }
                            }


                        }


                    }

                }

            }
        },
        sheetPeekHeight = 80.dp,
        sheetElevation = 0.dp,
        sheetContentColor = Color.White,
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
                    iconColor = textColor,
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
                    }, startHour = startHour)
            }


        }
    }

}