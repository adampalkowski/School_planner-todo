package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme


@Composable
fun SubjectInput(eventState: MutableState<Event>, label: String = "Subject") {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Black,
        backgroundColor = Color.Gray.copy(alpha = 0.4f)
    )
    Column() {
        Box(Modifier.padding(horizontal = 32.dp).padding(top=16.dp)) {
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                BasicTextField(
                    value = eventState.value.name,
                    onValueChange = {
                        eventState.value = eventState.value.copy(name = it)
                    },
                    textStyle = TextStyle(
                        color = PlannerTheme.colors.textSecondary,
                        fontFamily = Lexend,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light
                    ), // Customize the text color
                    modifier = Modifier.fillMaxWidth()
                )

                if (eventState.value.name.isEmpty()) {
                    Text(
                        text = label,
                        style = TextStyle(
                            color = PlannerTheme.colors.textSecondary.copy(0.7f),
                            fontFamily = Lexend,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        CreateDivider()
    }
}
@Composable
fun ClassroomInput(eventState: MutableState<Event>, label: String = "Classroom") {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Black,
        backgroundColor = Color.Gray.copy(alpha = 0.4f)
    )
    Column() {
        Box(Modifier.padding(horizontal = 32.dp).padding(top=16.dp)) {
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                BasicTextField(
                    value = eventState.value.className,
                    onValueChange = {
                        eventState.value = eventState.value.copy(className = it)
                    },
                    textStyle = TextStyle(
                        color = PlannerTheme.colors.textSecondary,
                        fontFamily = Lexend,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light
                    ), // Customize the text color
                    modifier = Modifier.fillMaxWidth()
                )

                if (eventState.value.className.isEmpty()) {
                    Text(
                        text = label,
                        style = TextStyle(
                            color = PlannerTheme.colors.textSecondary.copy(0.7f),
                            fontFamily = Lexend,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light
                        ),
                        modifier = Modifier
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        CreateDivider()
    }
}