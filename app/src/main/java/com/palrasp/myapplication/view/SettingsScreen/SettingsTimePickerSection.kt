package com.palrasp.myapplication.view.SettingsScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.view.CreateScreen.createTextStyle
import com.palrasp.myapplication.view.CreateScreen.selectedTextStyle
import com.palrasp.myapplication.view.SettingsScreenEvents
import com.palrasp.myapplication.view.textColor


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

            Text(
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
                Text(text = startTimeText, style = selectedTextStyle)
            }

        }

    }

}
