package com.palrasp.myapplication.view

import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.EditableText
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.PlannerTheme

@Composable
fun DisplayEventScreen(event:Event,GoBack:()->Unit,SaveNotes:(Event)->Unit){
    var notes by remember { mutableStateOf(event.description) }
    // Save the notes whenever they change
    DisposableEffect(notes) {
        onDispose {
            event.description=notes
            SaveNotes(event)
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        IconButton(onClick = GoBack, modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 24.dp, top = 24.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription =null )
        }
        IconButton(onClick = { notes="" }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(start = 24.dp, top = 24.dp)) {
            Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription =null )
        }
        Column(Modifier.padding(top=48.dp)) {
            Text(text =event.name)
            Text(text = event.className)
            Text(text = event.start.toString().takeLast(5))
            Divider()

            //HERE
           /* TextField(
                value = notes,
                onValueChange = { newValue -> notes = newValue },
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                textStyle = TextStyle(textDecoration = TextDecoration.None)
            )*/
            // Render the description with tasks

            TextWithTasksEditable(notes, onDescriptionChange = {
                notes = it
            })


        }
        
        
    }
}

@Composable
fun CheckBoxPlanner(checked:Boolean,onCheckChanged:()->Unit){

    val color=if (checked){
        Color.Blue
    }else{
        Color.Gray
    }
    if (checked){
        Box(modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF5887FB))
            .size(24.dp)
            .clickable(onClick = onCheckChanged)
            .border(BorderStroke(1.dp, color = Color(0xFF5887FB)), shape = RoundedCornerShape(4.dp))){
            Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription =null, tint = Color.White )
        }
    }else{
        Box(modifier = Modifier
            .size(24.dp)
            .border(BorderStroke(1.dp, color = Color(0xFF5887FB)), shape = RoundedCornerShape(4.dp))

            .clip(RoundedCornerShape(4.dp))
            .background(PlannerTheme.colors.uiBackground)
            .clickable(onClick = onCheckChanged))
    }


}

@Composable
fun TextWithTasksEditable(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    val textSize = 18.sp
    val lineHeight = with(LocalDensity.current) { textSize.toDp().toPx() }

    val lines = description.lines()
    var updatedDescription by remember { mutableStateOf(description) }
    val requester = remember { FocusRequester() }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp)
    ) {
        items(lines.size) { index ->
            val line = lines[index]

            if (line.trim().startsWith("[-]") || line.trim().startsWith("[x]")) {
                var isChecked = line.trim().startsWith("[x]")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    CheckBoxPlanner(isChecked, onCheckChanged = {
                        if (isChecked){
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] =  "[-]"+updatedLines[index].substring(3)
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        }else{
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] =  "[x]"+updatedLines[index].substring(3)
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        }
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                    // Use BasicTextField for tasks
                    BasicTextField(
                        textStyle = TextStyle(fontSize = textSize),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                            .onKeyEvent { event ->
                                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                                    if (line
                                            .trim()
                                            .startsWith("[-]") || line
                                            .trim()
                                            .startsWith("[x]") && line.length == 3
                                    ) {
                                        val updatedLines = lines.toMutableList()
                                        updatedLines[index] = ""
                                        updatedDescription = updatedLines.joinToString("\n")
                                        onDescriptionChange(updatedDescription)
                                    }
                                    true
                                } else {
                                    false
                                }
                            },
                        value = line.substring(3),
                        onValueChange = { newLine ->
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] = line.substring(0, 3) + newLine
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        },

                    )
                }

            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null, tint = Color(
                        0x60DBDBDB), modifier = Modifier.clickable(onClick = {
                        val updatedLines = lines.toMutableList()
                        updatedLines[index] = "[-]" +updatedLines[index]
                        updatedDescription = updatedLines.joinToString("\n")
                        onDescriptionChange(updatedDescription)

                    }))
                    Spacer(modifier = Modifier.width(12.dp))
                    // Use BasicTextField for regular text
                    BasicTextField(
                        textStyle = TextStyle(fontSize = textSize),
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .onKeyEvent { event ->
                                Log.d("EVENTS123","DETEE")
                                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                                    if (line.length == 0) {

                                        val updatedLines = lines.toMutableList()
                                        updatedLines.removeAt(index)
                                        updatedDescription = updatedLines.joinToString("\n")
                                        onDescriptionChange(updatedDescription)
                                    }
                                    true
                                }

                                else {
                                    false
                                }
                            },
                        value = line,
                        onValueChange = { newLine ->
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] = newLine
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        },
                        keyboardActions = KeyboardActions(onDone = {
                            val updatedLines = lines.toMutableList()
                            updatedLines.add(index + 1, "") // Insert a new line at index + 1
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription) })

                        )
                }

            }
        }
        item {
            Divider()
        }
    }
}
@Composable
fun TaskItem(description:String,isChecked:Boolean){
    Button(onClick = { /*TODO*/ }) {
        Text(text = "a")
    }
}