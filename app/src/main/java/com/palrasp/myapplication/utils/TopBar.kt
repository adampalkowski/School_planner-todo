package com.palrasp.myapplication.utils

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend

val topBarColor=Color(0x9EFFFFFF)
@Composable
fun TopBar(lessonsClicked:()->Unit,NextWeek:()->Unit,PrevWeek:()->Unit,openMonthPicker:()->Unit,iconColor: Color,selectedMonth:String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(topBarColor)
        .padding(horizontal = 24.dp, vertical = 2.dp)){

            Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick =PrevWeek) {
                Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(6.dp))

            Text(text =selectedMonth, modifier = Modifier, style = TextStyle(fontFamily = Lexend, fontWeight = FontWeight.Light, fontSize = 16.sp))

            Spacer(modifier = Modifier.width(6.dp))
            IconButton(onClick =NextWeek) {
                Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription = null, tint = Color.Black)
            }
            }

    }
}