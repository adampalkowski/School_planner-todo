package com.palrasp.myapplication.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme

sealed class CalendarEvents {
    class GetEventsForWeek(val firstDayOfWeek: String, val endOfWeek: String) : CalendarEvents()
    object GoToLesson : CalendarEvents()
    object GoToCreate : CalendarEvents()
    class GoToEvent(val event: Event) : CalendarEvents()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalendarScreen(onEvent: (CalendarEvents) -> Unit, classes: List<Event>) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed,
        )
    )

    BottomSheetScaffold(sheetShape = RoundedCornerShape(topEnd = 48.dp),
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
            ) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "SVG Image",
                        modifier = Modifier.size(52.dp),
                        contentScale = ContentScale.Fit, // Adjust contentScale as needed
                    )
                    Text(
                        text = "Weekly todo", style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            fontFamily = Lexend, color = Color(0xFF5317FF)
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    if (bottomSheetScaffoldState.bottomSheetState.isExpanded){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_down),
                            contentDescription = null,
                            tint = Color(
                                0xFF5317FF
                            )
                        )
                    }else{
                        Icon(
                            painter = painterResource(id = R.drawable.ic_up),
                            contentDescription = null,
                            tint = Color(
                                0xFF5317FF
                            )
                        )
                    }



                }
                LazyColumn {
                    var currentWeek = ""
                    items(classes) { event ->
                        val weekName = event.start.dayOfWeek.name // Get the name of the week for the event

                        // If the week changes, display the week name
                        if (weekName != currentWeek) {
                            currentWeek = weekName
                            Text(
                                text = weekName,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                                fontWeight = FontWeight.Medium, fontFamily = Lexend,
                                fontSize = 14.sp,
                                color = Color(0xFFD1CFCF)
                            )
                        }

                        // Display the extracted lines for the event
                        event.extractedLines.forEach { line ->
                          Row(verticalAlignment = CenterVertically, modifier = Modifier.padding(horizontal = 24.dp)) {
                              CheckBoxPlanner(checked = false) {
                                  
                              }
                              Text(text = line, modifier = Modifier.padding(12.dp), color = Color.Black)
                          }  
                              
                        }
                    }
                }
            }


        },
        sheetPeekHeight = 80.dp,
        sheetContentColor = Color.White,
        sheetBackgroundColor = PlannerTheme.colors.uiBackground,
        sheetGesturesEnabled = true,
        modifier = Modifier.fillMaxSize(),
        contentColor = Color.Transparent,
        scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetScaffoldState.bottomSheetState)
    ) { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PlannerTheme.colors.uiBackground)
                .padding(it)
        ) {
            val currentDate = LocalDate.now()

            var firstDayOfWeek by remember {
                mutableStateOf(
                    currentDate.with(
                        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                    )
                )
            }
            var endOfWeek = firstDayOfWeek.plusDays(6)

            LaunchedEffect(firstDayOfWeek) {
                onEvent(
                    CalendarEvents.GetEventsForWeek(
                        firstDayOfWeek.toString(),
                        endOfWeek.toString()
                    )
                )

            }
            var selectedMonth by remember { mutableStateOf(firstDayOfWeek.month) }

            Column() {
                TopBar(
                    iconColor = Color(0xFF2A1A61),
                    lessonsClicked = {
                        onEvent(CalendarEvents.GoToLesson)
                    },
                    NextWeek = {
                        firstDayOfWeek = firstDayOfWeek.plusWeeks(1)
                        selectedMonth =
                            firstDayOfWeek.month // Update selectedMonth when firstDayOfWeek changes

                    },
                    PrevWeek = {
                        firstDayOfWeek = firstDayOfWeek.minusWeeks(1)
                        selectedMonth =
                            firstDayOfWeek.month // Update selectedMonth when firstDayOfWeek changes

                    },
                    openMonthPicker = { },
                    selectedMonth = selectedMonth.name
                )
                Schedule(modifier = Modifier,
                    events = classes,
                    minDate = firstDayOfWeek,
                    maxDate = firstDayOfWeek.plusDays(4),
                    classesContent = {
                        BasicClass(
                            event = it,
                            modifier = Modifier.clickable(onClick = {
                                onEvent(CalendarEvents.GoToEvent(it))

                            }),
                            important = true
                        )
                    })
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 24.dp, end = 24.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0EABFF))
                    .clickable(onClick = {
                        onEvent(CalendarEvents.GoToCreate)
                    })
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = com.palrasp.myapplication.R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}