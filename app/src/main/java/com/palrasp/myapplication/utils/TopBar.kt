package com.palrasp.myapplication.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.R

@Composable
fun TopBar(lessonsClicked:()->Unit,NextWeek:()->Unit,PrevWeek:()->Unit,openMonthPicker:()->Unit,iconColor: Color,selectedMonth:String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0x32639AFF))
        .padding(horizontal = 24.dp, vertical = 8.dp)){
        Row(verticalAlignment = Alignment.CenterVertically){
            IconButton(onClick =lessonsClicked) {
                Icon(painter = painterResource(id = R.drawable.ic_book), contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick =PrevWeek) {
                Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(12.dp))

            Text(text =selectedMonth, modifier = Modifier.clickable(onClick = openMonthPicker))

            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick =NextWeek) {
                Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription = null, tint = Color.Black)
            }
        }
    }
}