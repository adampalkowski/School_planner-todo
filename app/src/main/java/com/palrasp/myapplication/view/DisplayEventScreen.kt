package com.palrasp.myapplication.view

import android.graphics.Rect
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.EditableText
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme


@Composable
fun PlannerDivider(height:Int=1,color:Color=Color(0xFFDADADA)){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(height = height.dp)
        .background(color = color))
}
@Composable
fun ModDivider(height:Int=1,color:Color=Color(0xFFDADADA),onClick:()->Unit,onClear:()->Unit){
    Row(modifier = Modifier
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
        Box(modifier = Modifier
            .weight(1f)
            .height(height = height.dp)
            .background(color = color))

        Box(modifier = Modifier
            .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)){
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription =null, tint = Color(0xFF666666) )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Todo")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier
            .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClear)){
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription =null, tint = Color(0xFF666666) )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clear")
            }
        }
        Box(modifier = Modifier
            .weight(0.2f)
            .height(height = height.dp)
            .background(color = color))
    }

}
@OptIn(ExperimentalComposeUiApi::class)
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
        Column(Modifier.padding(top=64.dp)) {
            Column(modifier=Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text =event.name, style = TextStyle(fontFamily = Lexend, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center))
                Text(text = event.className)
                Row() {
                    Text(text = event.start.toString().takeLast(5),style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Center))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(painter = painterResource(id = R.drawable.ic_long_right), contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.end.toString().takeLast(5),style = TextStyle(fontFamily = Lexend, fontSize = 14.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Center))
                }
            }
            var indexState= remember{ mutableStateOf(0) }

            ModDivider(onClick = {
                val lines=notes.lines()
                val updatedLines = lines.toMutableList()
                updatedLines[indexState.value] = "[-]" +updatedLines[indexState.value]
                val updatedDescription = updatedLines.joinToString("\n")
                notes=updatedDescription
            }, onClear = {
                //todo confirm clear??
                notes=""
            })

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
            },indexState)

        }




    }
}
val lightGray=Color(0xFFDADADA)
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
            .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(4.dp))

            .clip(RoundedCornerShape(4.dp))
            .background(PlannerTheme.colors.uiBackground)
            .clickable(onClick = onCheckChanged))
    }


}

val customTextSelectionColors = TextSelectionColors(
    handleColor = Color.LightGray,
    backgroundColor = Color.LightGray.copy(alpha = 0.4f)
)
@Composable
fun TextWithTasksEditable(
    description: String,
    onDescriptionChange: (String) -> Unit,
    indexState:MutableState<Int>,
) {
    val textSize = 18.sp
    val lines = description.lines()
    var updatedDescription by remember { mutableStateOf(description) }
    val focusRequester = remember { FocusRequester() }

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
                    SelectionContainer {

                        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
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

                        }}

                }

            } else {


                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectionContainer {

                        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                        BasicTextField(
                        textStyle = TextStyle(fontSize = textSize),
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .focusRequester(focusRequester = focusRequester)
                            .onFocusChanged {
                                indexState.value = index

                            }
                            .onKeyEvent { event ->
                                if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DEL) {
                                    if (line.length == 0) {

                                        val updatedLines = lines.toMutableList()
                                        updatedLines.removeAt(index)
                                        updatedDescription = updatedLines.joinToString("\n")
                                        onDescriptionChange(updatedDescription)
                                    }
                                    true
                                } else if(event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER){

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
                        keyboardActions = KeyboardActions(onNext = {
                            Log.d("KEYBOARDDEB","DONE")
                            val updatedLines = lines.toMutableList()
                            updatedLines.add(index + 1, "") // Insert a new line at index + 1
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription) })
                        )
                }                    }                        }



            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            PlannerDivider()
        }


    }
}
enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Int> {
    val keyboardState = remember { mutableStateOf(0) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                keypadHeight
            } else {
                0
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}
