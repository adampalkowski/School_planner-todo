package com.palrasp.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.palrasp.myapplication.CalendarClasses.DayOfWeekPicker
import com.palrasp.myapplication.R
import com.palrasp.myapplication.TimePicker.WheelTimePicker
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.PlannerEditText
import com.palrasp.myapplication.utils.TextFieldState
import com.palrasp.myapplication.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

var mediumTextStyle = TextStyle(
    fontFamily = Lexend,
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    color = Color.Black
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    modifier: Modifier,
    onBack: () -> Unit,
    onAccept: (DayOfWeek, LocalTime, LocalTime, String, Color,String) -> Unit,
) {
    var selectedDayOfWeek by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var eventName by remember { mutableStateOf(TextFieldState()) } // State for the event name
    var eventClass by remember { mutableStateOf(TextFieldState()) } // State for the event class

    var isDayPickerVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val timeTextStyle = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = PlannerTheme.colors.textPrimary
    )
    val focusRequester = remember { FocusRequester() }
    var startTime = LocalTime.of(9, 45)
    var endTime = LocalTime.of(12, 0)
    var selectedColor by remember { mutableStateOf(Color(0xFF3C8ADC)) }

    val scope = rememberCoroutineScope()
    Box(modifier = modifier.fillMaxSize()) {


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                IconButton(
                    onClick = onBack, modifier = Modifier
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0094FF))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable(onClick = {
                        onAccept(
                            selectedDayOfWeek,
                            startTime,
                            endTime,
                            eventName.text,
                            selectedColor,
                            eventClass.text
                        )
                    }
                    )) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Save",
                        style = timeTextStyle,
                        color = Color.White
                    )
                }

            }
            PlannerEditText(
                modifier = modifier,
                focusRequester = focusRequester,
                focus = false,
                onFocusChange = { focusState ->
                }, label = "Class name",
                textState = eventName
            )
            Spacer(modifier = Modifier.height(24.dp))
            PlannerEditText(
                modifier = modifier,
                focusRequester = focusRequester,
                focus = false,
                onFocusChange = { focusState ->
                }, label = "Class number",
                textState = eventClass
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                WheelTimePicker(
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = Color.White,
                        border = null
                    ),
                    startTime = startTime,
                    textStyle = timeTextStyle,
                    size = DpSize(100.dp, 100.dp)
                ) {
                    startTime = it
                }
                Spacer(modifier = Modifier.height(24.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_long_right),
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))

                WheelTimePicker(
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = Color.White,
                        border = null
                    ),
                    startTime = endTime,
                    textStyle = timeTextStyle,
                    size = DpSize(100.dp, 100.dp)
                ) {
                    endTime = it
                }

            }

            ColorPicker(onColorPicker = { color ->
                selectedColor = color
            }, selectedColor)


            Box(modifier = Modifier.clickable(onClick = { isDayPickerVisible = true })) {
                Text(
                    text = selectedDayOfWeek.name.toString(), modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Color(
                                0x5E0C81FF
                            )
                        )
                        .padding(horizontal = 12.dp), style = mediumTextStyle
                )
            }

        }


    }
    if (isDayPickerVisible) {

        ModalBottomSheet(
            onDismissRequest = {
                isDayPickerVisible = false
            },
            sheetState = sheetState,

        ) {
            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                for (day in DayOfWeek.values()) {
                    if (day == selectedDayOfWeek) {
                        Text(textAlign = TextAlign.Center,
                            text = day.toString(), style = mediumTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Color(
                                        0x5E0C81FF
                                    )
                                )
                                .clickable {
                                    selectedDayOfWeek = day
                                    isDayPickerVisible = false
                                }
                                .padding(8.dp)
                        )
                    } else {
                        Text(textAlign = TextAlign.Center,
                            text = day.toString(), style = mediumTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDayOfWeek = day
                                    isDayPickerVisible = false
                                }
                                .padding(8.dp)
                        )
                    }

                }
            }

        }
    }


}

@Composable
fun ColorPicker(onColorPicker: (Color) -> Unit, selectedColor: Color?) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(colors) { color ->
            ColorSwatch(
                color = color,
                onColorSelected = { selectedColor ->
                    onColorPicker(selectedColor)
                },
                isSelected = color == selectedColor,

                )
        }
    }
}

@Composable
fun ColorSwatch(color: Color, isSelected: Boolean, onColorSelected: (Color) -> Unit) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(color = color, shape = CircleShape)
                .clickable {
                    onColorSelected(color)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White
            )
        }
    } else {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(color = color, shape = CircleShape)
                .clickable {
                    onColorSelected(color)
                },
            contentAlignment = Alignment.Center
        ) {

        }
    }

}

val colors = listOf(
    Color(0xFF52B69A),
    Color(0xFF1BA1EC),
    Color(0xFFFF8800),
    Color(0xFF25A244),
    Color(0xFF293241),
    Color(0xFFBC4749),
    Color(0xFFE09F3E),
    Color(0xFF1A759F),

    Color(0xFF4AD66D),

    )