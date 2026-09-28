package com.example.ui.timer

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArvexaDatabase
import com.example.data.ArvexaRepository
import com.example.data.TimerPresetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimerState(
    val totalTimeMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val isVibrateEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val isRepeatEnabled: Boolean = false,
    val selectedCategory: String = "Custom"
)

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ArvexaRepository(ArvexaDatabase.getDatabase(application).arvexaDao())

    val timerPresets = repository.allTimerPresets

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setDuration(millis: Long) {
        timerJob?.cancel()
        _state.update {
            it.copy(
                totalTimeMillis = millis,
                remainingMillis = millis,
                isRunning = false,
                isFinished = false
            )
        }
    }

    fun start() {
        if (_state.value.remainingMillis <= 0L || _state.value.isRunning) return
        _state.update { it.copy(isRunning = true, isFinished = false) }

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            val startTime = System.currentTimeMillis()
            val initialRemaining = _state.value.remainingMillis

            while (isActive && _state.value.isRunning) {
                val elapsed = System.currentTimeMillis() - startTime
                val currentRemaining = initialRemaining - elapsed

                if (currentRemaining <= 0L) {
                    _state.update {
                        it.copy(
                            remainingMillis = 0L,
                            isRunning = false,
                            isFinished = true
                        )
                    }
                    triggerCompletionAlert()
                    if (_state.value.isRepeatEnabled) {
                        delay(1000)
                        setDuration(_state.value.totalTimeMillis)
                        start()
                    }
                    break
                } else {
                    _state.update { it.copy(remainingMillis = currentRemaining) }
                }
                delay(50)
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    fun reset() {
        timerJob?.cancel()
        _state.update {
            it.copy(
                remainingMillis = it.totalTimeMillis,
                isRunning = false,
                isFinished = false
            )
        }
    }

    fun addTime(millis: Long) {
        _state.update {
            val newTotal = it.totalTimeMillis + millis
            val newRemaining = it.remainingMillis + millis
            it.copy(totalTimeMillis = newTotal, remainingMillis = newRemaining)
        }
    }

    fun subtractTime(millis: Long) {
        _state.update {
            val newRemaining = (it.remainingMillis - millis).coerceAtLeast(0L)
            it.copy(remainingMillis = newRemaining)
        }
    }

    fun toggleVibrate(enabled: Boolean) {
        _state.update { it.copy(isVibrateEnabled = enabled) }
    }

    fun toggleSound(enabled: Boolean) {
        _state.update { it.copy(isSoundEnabled = enabled) }
    }

    fun toggleRepeat(enabled: Boolean) {
        _state.update { it.copy(isRepeatEnabled = enabled) }
    }

    fun savePreset(title: String, duration: Long, category: String) {
        viewModelScope.launch {
            repository.insertTimerPreset(TimerPresetEntity(title = title, durationMillis = duration, category = category))
        }
    }

    private fun triggerCompletionAlert() {
        val context = getApplication<Application>().applicationContext
        if (_state.value.isVibrateEnabled) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
            } catch (e: Exception) {
                // Ignore or handle security exception gracefully
            }
        }
    }

    companion object {
        fun formatTimerMillis(millis: Long): String {
            val hours = millis / (1000 * 60 * 60)
            val minutes = (millis / (1000 * 60)) % 60
            val seconds = (millis / 1000) % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
    }
}
