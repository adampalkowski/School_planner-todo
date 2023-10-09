package com.palrasp.myapplication.CalendarClasses

import android.util.Log
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?)= event
}
private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))
val sideBarTextSize=12.sp
val sideBarTextStyle=TextStyle(fontFamily = Lexend, fontSize = sideBarTextSize, fontWeight = FontWeight.SemiBold, color = Color(0xFF010045))
@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    classesContent: @Composable (event:Event) -> Unit = { BasicClass(event = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate(),
){
    val hourHeight = 64.dp
    //move the inital state to 8 oclock
    val pixels=LocalDensity.current.run { hourHeight.toPx() }
    val verticalScrollState = rememberScrollState(initial=(pixels*8).toInt())
    val horizontalScrollState = rememberScrollState()
    // Get the screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Calculate the dayWidth as 1/5 of the screen width
    Log.d("ScreenSize",screenWidth.toString())
    var sidebarWidth by remember { mutableStateOf(0) }
    Log.d("ScreenSize","sidebar"+sidebarWidth.toString())
    val dayWidth = ((screenWidth-sidebarWidth.dp) / 5f)
    Log.d("ScreenSize","daywidth"+dayWidth.toString())
    Log.d("VERTICALSCREOL",verticalScrollState.value.toString())
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        ScheduleHeader(
            minDate = minDate,
            maxDate = maxDate,
            dayWidth = dayWidth,
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })

        )
        Row(modifier = Modifier.weight(1f)) {
            ScheduleSidebar(
                hourHeight = hourHeight,
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
                    .onGloballyPositioned { sidebarWidth = it.size.width },
                sideBarTextStyle
            )
            BasicSchedule(
                events = events,
                classesContent = classesContent,
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = dayWidth,
                hourHeight = hourHeight,
                modifier = Modifier.padding(top =(sideBarTextSize).value.dp)
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            )
        }

    }

}

@Composable
fun BasicSchedule(
    modifier: Modifier, events: List<Event>,
    classesContent: @Composable (event:Event) -> Unit = { BasicClass(event = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)!!.start.toLocalDate(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)!!.end.toLocalDate(),
    dayWidth: Dp,
    hourHeight: Dp,
) {
    val dividerColor = Color.LightGray

    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    Layout(
        content = {
            events.sortedBy(Event::start).forEach { event ->
                Box(modifier = Modifier.eventData(event)) {
                    classesContent(event)
                }
            }
        },
        modifier = modifier.drawBehind {
            repeat(23) {
                drawLine(
                    dividerColor,
                    start = Offset(0f, (it + 1) * hourHeight.toPx()),
                    end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            repeat(numDays - 1) {
                drawLine(
                    dividerColor,
                    start = Offset((it + 1) * dayWidth.toPx(), 0f),
                    end = Offset((it + 1) * dayWidth.toPx(), size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        ,
    ) { measureables, constraints ->
        var height = hourHeight.roundToPx() * 24
        val width = dayWidth.roundToPx() * numDays


        val placeablesWithEvents = measureables.map { measurable ->
            val event = measurable.parentData as Event
            val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(constraints.copy(minWidth = dayWidth.roundToPx(), maxWidth = dayWidth.roundToPx(), minHeight = eventHeight, maxHeight = eventHeight))
            Pair(placeable, event)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays = ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt()
                val eventX = eventOffsetDays * dayWidth.roundToPx()
                placeable.place(eventX, eventY)
            }
        }
    }
}

@Preview
@Composable
fun PreviewSchedule(){
    Schedule(modifier = Modifier, events = fakeClasses)
}