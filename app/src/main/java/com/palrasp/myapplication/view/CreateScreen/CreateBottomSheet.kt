package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.view.ColorSwatch
import com.palrasp.myapplication.view.mediumTextStyle
import java.time.DayOfWeek
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBottomSheet(onDismissRequest:()->Unit,eventState:MutableState<Event>) {

    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest =onDismissRequest,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {

        // Sheet content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            for (day in DayOfWeek.values()) {
                if (day.value == eventState.value.dayOfTheWeek) {
                    Text(textAlign = TextAlign.Center,
                        text = day.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, Locale.getDefault()), style = mediumTextStyle, color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth().padding(horizontal = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color(0xFF47A0FF)
                            )
                            .clickable {
                                eventState.value= eventState.value.copy(dayOfTheWeek =day.value)

                                onDismissRequest()

                            }
                            .padding(8.dp)
                    )
                } else {
                    Text(textAlign = TextAlign.Center,
                        text = day.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, Locale.getDefault()), style = mediumTextStyle,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                eventState.value= eventState.value.copy(dayOfTheWeek =day.value)
                                onDismissRequest()
                            }
                            .padding(8.dp)
                    )
                }

            }
        }
    }
}

val colorsPicker = listOf(
    Color(0xFF7DC1FF),

    Color(0xFF52B69A),
    Color(0xFF1BA1EC),
    Color(0xFFFF8800),
    Color(0xFF25A244),
    Color(0xFF293241),
    Color(0xFFBC4749),
    Color(0xFFE09F3E),
    Color(0xFF1A759F),

    Color(0xFF4AD66D),
    Color(0xFFbc6c25),
    Color(0xFFa2d2ff),
    Color(0xFF7d4f50),
    Color(0xFF5e548e),

    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateColorPickerBottomSheet(onDismissRequest:()->Unit,eventState:MutableState<Event>) {

    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest =onDismissRequest,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
// Sheet content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(colorsPicker) { color ->

                    ColorSwatch(
                        color = color,
                        onColorSelected = { selectedColorVal ->
                            eventState.value=eventState.value.copy(color = selectedColorVal)
                            onDismissRequest()
                        },
                        isSelected = color ==   eventState.value.color,
                        )
                }
            }

        }

    }
}