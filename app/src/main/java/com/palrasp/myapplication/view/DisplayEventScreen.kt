package com.palrasp.myapplication.view

import android.graphics.Rect
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties.EditableText
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.palrasp.myapplication.CalendarClasses.Event
import com.palrasp.myapplication.R
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme
import java.time.LocalDateTime
import java.time.LocalTime


@Composable
fun PlannerDivider(height: Int = 1, color: Color = Color(0xFFDADADA)) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = height.dp)
            .background(color = color)
    )
}

@Composable
fun ModDivider(
    height: Int = 1,
    color: Color = Color(0xFFDADADA),
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height = height.dp)
                .background(color = color)
        )

        Box(
            modifier = Modifier
                .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
        ) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Todo")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(8.dp))
                .clickable(onClick = onClear)
        ) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear),
                    contentDescription = null,
                    tint = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text( stringResource(id = R.string.clear),)
            }
        }
        Box(
            modifier = Modifier
                .weight(0.2f)
                .height(height = height.dp)
                .background(color = color)
        )
    }

}

sealed class DisplayEventScreenEvents{
    class GoToEditClass(val event: Event):DisplayEventScreenEvents()
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisplayEventScreen(
    event: Event,
    GoBack: () -> Unit,
    SaveNotes: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    onEvent:(DisplayEventScreenEvents)->Unit
) {
    BackHandler() {
        GoBack()
    }
    var notes = remember { mutableStateOf(event.description) }
    var displayDeleteDialog by remember { mutableStateOf(false) }

    // Save the notes whenever they change
    DisposableEffect(notes) {
        onDispose {
            event.description = notes.value
            SaveNotes(event)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)) {
            IconButton(
                onClick = GoBack, modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    onEvent(DisplayEventScreenEvents.GoToEditClass(event))

                          }, modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = null)
            }
            IconButton(
                onClick = { displayDeleteDialog = true }, modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
            }
        }

        Column(Modifier.padding(top = 64.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = event.name,
                    style = TextStyle(
                        fontFamily = Lexend,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )
                Text(text = event.className)
                Row() {
                    Text(
                        text = event.start.toString().takeLast(5),
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_long_right),
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.end.toString().takeLast(5),
                        style = TextStyle(
                            fontFamily = Lexend,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            var indexState = remember { mutableStateOf(0) }

            ModDivider(onClick = {
                val lines = notes.value.lines()
                val updatedLines = lines.toMutableList()
                updatedLines[indexState.value] = "[-]" + updatedLines[indexState.value]
                val updatedDescription = updatedLines.joinToString("\n")
                notes .value= updatedDescription
            }, onClear = {
                //todo confirm clear??
                notes.value = "\n\n\n\n"


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
                notes.value = it
            }, indexState)

        }

        if (displayDeleteDialog) {
            Dialog(onDismissRequest = { displayDeleteDialog = false }) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.delete_class),
                            style = TextStyle(
                                fontFamily = Lexend,
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text =  stringResource(id = R.string.cancel),
                                modifier = Modifier.clickable(onClick = {
                                    displayDeleteDialog = false
                                }),
                                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text =  stringResource(id = R.string.confirm),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = confirmColor
                                ),
                                modifier = Modifier.clickable(onClick = {
                                    onDeleteEvent(event)
                                    displayDeleteDialog = false

                                    GoBack()
                                })
                            )
                        }
                    }
                }
            }
        }


    }
}

val lightGray = Color(0xFFDADADA)

@Composable
fun CheckBoxPlanner(checked: Boolean, onCheckChanged: () -> Unit) {

    val color = if (checked) {
        Color.Blue
    } else {
        Color.Gray
    }
    if (checked) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF5887FB))
                .size(24.dp)
                .clickable(onClick = onCheckChanged)
                .border(
                    BorderStroke(1.dp, color = Color(0xFF5887FB)),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(BorderStroke(1.dp, color = lightGray), shape = RoundedCornerShape(4.dp))

                .clip(RoundedCornerShape(4.dp))
                .background(PlannerTheme.colors.uiBackground)
                .clickable(onClick = onCheckChanged)
        )
    }


}

val customTextSelectionColors = TextSelectionColors(
    handleColor = Color.LightGray,
    backgroundColor = Color.LightGray.copy(alpha = 0.4f)
)

