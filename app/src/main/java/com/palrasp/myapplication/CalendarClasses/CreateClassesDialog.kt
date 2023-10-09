package com.palrasp.myapplication.CalendarClasses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.palrasp.myapplication.TimePicker.WheelTimePicker
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun CreateClassesDialog(modifier:Modifier,    onClick: (dayOfTheWeek: DayOfWeek, startTime: LocalTime, endTime: LocalTime) -> Unit){
    val timeTextStyle= TextStyle(fontFamily = Lexend, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,color= PlannerTheme.colors.textPrimary)
    var selectedDayOfWeek by remember { mutableStateOf(DayOfWeek.MONDAY) }

    var startTime =LocalTime.of(9, 45)
    var endTime = LocalTime.of(12, 0)

    Box(modifier = modifier
        .background(Color.White)
        .padding(16.dp)){
        Column() {

        Row() {
            // Day of the week picker
            DayOfWeekPicker(
                selectedDay = selectedDayOfWeek,
                onDaySelected = { day ->
                    selectedDayOfWeek = day
                },
                modifier = Modifier.padding(16.dp)
            )
            Column() {


                WheelTimePicker(
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = Color.White,
                        border = null
                    ),
                    startTime = LocalTime.now(),
                    textStyle = timeTextStyle,
                    size = DpSize(100.dp, 100.dp)
                ) {
                    startTime=it
                }
                Spacer(modifier = Modifier.height(24.dp))
                WheelTimePicker(
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = Color.White,
                        border = null
                    ),
                    startTime = LocalTime.now(),
                    textStyle = timeTextStyle,
                    size = DpSize(100.dp, 100.dp)
                ) {
                    endTime = it
                }



            }

        }

            Button(onClick = { onClick(selectedDayOfWeek,startTime,endTime) }) {
                Text(text = "confirm")
            }
        }


    }
}

@Composable
fun WheelPicker(
    items: List<String>,
    selectedItem: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            Text(
                text = item,
                color = if (item == selectedItem) Color.Black else Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable {
                        onValueChange(item)
                    }
                    .padding(vertical = 16.dp)
            )
        }
    }
}
@Composable
fun DayOfWeekPicker(
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    val selectedDayName = remember { mutableStateOf(selectedDay.name) }

    WheelPicker(
        items = daysOfWeek.map { it.name },
        selectedItem = selectedDayName.value,
        onValueChange = {
            selectedDayName.value = it
            onDaySelected(DayOfWeek.valueOf(it))
        },
        modifier = modifier
    )
}
