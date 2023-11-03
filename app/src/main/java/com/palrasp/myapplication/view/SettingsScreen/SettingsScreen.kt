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
import com.palrasp.myapplication.view.SettingsScreen.CalendarOptionSection
import com.palrasp.myapplication.view.SettingsScreen.SettingTopPart
import com.palrasp.myapplication.view.SettingsScreen.SettingsTimePickerSection
import com.palrasp.myapplication.view.SettingsScreen.TimePickerDialogOnly
import java.time.LocalTime

enum class CalendarOption(val label: String) {
    FULL_WEEK("Full_week"),
    HALF_WEEK("Half_Week"),
    WEEKEND("Weekend")
}

sealed class SettingsScreenEvents {
    object GoBack : SettingsScreenEvents()
    class ChangeCalendarOption(val option: CalendarOption) : SettingsScreenEvents()
    class ChangeStartHour(val hour: Float) : SettingsScreenEvents()
    object OpenChangeStartHour : SettingsScreenEvents()
}

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
        CalendarOptionSection(checked, checkChanged = {
            checked.value = it
            onEvent(SettingsScreenEvents.ChangeCalendarOption(it))
        })
        CreateDivider()
        SettingsTimePickerSection(onEvent = onEvent, startHour)

    }

}


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
    }, modifier = modifier)
    currentEvent?.let { event ->
        when (event) {
            is SettingsScreenEvents.OpenChangeStartHour -> {
                TimePickerDialogOnly(onDismissRequest = {
                    currentEvent = null
                }, onEvent = onEvent, startHour)
            }
            is SettingsScreenEvents.ChangeCalendarOption -> {
            }
            else -> {
            }
        }
    }

}



