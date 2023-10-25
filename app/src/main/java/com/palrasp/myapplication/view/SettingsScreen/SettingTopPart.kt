package com.palrasp.myapplication.view.SettingsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.palrasp.myapplication.R
import com.palrasp.myapplication.view.SettingsScreenEvents

@Composable
fun SettingTopPart(onEvent: (SettingsScreenEvents) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 24.dp)){
        IconButton(onClick = { onEvent(SettingsScreenEvents.GoBack)}) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)

        }
    }
}