package com.palrasp.myapplication.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClassesGraph(
    daysHeader: @Composable () -> Unit,
    hoursItemsCount: Int,
    hourLabel: @Composable (index: Int) -> Unit,
    bar: @Composable ClassesGraphScope. (index: Int) -> Unit,
    modifier: Modifier = Modifier
){

    val hourLabels = @Composable{repeat(hoursItemsCount){hourLabel(it)} }
    val bars =@Composable{ repeat(hoursItemsCount){ ClassesGraphScope.bar(it) } }
    Layout(
        contents = listOf(daysHeader, hourLabels, bars),
        modifier = modifier.padding(bottom = 32.dp)
    ) {
        (dayHeaderMeasurables, hourLabelMeasurables, barMeasureables),  constraints,
        ->
        require(dayHeaderMeasurables.size == 1) {
            "hoursHeader should only emit one composable"
        }
        val daysHeaderPlaceable = dayHeaderMeasurables.first().measure(constraints)
        val hourLabelPlaceables = hourLabelMeasurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            placeable
        }
        var totalHeight = daysHeaderPlaceable.height

        val totalWidth = hourLabelPlaceables.first().width + daysHeaderPlaceable.width

        val barPlaceables = barMeasureables.map { measurable ->
            val barParentData = measurable.parentData as ClassesGraphParentData
            val barWidth = (barParentData.duration * daysHeaderPlaceable.width).roundToInt()

            val barPlaceable = measurable.measure(
                constraints.copy(
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
            totalHeight += barPlaceable.height
            barPlaceable
        }

        layout(totalWidth, totalHeight) {
            val xPosition = hourLabelPlaceables.first().width
            var yPosition = daysHeaderPlaceable.height
            daysHeaderPlaceable.place(xPosition, 0)
             hourLabelPlaceables.forEachIndexed { index, barPlaceable ->
                 barPlaceable.place(x = 0, y = yPosition)
                 yPosition+=barPlaceable.height

             }

           /*barPlaceables.forEachIndexed { index, barPlaceable ->
                val barParentData = barPlaceable.parentData as ClassesGraphParentData
                val barOffset = (barParentData.offset * daysHeaderPlaceable.width).roundToInt()
                barPlaceable.place(xPosition + barOffset, yPosition)

                // the label depend on the size of the bar content - so should use the same y
                val hourLabelPlaceables = hourLabelPlaceables[index]
                hourLabelPlaceables.place(x = 0, y = yPosition)
                yPosition += barPlaceable.height
            }*/

        }

    }
}


@LayoutScopeMarker
@Immutable
object ClassesGraphScope{
    @Stable
    fun Modifier.classesGraphBar(
        start: LocalDateTime,
        end: LocalDateTime,
        hours: List<Int>,
    ):Modifier{

        val earliestTime = LocalTime.of(hours.first(), 0)
        val durationInHours = ChronoUnit.MINUTES.between(start, end) / 60f
        val durationFromEarliestToStartInHours =
            ChronoUnit.MINUTES.between(earliestTime, start.toLocalTime()) / 60f
        val offsetInHours = durationFromEarliestToStartInHours + 0.5f
        return then(
            ClassesGraphParentData(
                duration = durationInHours / hours.size,
                offset = offsetInHours / hours.size
            )
        )
    }
}

class ClassesGraphParentData(
    val duration: Float,
    val offset: Float,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = this@ClassesGraphParentData
}

