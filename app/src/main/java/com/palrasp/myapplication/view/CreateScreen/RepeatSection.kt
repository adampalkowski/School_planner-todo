package com.palrasp.myapplication.view.CreateScreen

import android.content.Context
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreenEvent

fun getRecurrencePatternStringResource(pattern: RecurrencePattern, context: Context): String {
    val patternStringResId = when (pattern) {
        RecurrencePattern.DAILY -> R.string.daily
        RecurrencePattern.WEEKLY -> R.string.weekly
        RecurrencePattern.TWOWEEKS -> R.string.twoWeeks
        RecurrencePattern.MONTHLY -> R.string.monthly
        // Handle additional patterns as needed
    }

    return context.getString(patternStringResId)
}
@Composable
fun RepeatSection(event: MutableState<Event>,onEvent:(CreateScreenEvent)->Unit){
    val recurrence=event.value.getRecurrence()
    val context= LocalContext.current
    if (recurrence==null){
        event.value.setRecurrence(Recurrence(RecurrencePattern.WEEKLY))
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.repeat), style = createTextStyle, color = PlannerTheme.colors.textSecondary)
            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = {onEvent(CreateScreenEvent.OpenRepeatPicker)})){

                Text(text = getRecurrencePatternStringResource( event.value.getRecurrence()!!.pattern,context), color = PlannerTheme.colors.textSecondary)
            }

        }

    }
}