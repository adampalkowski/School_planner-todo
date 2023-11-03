package com.palrasp.myapplication.CalendarClasses

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreen.CreateDivider
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
val sideBarTextSize=10.sp
val sideBarTextStyle=TextStyle(fontFamily = Lexend, fontSize = sideBarTextSize, fontWeight = FontWeight.SemiBold)
@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    classesContent: @Composable (event:Event) -> Unit,
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now().plusDays(4),
    startHour:Float
){
    val hourHeight = 64.dp
    //move the inital state to 8 oclock
    val pixels=LocalDensity.current.run { hourHeight.toPx() }
    val verticalScrollState = rememberScrollState(initial=(pixels*startHour).toInt())
    val horizontalScrollState = rememberScrollState()
    // Get the screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val days=ChronoUnit.DAYS.between(minDate, maxDate)+1
    // Calculate the dayWidth as 1/5 of the screen width
    var sidebarWidth by remember { mutableStateOf(0) }
    val dayWidth = ((screenWidth-sidebarWidth.dp) / days.toFloat())+5.dp
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()                    .background(color = PlannerTheme.colors.uiBackground)
        ){
            ScheduleHeader(
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = dayWidth,
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })

            )
        }
        CreateDivider()

        Row(modifier = Modifier.fillMaxWidth()) {
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
                modifier = Modifier.padding(top=12.dp)
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            )
        }

    }

}

@Composable
fun BasicSchedule(
    modifier: Modifier,
    events: List<Event>,
    classesContent: @Composable (event:Event) -> Unit ,
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
    dayWidth: Dp,
    hourHeight: Dp,
) {
    val dividerColor= PlannerTheme.colors.iconInteractiveInactive
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
            repeat(48) {
                val offsetY = (it * (hourHeight.toPx() / 2)).toFloat() // 30 minutes interval
                if (it % 2 == 0) {
                    // Draw a line for full hours
                    drawLine(
                        dividerColor,
                        start = Offset(0f, offsetY),
                        end = Offset(size.width, offsetY),
                        strokeWidth = 0.8.dp.toPx()
                    )
                } else {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, offsetY),
                        end = Offset(size.width, offsetY),
                        strokeWidth = 0.5.dp.toPx()
                    )
                }

            }
            repeat(numDays - 1) {
                drawLine(
                    dividerColor,
                    start = Offset((it + 1) * dayWidth.toPx(), 0f),
                    end = Offset((it + 1) * dayWidth.toPx(), size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
        },
    ) { measureables, constraints ->
        var height = hourHeight.roundToPx() * 24
        val width = dayWidth.roundToPx() * numDays

        val placeablesWithEvents = measureables.mapNotNull { measurable ->
            val event = measurable.parentData as? Event
            val eventDurationMinutes = event?.let { ChronoUnit.MINUTES.between(it.start, it.end) } ?: 0
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(constraints.copy(minWidth = dayWidth.roundToPx()-20, maxWidth = dayWidth.roundToPx()-20, minHeight = eventHeight, maxHeight = eventHeight))
            Pair(placeable, event)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, event) ->
                event?.let {
                    val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.MIN, it.start.toLocalTime())
                    val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                    val eventOffsetDays = ChronoUnit.DAYS.between(minDate, it.start.toLocalDate()).toInt()
                    val eventX = eventOffsetDays * dayWidth.roundToPx()+10
                    placeable.place(eventX, eventY)
                }
            }
        }
    }
}
data class Recurrence(
    val pattern: RecurrencePattern,
    val endDate: LocalDate? = null, // Optional end date for recurring events
    val count: Int? = null // Optional count for how many times the event should repeat
)

enum class RecurrencePattern {
    WEEKLY,
    MONTHLY,
    DAILY,
    TWOWEEKS,
}
data class EventInstance(
    val event: Event,
    val startTime: LocalTime
)
