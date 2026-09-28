package com.example.ui.tools

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TimeToolsState(
    val converterValue: String = "1",
    val converterUnitFrom: String = "Hours",
    val timestampInput: String = System.currentTimeMillis().toString(),
    val is24HourFormat: Boolean = true
)

class TimeToolsViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimeToolsState())
    val state: StateFlow<TimeToolsState> = _state.asStateFlow()

    fun updateConverterValue(value: String) {
        _state.update { it.copy(converterValue = value) }
    }

    fun updateConverterUnit(unit: String) {
        _state.update { it.copy(converterUnitFrom = unit) }
    }

    fun updateTimestampInput(timestamp: String) {
        _state.update { it.copy(timestampInput = timestamp) }
    }

    fun toggle24HourFormat(is24: Boolean) {
        _state.update { it.copy(is24HourFormat = is24) }
    }
}
