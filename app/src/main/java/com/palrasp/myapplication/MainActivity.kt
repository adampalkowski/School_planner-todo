package com.palrasp.myapplication

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.palrasp.myapplication.CalendarClasses.*
import com.palrasp.myapplication.data.local.database.AppDatabase
import com.palrasp.myapplication.ui.theme.PlannerTheme
import com.palrasp.myapplication.utils.generateRandomId
import com.palrasp.myapplication.utils.sampleEvent
import com.palrasp.myapplication.view.*
import com.palrasp.myapplication.view.SettingsScreen.getSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.getSelectedHour
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedCalendarOption
import com.palrasp.myapplication.view.SettingsScreen.saveSelectedHour
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.EventViewModelFactory
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModelFactory
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.random.Random




class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Access room database
        val database = AppDatabase.getInstance(applicationContext)
        val eventDao = database.eventDao()
        //only Instance of eventViewModel
        val eventViewModel =
            ViewModelProvider(this, EventViewModelFactory(eventDao)).get(EventViewModel::class.java)

        setContent {
            PlannerTheme() {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(application = LocalContext.current.applicationContext as Application)
                )
                NavigationComponent(
                    eventViewModel=eventViewModel,
                    settingsViewModel=settingsViewModel
                )
            }
        }
    }
}

