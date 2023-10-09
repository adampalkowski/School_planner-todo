package com.palrasp.myapplication.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.R

@Composable
fun TopBar(lessonsClicked:()->Unit,iconColor: Color){
    Box(modifier = Modifier
        .fillMaxWidth().background(Color(0x32639AFF))
        .padding(horizontal = 24.dp, vertical = 8.dp)){
        Row(){
            IconButton(onClick =lessonsClicked) {
                Icon(painter = painterResource(id = R.drawable.ic_book), contentDescription = null)
            }
        }
    }
}