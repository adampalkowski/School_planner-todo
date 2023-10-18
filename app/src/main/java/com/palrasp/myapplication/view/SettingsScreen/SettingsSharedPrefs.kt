package com.palrasp.myapplication.view.SettingsScreen

import android.content.Context
import android.content.SharedPreferences
import com.palrasp.myapplication.view.CalendarOption

fun getSelectedCalendarOption(context: Context): CalendarOption {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val optionName =
        sharedPreferences.getString("selectedCalendarOption", CalendarOption.HALF_WEEK.name)
    return CalendarOption.valueOf(optionName ?: CalendarOption.HALF_WEEK.name)
}
fun getSelectedHour(context: Context): Float {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val hour =
        sharedPreferences.getFloat("selectedHourOption", 8f)
    return hour
}

fun saveSelectedCalendarOption(context: Context, option: CalendarOption) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("selectedCalendarOption", option.name)
    editor.apply()
}
fun saveSelectedHour(context: Context, hour: Float) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putFloat("selectedHourOption",hour)
    editor.apply()
}