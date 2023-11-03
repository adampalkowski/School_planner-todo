package com.palrasp.myapplication.view.SettingsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CalendarOption
import com.palrasp.myapplication.view.CreateScreen.createTextStyle



@Composable
fun CalendarOptionSection(checked: MutableState<CalendarOption>, checkChanged:(CalendarOption)->Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = stringResource(id = R.string.calendar_option),
                style = createTextStyle,
                color = PlannerTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                CalendarOptionPicker(
                    option = CalendarOption.FULL_WEEK,
                    checked = checked.value == CalendarOption.FULL_WEEK,
                    onCheckedChange = {
                        checkChanged(CalendarOption.FULL_WEEK)
                    }, stringResource(id = R.string.Full_week)
                )
                CalendarOptionPicker(
                    option = CalendarOption.HALF_WEEK,
                    checked = checked.value == CalendarOption.HALF_WEEK,
                    onCheckedChange = {
                        checkChanged(CalendarOption.HALF_WEEK)


                    }, stringResource(id = R.string.Half_week)
                )
                CalendarOptionPicker(
                    option = CalendarOption.WEEKEND,
                    checked = checked.value == CalendarOption.WEEKEND,
                    onCheckedChange = {
                        checkChanged(CalendarOption.WEEKEND)


                    }, stringResource(id = R.string.Weekend)
                )
            }


        }

    }

}