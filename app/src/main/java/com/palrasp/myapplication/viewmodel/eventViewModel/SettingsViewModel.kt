package com.palrasp.myapplication.viewmodel.eventViewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.palrasp.myapplication.view.CalendarOption
import java.time.LocalDate
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    private val _firstDayOfWeek = mutableStateOf(
        LocalDate.ofEpochDay(sharedPreferences.getLong("firstDayOfWeek", LocalDate.now().toEpochDay()))
    )
    val firstDayOfWeek: MutableState<LocalDate> = _firstDayOfWeek

    private val _calendarOption = mutableStateOf(
        CalendarOption.valueOf(sharedPreferences.getString("selectedCalendarOption", CalendarOption.HALF_WEEK.name)!!)
    )
    // You can add a function to update the selected month if needed
    fun updateCalendarOption(calendarOption: CalendarOption) {
        _calendarOption.value = calendarOption
    }
    fun updateStartHour(startHour: Float) {
        _startHour.value = startHour
    }
    val calendarOption: State<CalendarOption> = _calendarOption

    private val _startHour = mutableStateOf(
        sharedPreferences.getFloat("selectedHourOption", 8f)
    )
    val startHour: MutableState<Float> = _startHour

    private val _selectedMonth = mutableStateOf(
        firstDayOfWeek.value.month.getDisplayName(
            java.time.format.TextStyle.FULL_STANDALONE,
            Locale.getDefault()
        )
    )
    val selectedMonth: MutableState<String> = _selectedMonth

    // ... other functions ...

    // You can add a function to update the selected month if needed
    fun updateSelectedMonth(newMonth: String) {
        _selectedMonth.value = newMonth
    }

    init {
        // Initialize properties with values from SharedPreferences
        val defaultFirstDayOfWeek = LocalDate.now()
        val defaultCalendarOption = CalendarOption.HALF_WEEK
        val defaultStartHour = 8f

        _firstDayOfWeek.value = _firstDayOfWeek.value.takeIf { it != defaultFirstDayOfWeek }
            ?: defaultFirstDayOfWeek

        _calendarOption.value = _calendarOption.value.takeIf { it != defaultCalendarOption }
            ?: defaultCalendarOption

        _startHour.value = _startHour.value.takeIf { it != defaultStartHour }
            ?: defaultStartHour
    }
}

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}