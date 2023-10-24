package com.palrasp.myapplication

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar")
    class Event(val event: com.palrasp.myapplication.CalendarClasses.Event) : Screen("event")
    object Create : Screen("create")
    object Settings : Screen("settings")
    object Update : Screen("create")
    object Lessons : Screen("lessons")
}