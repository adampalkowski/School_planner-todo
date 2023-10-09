package com.palrasp.myapplication.CalendarClasses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.TimeZone

private val HourFormatter = DateTimeFormatter.ofPattern("H")

@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,sideBarTextStyle:androidx.compose.ui.text.TextStyle,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it,sideBarTextStyle=sideBarTextStyle) },
) {
    Column(modifier = modifier) {
        val startTime = LocalTime.MIDNIGHT
        repeat(24) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,sideBarTextStyle:androidx.compose.ui.text.TextStyle=androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
) {
    Text(
        text = time.format(HourFormatter),
        style = sideBarTextStyle,
        modifier = modifier
            .fillMaxHeight()
            .padding(6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
    ScheduleSidebar(hourHeight = 64.dp, sideBarTextStyle=androidx.compose.ui.text.TextStyle())
}