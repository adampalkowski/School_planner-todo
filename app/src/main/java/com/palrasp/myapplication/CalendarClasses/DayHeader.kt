package com.palrasp.myapplication.CalendarClasses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.dividerColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val DayFormatText = DateTimeFormatter.ofPattern("E")
val DayFormatNumber = DateTimeFormatter.ofPattern("d")

val colorArray = listOf(
    Color(0xFFE57373), // Red
    Color(0xFF81C784), // Green
    Color(0xFF64B5F6), // Blue
    Color(0xFFFFD54F), // Yellow
    Color(0xFF9575CD)  // Purple
)
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
            Box(modifier = Modifier
                .width(dayWidth)
                ){
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
    val isToday = day == LocalDate.now()
    var textColor =if (isToday){
        Color(0xFFFFFFFF)
    }else{
        Color(0xFF303030)

    }
    Card( shape = RoundedCornerShape(0.dp), contentColor = Color.Transparent, elevation =
        0.dp,
        backgroundColor = if (isToday) Color(0xFF66A4FF) else Color.Transparent
    )
    {
        Column(modifier=Modifier.padding(4.dp)) {
            Text(
                text = day.format(DayFormatText),
                color = textColor,style= TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light, fontFamily = Lexend, color = textColor),
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
            )
            Text(
                text = day.format(DayFormatNumber),
                color = textColor,
                style= TextStyle( fontWeight = FontWeight.Medium, fontFamily = Lexend, color = textColor),
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
            )
        }
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