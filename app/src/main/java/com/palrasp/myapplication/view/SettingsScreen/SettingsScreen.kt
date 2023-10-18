package com.palrasp.myapplication.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreen.CreateDivider
import com.palrasp.myapplication.view.CreateScreen.TimePickerSection
import com.palrasp.myapplication.view.CreateScreen.createTextStyle
import com.palrasp.myapplication.view.CreateScreen.selectedTextStyle
import java.time.LocalTime

enum class CalendarOption(val label: String) {
    FULL_WEEK("Full Week"),
    HALF_WEEK("Half Week"),
    WEEKEND("Weekend")
}

sealed class SettingsScreenEvents {
    object GoBack : SettingsScreenEvents()
    class ChangeCalendarOption(val option: CalendarOption) : SettingsScreenEvents()
    class ChangeStartHour(val hour: Float) : SettingsScreenEvents()
    object OpenChangeStartHour : SettingsScreenEvents()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenScheme(
    modifier: Modifier,
    onEvent: (SettingsScreenEvents) -> Unit,
    calendarOption: CalendarOption,
    startHour: MutableState<Float>,
) {
    var checked = remember {
        mutableStateOf(calendarOption)
    }
    LaunchedEffect(checked) {
        onEvent(SettingsScreenEvents.ChangeCalendarOption(checked.value))
    }
    Column(modifier = modifier.fillMaxSize()) {

        SettingTopPart(onEvent = onEvent)

        CreateDivider()
        CalendarOptionSection(checked,checkChanged={
            checked.value=it
            onEvent(SettingsScreenEvents.ChangeCalendarOption(it))
        })
        CreateDivider()
        SettingsTimePickerSection(onEvent = onEvent, startHour)

    }

}

@Composable
fun SettingTopPart(onEvent: (SettingsScreenEvents) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 24.dp)){
        IconButton(onClick = { onEvent(SettingsScreenEvents.GoBack)}) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)

        }
    }
}

@Composable
fun CalendarOptionSection(checked: MutableState<CalendarOption>,checkChanged:(CalendarOption)->Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            androidx.compose.material.Text(
                text = stringResource(id = R.string.calendar_option),
                style = createTextStyle,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                CalendarOptionPicker(
                    option = CalendarOption.FULL_WEEK,
                    checked = checked.value == CalendarOption.FULL_WEEK,
                    onCheckedChange = {
                        checkChanged(CalendarOption.FULL_WEEK)
                    })
                CalendarOptionPicker(
                    option = CalendarOption.HALF_WEEK,
                    checked = checked.value == CalendarOption.HALF_WEEK,
                    onCheckedChange = {
                        checkChanged(CalendarOption.HALF_WEEK)


                    })
                CalendarOptionPicker(
                    option = CalendarOption.WEEKEND,
                    checked = checked.value == CalendarOption.WEEKEND,
                    onCheckedChange = {
                        checkChanged(CalendarOption.WEEKEND)


                    })
            }


        }

    }

}

@Composable
fun SettingsTimePickerSection(
    onEvent: (SettingsScreenEvents) -> Unit,
    startHour: MutableState<Float>,
) {
    val hours = startHour.value.toInt() // Extract hours (integer part)
    val minutes =
        ((startHour.value - hours) * 60).toInt() // Extract minutes (fractional part converted to minutes)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            androidx.compose.material.Text(
                text = stringResource(id = R.string.inital_time),
                style = createTextStyle,
                color = textColor
            )
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(
                        BorderStroke(1.dp, color = Color(0xFFD6D6D6)),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable(onClick = {
                        onEvent(SettingsScreenEvents.OpenChangeStartHour)
                    })
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val startTimeText = String.format("%02d:%02d", hours, minutes)
                androidx.compose.material.Text(text = startTimeText, style = selectedTextStyle)
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier,
    onEvent: (SettingsScreenEvents) -> Unit,
    calendarOption: CalendarOption,
    startHour: MutableState<Float>,
) {
    BackHandler() {
        onEvent(SettingsScreenEvents.GoBack)
    }
    var currentEvent by remember { mutableStateOf<SettingsScreenEvents?>(null) }
    SettingsScreenScheme(calendarOption = calendarOption, startHour = startHour, onEvent = {
        when (it) {
            is SettingsScreenEvents.OpenChangeStartHour -> {
                currentEvent = it
            }
            is SettingsScreenEvents.ChangeCalendarOption -> {
                onEvent(SettingsScreenEvents.ChangeCalendarOption(it.option))
            }
            else -> {
                onEvent(it)
            }
        }
    }, modifier = Modifier)
    currentEvent?.let { event ->
        when (event) {
            is SettingsScreenEvents.OpenChangeStartHour -> {
                TimePickerDialogOnly(onDismissRequest = {
                    currentEvent = null
                }, onEvent = onEvent,startHour)
            }
            is SettingsScreenEvents.ChangeCalendarOption -> {
            }
            else -> {
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogOnly(onDismissRequest: () -> Unit, onEvent: (SettingsScreenEvents) -> Unit,startHour: MutableState<Float>) {
    val startState = rememberTimePickerState(is24Hour = true, initialMinute =((startHour.value - startHour.value.toInt()) * 60).toInt() , initialHour =startHour.value.toInt())

    val textLabel =
        stringResource(id = R.string.calendar_time)

    Dialog(onDismissRequest = onDismissRequest) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlannerTheme.colors.uiBackground)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    androidx.compose.material.Text(
                        text = textLabel,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                    )

                }
                Column(horizontalAlignment = Alignment.End) {

                    Spacer(modifier = Modifier.height(16.dp))

                    TimeInput(
                        state = startState,
                        modifier = Modifier.padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.material.Text(
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier.clickable(onClick = onDismissRequest),
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        androidx.compose.material.Text(
                            text = stringResource(id = R.string.confirm),
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = confirmColor
                            ),
                            modifier = Modifier.clickable(onClick = {
                                val newHour = startState.hour
                                val newMinute = startState.minute
                                val newTime = LocalTime.of(newHour, newMinute)
                                val combinedFloat = newTime.hour + newTime.minute / 60.0

                                onEvent(SettingsScreenEvents.ChangeStartHour(combinedFloat.toFloat()))
                                onDismissRequest()

                            })
                        )
                    }
                }

            }

        }

    }
}

@Composable
fun CalendarOptionPicker(option: CalendarOption, checked: Boolean, onCheckedChange: () -> Unit) {

    Row(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 0.dp).padding(start = 24.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0x2DE4E4E4))
            .padding(8.dp)
    ) {
        Text(
            text = option.label,
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.width(24.dp))
        CheckBoxPlanner(checked = checked, onCheckChanged = onCheckedChange)
    }
}