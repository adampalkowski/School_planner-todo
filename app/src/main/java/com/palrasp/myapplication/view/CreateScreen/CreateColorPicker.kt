package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.palrasp.myapplication.view.CreateScreenEvent

val createTextStyle=TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 16.sp)
@Composable
fun CreateColorPicker(eventState:MutableState<Event>,onEvent:(CreateScreenEvent)->Unit){

    val pickColors= remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .background(color = eventState.value.color)
        .clickable(onClick = {
            pickColors.value = !pickColors.value
        })
        .padding(horizontal = 24.dp, vertical = 12.dp)){
        Text(text = stringResource(id = R.string.color), style = createTextStyle, color = Color.White)
    }
    AnimatedVisibility(visible = pickColors.value) {
        LazyRow{
            items(colorsPicker){
                if (it==eventState.value.color){
                    Box(modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .background(it)
                        .clickable(onClick = {
                            eventState.value = eventState.value.copy(color = it)
                        })){
                    Icon(painter = painterResource(id = com.palrasp.myapplication.R.drawable.ic_check), contentDescription =null, tint = Color.White, modifier = Modifier.align(
                        Alignment.Center) )
                    }
                }else{
                    Box(modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .background(it)
                        .clickable(onClick = {
                            eventState.value = eventState.value.copy(color = it)
                        })){

                    }
                }

            }
        }

    }
}