@Composable
fun TextWithTasksEditable(
    description: MutableState<String>,
    onDescriptionChange: (String) -> Unit,
    indexState: MutableState<Int>,
) {
    val textSize = 18.sp
    var lines = description.value.lines()
    var updatedDescription by remember { mutableStateOf(description.value) }
    var focusRequesters = remember { lines.map { FocusRequester() } }
    lines.let {
        focusRequesters = it.map { FocusRequester() }
    }

    Log.d("TEXTDEBUG","After")
    Log.d("TEXTDEBUG",lines.size.toString())
    Log.d("TEXTDEBUG",focusRequesters.size.toString())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Fill the whole screen
            .padding(horizontal = 12.dp)
    ) {
        items(lines.size) { index ->
            var line = lines[index]
            var cursorPosition = remember { mutableStateOf(0) }

            var textState =  TextFieldValue(line, TextRange(cursorPosition.value, cursorPosition.value))

            if (line.trim().startsWith("[-]") || line.trim().startsWith("[x]")) {
                var isChecked = line.trim().startsWith("[x]")
                var textState =  TextFieldValue(line.substring(3), TextRange(cursorPosition.value, cursorPosition.value))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    CheckBoxPlanner(isChecked, onCheckChanged = {
                        if (isChecked) {
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] = "[-]" + updatedLines[index].substring(3)
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        } else {
                            val updatedLines = lines.toMutableList()
                            updatedLines[index] = "[x]" + updatedLines[index].substring(3)
                            updatedDescription = updatedLines.joinToString("\n")
                            onDescriptionChange(updatedDescription)
                        }
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                    // Use BasicTextField for tasks
                    val focusRequester = focusRequesters[index]

                    SelectionContainer {

                        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                            BasicTextField(
                                textStyle = TextStyle(fontSize = textSize),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .focusRequester(focusRequester)
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
                                value = textState,
                                onValueChange = { newLine ->
                                    cursorPosition.value=newLine.selection.start

                                    val updatedLines = lines.toMutableList()
                                    updatedLines[index] = line.substring(0, 3) + newLine.text
                                    updatedDescription = updatedLines.joinToString("\n")
                                    onDescriptionChange(updatedDescription)
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        // Move focus to the next line
                                        if (index+1 < lines.size) {
                                            val updatedLines = lines.toMutableList()
                                            if (cursorPosition.value > 0 && cursorPosition.value < textState.text.length-1) {

                                                var textBeforeCursor = textState.text.substring(0, cursorPosition.value)
                                                var textAfterCursor = textState.text.substring(cursorPosition.value)
                                                textAfterCursor="[-]" +textAfterCursor
                                                textBeforeCursor="[-]" +textBeforeCursor
                                                val updatedLines = lines.toMutableList()

                                                updatedLines[index] = textBeforeCursor
                                                updatedLines.add(index+1, textAfterCursor)
                                                updatedDescription =updatedLines.joinToString("\n")

                                                onDescriptionChange(updatedDescription)

                                                focusRequesters[index + 1].requestFocus()
                                            }else{
                                                if(updatedLines[index+1].toString().isEmpty()){
                                                    if( updatedLines[index+1].startsWith("[-]")){
                                                        focusRequesters[ index + 1].requestFocus()
                                                    }else{
                                                        updatedLines[index+1] = "[-]" + updatedLines[index+1]
                                                        updatedDescription = updatedLines.joinToString("\n")
                                                        onDescriptionChange(updatedDescription)
                                                        focusRequesters[ index + 1].requestFocus()
                                                    }


                                                }else{
                                                    focusRequesters[ index + 1].requestFocus()
                                                }
                                            }



                                        }else{
                                            val updatedLines = lines.toMutableList()

                                            updatedLines.add("")
                                            updatedLines[index+1] = "[-]" + updatedLines[index+1]
                                            updatedDescription = updatedLines.joinToString("\n")
                                            onDescriptionChange(updatedDescription)
                                        }
                                        true
                                    }
                                ),
                            )

                        }
                    }

                }

            } else {

                var focusRequester = focusRequesters[index]

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
                                                if (index > 0) {
                                                    focusRequesters[index - 1].requestFocus()

                                                }
                                                val updatedLines = lines.toMutableList()
                                                updatedLines.removeAt(index)
                                                updatedDescription = updatedLines.joinToString("\n")
                                                onDescriptionChange(updatedDescription)

                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                value = textState,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                                ),
                                onValueChange = { newLine ->
                                    cursorPosition.value=newLine.selection.start
                                    val updatedLines = lines.toMutableList()
                                    updatedLines[index] = newLine.text
                                    updatedDescription = updatedLines.joinToString("\n")
                                    onDescriptionChange(updatedDescription)
                                },
                                keyboardActions = KeyboardActions(onAny = {
                                    // Move focus to the next line
                                    // Move focus to the next line
                                    if (index == lines.size - 1) {

                                        val updatedLines = lines.toMutableList()
                                        updatedLines.add("")
                                        updatedDescription = updatedLines.joinToString("\n")
                                        onDescriptionChange(updatedDescription)

                                    } else {
                                        if (cursorPosition.value >= 0 && cursorPosition.value < line.length) {
                                            val textBeforeCursor = line.substring(0, cursorPosition.value)
                                            val textAfterCursor = line.substring(cursorPosition.value)

                                            val updatedLines = lines.toMutableList()
                                            updatedLines[index] = textBeforeCursor
                                            updatedDescription = updatedLines.joinToString("\n")
                                            onDescriptionChange(updatedDescription)

                                            updatedLines.add(index + 1, textAfterCursor)
                                            updatedDescription = updatedLines.joinToString("\n")
                                            onDescriptionChange(updatedDescription)

                                            focusRequesters[index + 1].requestFocus()
                                        }else{
                                            val updatedLines = lines.toMutableList()
                                            updatedLines.add("")
                                            updatedDescription = updatedLines.joinToString("\n")
                                            onDescriptionChange(updatedDescription)
                                            focusRequesters[index + 1].requestFocus()
                                        }


                                    }


                                    true
                                })
                            )
                        }
                    }
                }


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
