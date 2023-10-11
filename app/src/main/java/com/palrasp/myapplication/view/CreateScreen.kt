package com.palrasp.myapplication.view

import android.widget.EditText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.PlannerEditText
import com.palrasp.myapplication.utils.TextFieldState
import java.time.DayOfWeek
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
    onAccept: (DayOfWeek, LocalTime, LocalTime, String, Color, String, Boolean) -> Unit,
) {
    var selectedDayOfWeek by remember { mutableStateOf(DayOfWeek.MONDAY) }
    var eventName by remember { mutableStateOf(TextFieldState()) } // State for the event name
    var eventClass by remember { mutableStateOf(TextFieldState()) } // State for the event class

    var isDayPickerVisible by remember { mutableStateOf(false) }
    var isColorPickerVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val timeTextStyle = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = PlannerTheme.colors.textPrimary
    )
    var selectedColor by remember { mutableStateOf(Color(0xFF3C8ADC)) }

    val focusRequester = remember { FocusRequester() }
    var startTime by remember { mutableStateOf(LocalTime.of(9, 45)) }
    var endTime by remember { mutableStateOf(LocalTime.of(12,0)) }
    val startState = rememberTimePickerState(is24Hour = true)
    val endState = rememberTimePickerState(is24Hour = true)

    val scope = rememberCoroutineScope()
    var compulsory by remember { mutableStateOf(false) }
    var displayStartTimeDialog by remember { mutableStateOf(false) }
    var displayEndTimeDialog by remember { mutableStateOf(false) }
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
                Spacer(modifier = Modifier.width(48.dp))
                Switch(checked = compulsory, onCheckedChange = {
                    compulsory = it
                })
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
                            eventClass.text, compulsory
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

                Box(modifier = Modifier.clickable(onClick = {
                    displayStartTimeDialog = true
                })) {
                    Text(text = startTime.hour.toString() + ":" + startTime.minute)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Box(modifier = Modifier.clickable(onClick = {
                    displayEndTimeDialog = true

                })) {
                    Text(text = endTime.hour.toString() + ":" + endTime.minute)
                }


            }
            var textState by remember { mutableStateOf(TextFieldValue("Hello World")) }


            CreateItem(label="Class name",onClick = { }){
                BasicTextField(value = textState,
                    onValueChange = {
                    textState = it
                }                   , singleLine = true,                    textStyle = TextStyle(color = Color.Black)
                    )
            }

            CreateItem(label="Time",onClick = { }){
                Text(
                    text = selectedDayOfWeek.name.toString(), modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp), style = mediumTextStyle
                )
            }


            CreateItem(label="Day of the week",onClick = { isDayPickerVisible = true }){
                Text(
                    text = selectedDayOfWeek.name.toString(), modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp), style = mediumTextStyle
                )
            }
            CreateItem(label="Color",onClick = { isColorPickerVisible = true }){
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color = selectedColor, shape = CircleShape)
                       ,
                    contentAlignment = Alignment.Center
                ) {

                }

            }

        }
        if (displayStartTimeDialog) {
            Dialog(onDismissRequest = { displayStartTimeDialog = false }) {
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
                            text = "End time",
                            style= TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                        )

                    }
                    Column(horizontalAlignment = Alignment.End) {

                        Spacer(modifier = Modifier.height(16.dp))

                        TimeInput(
                            state = startState,
                            modifier = Modifier.padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Cancel",
                                modifier = Modifier.clickable(onClick = {
                                    displayStartTimeDialog = false
                                } )
                                ,style= TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Confirm",                                style= TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),modifier=Modifier.clickable(onClick = {
                                val newHour = startState.hour
                                val newMinute = startState.minute
                                startTime = LocalTime.of(newHour, newMinute)
                                displayStartTimeDialog=false
                            })
                            )
                        }
                    }

                }

            }

        }

        if (displayEndTimeDialog) {
            Dialog(onDismissRequest = { displayEndTimeDialog = false }) {
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
                            text = "End time",
                            style= TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                        )

                    }
                    Column(horizontalAlignment = Alignment.End) {

                        Spacer(modifier = Modifier.height(16.dp))

                        TimeInput(
                            state = endState,
                            modifier = Modifier.padding(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Cancel",
                                modifier = Modifier.clickable(onClick = {
                                    displayEndTimeDialog = false
                                } )
                                ,style= TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Confirm",                                style= TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),modifier=Modifier
                                .clickable(onClick = {
                                    val newHour = endState.hour
                                    val newMinute = endState.minute
                                    endTime = LocalTime.of(newHour, newMinute)
                                    displayEndTimeDialog = false
                                })

                            )
                        }
                    }

                }

            }
        }

    }
    if(isColorPickerVisible){
        ModalBottomSheet(
            onDismissRequest = {
                isColorPickerVisible = false
            },
            sheetState = sheetState,

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
                    items(colors) { color ->
                        ColorSwatch(
                            color = color,
                            onColorSelected = { selectedColorVal ->
                                selectedColor=selectedColorVal
                                isColorPickerVisible = false
                            },
                            isSelected = color == selectedColor,

                            )
                    }
                }

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

@Composable
fun CreateItem(label:String,onClick:()->Unit,content:@Composable ()->Unit){

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column (horizontalAlignment = Alignment.Start,modifier=Modifier.padding(horizontal = 24.dp)){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text =label,color=Color(0xFFC9C9C9))
                Spacer(modifier = Modifier.weight(1f))
                content()
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .background(
                    Color(
                        0xFFE0E0E0
                    )
                ))
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