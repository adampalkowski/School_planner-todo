package com.palrasp.myapplication.view.CreateScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.palrasp.myapplication.view.CreateScreenEvent

@Composable
fun CreateTopPart( onEvent: (CreateScreenEvent) -> Unit, eventState: MutableState<Event>) {
    val saveColor=if (eventState.value.start.isBefore(eventState.value.end)){
        PlannerTheme.colors.textInteractive
    }else{
        Color(0xFFC4C4C4)
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {onEvent(CreateScreenEvent.Close)}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_x),
                contentDescription = null,
                tint = PlannerTheme.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.save_button),
            style = TextStyle(
                fontFamily = Lexend,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = saveColor
            ),
            modifier = Modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .clickable(onClick = { onEvent(CreateScreenEvent.Save) })
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
    }
}

