package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArvexaDatabase
import com.example.data.ArvexaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val isDarkMode: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val keepScreenAwake: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ArvexaRepository(ArvexaDatabase.getDatabase(application).arvexaDao())

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val dark = repository.getPreference("dark_mode", "true").toBoolean()
            val vib = repository.getPreference("vibration", "true").toBoolean()
            val sound = repository.getPreference("sound", "true").toBoolean()
            val awake = repository.getPreference("awake", "false").toBoolean()
            _state.value = SettingsState(isDarkMode = dark, isVibrationEnabled = vib, isSoundEnabled = sound, keepScreenAwake = awake)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _state.update { it.copy(isDarkMode = enabled) }
        viewModelScope.launch { repository.setPreference("dark_mode", enabled.toString()) }
    }

    fun toggleVibration(enabled: Boolean) {
        _state.update { it.copy(isVibrationEnabled = enabled) }
        viewModelScope.launch { repository.setPreference("vibration", enabled.toString()) }
    }

    fun toggleSound(enabled: Boolean) {
        _state.update { it.copy(isSoundEnabled = enabled) }
        viewModelScope.launch { repository.setPreference("sound", enabled.toString()) }
    }

    fun toggleKeepAwake(enabled: Boolean) {
        _state.update { it.copy(keepScreenAwake = enabled) }
        viewModelScope.launch { repository.setPreference("awake", enabled.toString()) }
    }
}
