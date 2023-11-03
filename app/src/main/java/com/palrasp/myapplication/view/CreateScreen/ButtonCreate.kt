package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CalendarEvents

@Composable
fun ButtonCreate(onCreate:()->Unit){
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(
                BorderStroke(1.dp, PlannerTheme.colors.iconInteractiveInactive),
                shape = CircleShape
            )
            .background(PlannerTheme.colors.uiBackground)
            .clickable(onClick =onCreate)
            .padding(16.dp)
    ) {

        Box(modifier = Modifier
            .height(3.dp)
            .width(24.dp)
            .clip(RoundedCornerShape(1.dp))
            .align(
                Alignment.Center
            )
            .background(Color(0xBA4F61FF)))
        Box(modifier = Modifier
            .height(24.dp)
            .width(3.dp)
            .clip(RoundedCornerShape(1.dp))
            .align(
                Alignment.Center
            )
            .background(Color(0xBA4F61FF)))


    }
}
