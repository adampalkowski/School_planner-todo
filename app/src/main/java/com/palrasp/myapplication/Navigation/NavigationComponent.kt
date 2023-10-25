package com.palrasp.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

import com.palrasp.myapplication.Navigation.mainGraph
import com.palrasp.myapplication.viewmodel.EventViewModel
import com.palrasp.myapplication.viewmodel.eventViewModel.SettingsViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationComponent(navController: NavHostController = rememberNavController(), eventViewModel: EventViewModel, settingsViewModel: SettingsViewModel) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var displaySplashScreen = rememberSaveable { mutableStateOf(true) }
    val coroutineScope= rememberCoroutineScope()
    val context = LocalContext.current

    NavHost(navController, startDestination = "Main") {
        mainGraph(navController,eventViewModel,settingsViewModel=settingsViewModel, coroutineScope = coroutineScope,context=context)
    }


    /* SPLASH SCREEN
    AnimatedVisibility(
        visible = displaySplashScreen.value,
        enter = fadeIn(animationSpec = tween(800)),
        exit = fadeOut(animationSpec = tween(800))
    ) {
        SplashScreen()

    }
    LaunchedEffect(Unit) {
        delay(0) // Delay for 1 second (1000 milliseconds)
        displaySplashScreen.value =
            false // Change the value of displaySplashScreen after the delay
    }*/
}

@Composable
fun SplashScreen(){
    Box(modifier = Modifier.fillMaxSize().background(Color.Gray)){

    }
}