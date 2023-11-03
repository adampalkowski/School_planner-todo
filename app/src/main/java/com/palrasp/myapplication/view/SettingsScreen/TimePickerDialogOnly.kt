package com.palrasp.myapplication.view.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.SettingsScreenEvents

import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogOnly(onDismissRequest: () -> Unit, onEvent: (SettingsScreenEvents) -> Unit, startHour: MutableState<Float>) {
    val startState = rememberTimePickerState(is24Hour = true, initialMinute =((startHour.value - startHour.value.toInt()) * 60).toInt() , initialHour =startHour.value.toInt())

    val textLabel =
        stringResource(id = R.string.calendar_time)

    Dialog(onDismissRequest = onDismissRequest) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
            rememberScrollState())) {

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
                    Text(
                        text = textLabel,
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {

                    Spacer(modifier = Modifier.height(16.dp))

                    TimeInput(
                        state = startState,
                        modifier = Modifier.padding(12.dp),
                        colors= TimePickerDefaults.colors(timeSelectorSelectedContainerColor =PlannerTheme.colors.uiBackground, timeSelectorUnselectedContainerColor =PlannerTheme.colors.uiBackground, timeSelectorSelectedContentColor =  PlannerTheme.colors.textSecondary, timeSelectorUnselectedContentColor = PlannerTheme.colors.textSecondary)

                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier.clickable(onClick = onDismissRequest),
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(id = R.string.confirm),
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = PlannerTheme.colors.textInteractive
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