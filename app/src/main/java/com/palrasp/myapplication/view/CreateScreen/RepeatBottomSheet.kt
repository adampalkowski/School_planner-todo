package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreenEvent
import com.palrasp.myapplication.view.mediumTextStyle
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatBottomSheet(onDismissRequest:()->Unit,eventState: MutableState<Event>) {
    val recurrencePatterns: List<Pair<RecurrencePattern, String>> = listOf(
        Pair(RecurrencePattern.DAILY, stringResource(id = R.string.daily)),
        Pair(RecurrencePattern.WEEKLY, stringResource(id = R.string.weekly) ),
        Pair(RecurrencePattern.TWOWEEKS, stringResource(id = R.string.twoWeeks)),
        Pair(RecurrencePattern.MONTHLY,  stringResource(id = R.string.monthly)),
    )
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest =onDismissRequest,
        sheetState = modalBottomSheetState,        containerColor = PlannerTheme.colors.uiBackground,

        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        // Sheet content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            for ((repeat,displayName) in recurrencePatterns) {
                if (repeat == eventState.value.getRecurrence()?.pattern) {
                    Text(textAlign = TextAlign.Center,
                        text = displayName, style = mediumTextStyle, color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color(0xFF47A0FF)
                            )
                            .clickable {
                                val recurance=Recurrence(pattern=repeat)
                                eventState.value.setRecurrence(recurance)
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    )
                } else {
                    Text(textAlign = TextAlign.Center,
                        text =displayName, style = mediumTextStyle,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth() .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val recurance=Recurrence(pattern=repeat)
                                val gson = Gson()
                                val recurrenceJson = gson.toJson(recurance)
                                eventState.value=eventState.value.copy(recurrenceJson =recurrenceJson )
                                onDismissRequest()

                            }
                            .padding(8.dp)
                    )
                }

            }
        }
    }
}

