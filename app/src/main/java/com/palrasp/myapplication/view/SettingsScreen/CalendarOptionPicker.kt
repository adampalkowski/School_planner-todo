package com.palrasp.myapplication.view.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.view.CalendarOption
import com.palrasp.myapplication.view.CheckBoxPlanner

@Composable
fun CalendarOptionPicker(option: CalendarOption, checked: Boolean, onCheckedChange: () -> Unit, text:String) {

    Row(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .padding(start = 24.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0x2DE4E4E4))
            .padding(8.dp)
    ) {
        Text(
            text =text ,
            style = TextStyle(
                fontFamily = Lexend,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color= PlannerTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.width(24.dp))
        CheckBoxPlanner(checked = checked, onCheckChanged = onCheckedChange)
    }
}