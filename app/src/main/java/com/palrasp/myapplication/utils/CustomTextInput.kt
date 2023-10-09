package com.palrasp.myapplication.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palrasp.myapplication.ui.theme.Lexend
import com.palrasp.myapplication.ui.theme.PlannerTheme

@Composable
fun PlannerEditText(modifier: Modifier, focusRequester: FocusRequester,
                    focus: Boolean,
                    onFocusChange: (Boolean) -> Unit, label:String="label",
                    textState: TextFieldState, imeAction: ImeAction = ImeAction.Next,
                    onImeAction: () -> Unit = {}){
    androidx.compose.material.OutlinedTextField(
        label = {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = Lexend, fontSize = 14.sp,
                    fontWeight = FontWeight.Light, color = Color(0xFF707070)
                )
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor =  PlannerTheme.colors.textLink,
            textColor = PlannerTheme.colors.textPrimary,
            unfocusedBorderColor = PlannerTheme.colors.uiBorder,
            focusedLabelColor = PlannerTheme.colors.textLink,
            cursorColor = PlannerTheme.colors.textPrimary,
            leadingIconColor = PlannerTheme.colors.textPrimary,
            trailingIconColor = PlannerTheme.colors.textPrimary,
            errorBorderColor = PlannerTheme.colors.error ,
            errorCursorColor =PlannerTheme.colors.error ,
            errorLabelColor = PlannerTheme.colors.error ,
            errorLeadingIconColor = PlannerTheme.colors.error
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth + 50.dp)
            .onFocusChanged { focusState ->
                textState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    textState.enableShowErrors()
                }
            },
        value = textState.text, onValueChange = { textState.text = it },
        textStyle = TextStyle(
            fontFamily = Lexend, fontSize = 14.sp,
            fontWeight = FontWeight.Light
        ), isError = textState.showErrors(),
        singleLine = false,
        maxLines = Int.MAX_VALUE,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {     onImeAction() }
        ),
        interactionSource =  remember { MutableInteractionSource() },
        shape = RoundedCornerShape(10.dp),
    )

}