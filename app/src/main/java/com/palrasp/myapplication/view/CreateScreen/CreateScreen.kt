package com.palrasp.myapplication.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CreateScreen.*
import java.util.*

var mediumTextStyle = TextStyle(
    fontFamily = Lexend,
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    color = Color.Black
)

val textColor = Color(0xFF4D4D4D)
val confirmColor = Color(0xFF2F69FF)
val dividerColor = Color(0xffEEEEEE)
val subjectColor = Color(0xffBFBFBF)

/*
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateScreenSchemePrev() {
    val eventState = rememberEvent(
        initialEvent = Event(
            id = generateRandomId(),
            name = "Class name",
            color = Color(0xFF7DC1FF),
            start = LocalDateTime.now(),
            end = LocalDateTime.now().plusHours(1).plusMinutes(30),
            description = "",
            className = "",
            recurrenceJson = "",
            compulsory = true,
            dayOfTheWeek = 1
        )
    )
    var currentEvent by remember { mutableStateOf<CreateScreenEvent?>(null) }
    val modalBottomSheetState = rememberModalBottomSheetState()

    CreateScreenScheme(modifier = Modifier, eventState = eventState, onEvent = {
        event->
        when(event){
            is CreateScreenEvent.OpenTimeEndPicker->{}
            is CreateScreenEvent.OpenTimeStartPicker->{}
            is CreateScreenEvent.OpenDayOfWeekPicker->{}
            is CreateScreenEvent.OpenColorPicker->{}
            is CreateScreenEvent.Close->{}
            is CreateScreenEvent.Save->{}
            else->{}
        }
    })
    currentEvent?.let { event ->
        when (event) {
            is CreateScreenEvent.OpenTimeStartPicker -> {
                TimePickerDialog(eventState=eventState,onDismissRequest={
                    currentEvent=null
                }, isStart = true)


            }
            is CreateScreenEvent.OpenTimeEndPicker -> {
                ModalBottomSheet(
                    onDismissRequest = {  currentEvent=null  },
                    sheetState = modalBottomSheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() },
                ) {

                }
            }
            is CreateScreenEvent.OpenDayOfWeekPicker -> {
                ModalBottomSheet(
                    onDismissRequest = {  currentEvent=null  },
                    sheetState = modalBottomSheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() },
                ) {
                }
            }
            else -> {
            }
        }
    }

}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(onBack:()->Unit,onCreateEvent:(Event)->Unit,eventState: MutableState<Event>,isUpdate:Boolean)
{
    BackHandler() {
        onBack()
    }
    var currentEvent by remember { mutableStateOf<CreateScreenEvent?>(null) }
    CreateScreenScheme(isUpdate=isUpdate,modifier = Modifier, eventState = eventState, onEvent = {
            event->
        when(event){
            is CreateScreenEvent.OpenTimeEndPicker->{
                currentEvent=event
            }
            is CreateScreenEvent.OpenTimeStartPicker->{
                currentEvent=event
            }
            is CreateScreenEvent.OpenDayOfWeekPicker->{
                currentEvent=event
            }
            is CreateScreenEvent.OpenRepeatPicker->{
            currentEvent=event
            }
            is CreateScreenEvent.Close->{
                onBack()
            }
            is CreateScreenEvent.Save->{
                if (!  eventState.value.start.isAfter(eventState.value.start)){
                    onCreateEvent(eventState.value)
                }
            }
            else->{}
        }
    })
    currentEvent?.let { event ->
        when (event) {
            is CreateScreenEvent.OpenTimeStartPicker -> {
                TimePickerDialog(eventState=eventState,onDismissRequest={
                    currentEvent=null
                }, isStart = true)
            }
            is CreateScreenEvent.OpenTimeEndPicker -> {
                TimePickerDialog(eventState=eventState,onDismissRequest={
                    currentEvent=null
                }, isStart = false)
            }
            is CreateScreenEvent.OpenDayOfWeekPicker -> {
                CreateBottomSheet(onDismissRequest={
                    currentEvent=null
                },eventState=eventState)
            }
            is CreateScreenEvent.OpenRepeatPicker->{
                RepeatBottomSheet(onDismissRequest={
                    currentEvent=null
                },eventState=eventState)
            }

            else -> {
            }
        }
    }
}

sealed class CreateScreenEvent {
    object Close : CreateScreenEvent()
    object Save : CreateScreenEvent()
    object OpenDayOfWeekPicker : CreateScreenEvent()
    object OpenRepeatPicker : CreateScreenEvent()
    object OpenTimeStartPicker : CreateScreenEvent()
    object OpenTimeEndPicker : CreateScreenEvent()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreenScheme(
    modifier: Modifier = Modifier,
    onEvent:(CreateScreenEvent)->Unit,
    eventState: MutableState<Event>,
    isUpdate:Boolean
) {
    PlannerTheme {
        Column (modifier=Modifier.verticalScroll(rememberScrollState())){
            CreateTopPart(
                modifier = modifier,
                onEvent=onEvent,
                eventState=eventState
            )
            SubjectInput(eventState,label=stringResource(id = R.string.subject_hint))
            ClassroomInput(eventState,label=stringResource(id = R.string.classroom_hint))
            Spacer(modifier = Modifier.height(24.dp))
            CreateColorPicker(eventState, onEvent =onEvent)
            if (!isUpdate){
                DayOfTheWeekPicker(eventState, onEvent =onEvent)
                CreateDivider()
                TimePickerSection(eventState, onEvent =onEvent)
                CreateDivider()
                RepeatSection(eventState,onEvent=onEvent)
            }

            CreateDivider()
            IsNeccesarySection(eventState)


        }

    }

}

