package com.example.ui.stopwatch

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArvexaDatabase
import com.example.data.ArvexaRepository
import com.example.data.SavedSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class LapItem(
    val lapNumber: Int,
    val lapTimeMillis: Long,
    val totalTimeMillis: Long
)

data class StopwatchState(
    val timeMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<LapItem> = emptyList(),
    val bestLapNumber: Int? = null,
    val slowestLapNumber: Int? = null,
    val sessionSavedMessage: String? = null
)

class StopwatchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ArvexaRepository(ArvexaDatabase.getDatabase(application).arvexaDao())

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var startTime = 0L
    private var accumulatedTime = 0L
    private var lastLapTime = 0L

    fun start() {
        if (_state.value.isRunning) return
        startTime = SystemClock.elapsedRealtime()
        _state.update { it.copy(isRunning = true, sessionSavedMessage = null) }

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive && _state.value.isRunning) {
                val elapsed = accumulatedTime + (SystemClock.elapsedRealtime() - startTime)
                _state.update { it.copy(timeMillis = elapsed) }
                delay(10)
            }
        }
    }

    fun pause() {
        if (!_state.value.isRunning) return
        timerJob?.cancel()
        accumulatedTime += SystemClock.elapsedRealtime() - startTime
        _state.update { it.copy(isRunning = false, timeMillis = accumulatedTime) }
    }

    fun reset() {
        timerJob?.cancel()
        accumulatedTime = 0L
        startTime = 0L
        lastLapTime = 0L
        _state.value = StopwatchState()
    }

    private fun calculateBestAndSlowest(laps: List<LapItem>): Pair<Int?, Int?> {
        if (laps.size < 2) return Pair(null, null)
        val bestLap = laps.minByOrNull { it.lapTimeMillis }
        val slowestLap = laps.maxByOrNull { it.lapTimeMillis }

        if (bestLap == null || slowestLap == null || bestLap.lapTimeMillis == slowestLap.lapTimeMillis) {
            return Pair(null, null)
        }
        return Pair(bestLap.lapNumber, slowestLap.lapNumber)
    }

    fun lap() {
        if (!_state.value.isRunning) return
        val currentTime = _state.value.timeMillis
        val lapDuration = if (_state.value.laps.isEmpty()) currentTime else currentTime - lastLapTime
        lastLapTime = currentTime

        val newLapNumber = _state.value.laps.size + 1
        val newLap = LapItem(newLapNumber, lapDuration, currentTime)
        val updatedLaps = listOf(newLap) + _state.value.laps // recent lap on top

        val (bestNum, slowestNum) = calculateBestAndSlowest(updatedLaps)

        _state.update {
            it.copy(
                laps = updatedLaps,
                bestLapNumber = bestNum,
                slowestLapNumber = slowestNum
            )
        }
    }

    fun deleteLap(lapNumber: Int) {
        val updatedLaps = _state.value.laps.filter { it.lapNumber != lapNumber }
        val (bestNum, slowestNum) = calculateBestAndSlowest(updatedLaps)
        _state.update {
            it.copy(
                laps = updatedLaps,
                bestLapNumber = bestNum,
                slowestLapNumber = slowestNum
            )
        }
    }

    fun clearLaps() {
        lastLapTime = _state.value.timeMillis
        _state.update { it.copy(laps = emptyList(), bestLapNumber = null, slowestLapNumber = null) }
    }

    fun saveSession(customName: String = "Stopwatch Session") {
        val current = _state.value
        if (current.timeMillis == 0L) return

        viewModelScope.launch {
            val summary = "Duration: ${formatMillis(current.timeMillis)}, Laps: ${current.laps.size}"
            val session = SavedSessionEntity(
                name = customName,
                type = "stopwatch",
                durationMillis = current.timeMillis,
                lapCount = current.laps.size,
                lapsSummary = summary
            )
            repository.insertSession(session)
            _state.update { it.copy(sessionSavedMessage = "Session saved successfully!") }
        }
    }

    fun clearSavedMessage() {
        _state.update { it.copy(sessionSavedMessage = null) }
    }

    companion object {
        fun formatMillis(millis: Long): String {
            val hours = (millis / (1000 * 60 * 60))
            val minutes = (millis / (1000 * 60)) % 60
            val seconds = (millis / 1000) % 60
            val millisPart = (millis % 1000)
            return if (hours > 0) {
                String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millisPart)
            } else {
                String.format("%02d:%02d.%03d", minutes, seconds, millisPart)
            }
        }
    }
}
