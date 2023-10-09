package com.palrasp.myapplication.CalendarClasses

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val DayFormatText = DateTimeFormatter.ofPattern("E")
val DayFormatNumber = DateTimeFormatter.ofPattern("d")

@Composable
fun ScheduleHeader(
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Dp,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
) {
    Row(modifier = modifier) {
        val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
        repeat(numDays) { i ->
            Box(modifier = Modifier.width(dayWidth)) {
                dayHeader(minDate.plusDays(i.toLong()))
            }
        }
    }
}
@Composable
fun BasicDayHeader(
    day: LocalDate,
    modifier: Modifier = Modifier,
) {
    Column() {
        Text(
            text = day.format(DayFormatText),
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Text(
            text = day.format(DayFormatNumber),
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
    PlannerTheme {
        ScheduleHeader(
            minDate = LocalDate.now(),
            maxDate = LocalDate.now().plusDays(5),
            dayWidth = 256.dp,
        )
    }
}