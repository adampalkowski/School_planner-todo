package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.view.CreateScreenEvent
import com.palrasp.myapplication.view.confirmColor
import com.palrasp.myapplication.view.dividerColor
import com.palrasp.myapplication.view.textColor

@Composable
fun RepeatSection(event: MutableState<Event>,onEvent:(CreateScreenEvent)->Unit){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.repeat), style = createTextStyle, color = textColor)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.clickable(onClick = {onEvent(CreateScreenEvent.OpenRepeatPicker)})){
                Text(text = "REapet")
            }

        }

    }
}