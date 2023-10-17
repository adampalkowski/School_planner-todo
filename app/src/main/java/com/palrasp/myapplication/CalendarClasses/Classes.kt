package com.palrasp.myapplication.CalendarClasses


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.format.DateTimeFormatter

val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")


val classTimeTextColor=Color(0xFF7E7E7E)
@Composable
fun BasicClass(
    event: com.palrasp.myapplication.CalendarClasses.Event,
    modifier: Modifier = Modifier,
) {
    val classTextColor =
        if (event.compulsory) {
            Color(0xFFFFFFFF)
        } else {
            PlannerTheme.colors.textPrimary
        }
    val classBackgroundColor =
        if (event.compulsory) {
            event.color
        } else {
            Color(0xFFFFFFFF)
        }

    val modifierMain = if (event.compulsory) {
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(classBackgroundColor)
    } else {
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(classBackgroundColor)
            .border(BorderStroke(1.dp, color = Color(0xffD6D6D6)), shape = RoundedCornerShape(8.dp))
    }

    Box(
        modifier = modifierMain
    ) {
        Box(modifier = Modifier
            .align(Alignment.TopCenter)
            .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd =3.dp))
            .background(Color.White)
            .padding(horizontal = 2.dp)) {
            Text(
                text = event.start.toString().takeLast(5),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(color = classTimeTextColor, fontSize = 8.sp, fontWeight = FontWeight.ExtraLight, fontFamily = Lexend),
            )
        }
        Column(
            modifier = modifier.fillMaxSize().padding(4.dp).padding(vertical = 4.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = event.name,
                style = TextStyle(color = classTextColor, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = Lexend),
                fontWeight = FontWeight.Normal,
                color = classTextColor
            )
            Text(
                text = event.className,
                style = TextStyle(color = classTextColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraLight, fontFamily = Lexend),
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
            .background(Color.White)
            .padding(horizontal = 2.dp)) {
            Text(
                text = event.end.toString().takeLast(5),
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(color = classTimeTextColor, fontSize = 8.sp, fontWeight = FontWeight.ExtraLight, fontFamily = Lexend),
            )
        }
    }
}

