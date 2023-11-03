package com.palrasp.myapplication.view.BottomSheet

import android.app.usage.UsageEvents.Event
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CalendarEvents
import com.palrasp.myapplication.view.CheckBoxPlanner


@Composable
fun TodosItem(event:com.palrasp.myapplication.CalendarClasses.Event,previousEventName: MutableState<String?>,onEvent:(CalendarEvents)->Unit){
    event.extractedLinesWithIndices.forEach { (index, line, checked) ->
        Column() {
            val showEventName = event.name != previousEventName.value
            if (showEventName) {
                // Render the event name text
                Text(
                    text = event.name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp),
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 10.sp,
                    color = PlannerTheme.colors.textSecondary

                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                CheckBoxPlanner(checked = checked, onCheckChanged = {
                    onEvent(CalendarEvents.UpdateEvent(event, index))
                })
                Text(
                    text = line,
                    modifier = Modifier.padding(12.dp),
                    color = PlannerTheme.colors.textSecondary,
                    style = TextStyle(
                        color = PlannerTheme.colors.textSecondary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Lexend
                    )
                )
            }
            previousEventName.value = event.name

        }
    }
}