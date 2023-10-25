package com.palrasp.myapplication.view.BottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.view.dividerColor
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun DayItem(day:DayOfWeek){
    Column() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .background(color = dividerColor)
                .width(24.dp)
        )
        Text(
            text = day.getDisplayName(
                TextStyle.FULL_STANDALONE,
                Locale.getDefault()
            ),
            modifier = Modifier.padding(bottom = 5.dp),
            fontWeight = FontWeight.Light, fontFamily = Lexend,
            fontSize = 14.sp,
            color = Color(0x4B000003)
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .background(color = dividerColor)
                .weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    }    }
