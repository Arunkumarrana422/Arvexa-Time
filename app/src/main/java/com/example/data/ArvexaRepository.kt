package com.example.data

import kotlinx.coroutines.flow.Flow

class ArvexaRepository(private val dao: ArvexaDao) {
    val allSessions: Flow<List<SavedSessionEntity>> = dao.getAllSessions()
    val allAlarms: Flow<List<AlarmEntity>> = dao.getAllAlarms()
    val allTimerPresets: Flow<List<TimerPresetEntity>> = dao.getAllTimerPresets()
    val allWorldClocks: Flow<List<WorldClockEntity>> = dao.getAllWorldClocks()

    suspend fun insertSession(session: SavedSessionEntity) = dao.insertSession(session)
    suspend fun deleteSession(id: Long) = dao.deleteSession(id)
    suspend fun updateSessionName(id: Long, newName: String) = dao.updateSessionName(id, newName)

    suspend fun insertAlarm(alarm: AlarmEntity): Long = dao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = dao.updateAlarm(alarm)
    suspend fun deleteAlarm(id: Long) = dao.deleteAlarm(id)
    suspend fun setAlarmEnabled(id: Long, enabled: Boolean) = dao.setAlarmEnabled(id, enabled)

    suspend fun insertTimerPreset(preset: TimerPresetEntity) = dao.insertTimerPreset(preset)
    suspend fun deleteTimerPreset(id: Long) = dao.deleteTimerPreset(id)

    suspend fun insertWorldClock(clock: WorldClockEntity) = dao.insertWorldClock(clock)
    suspend fun deleteWorldClock(id: Long) = dao.deleteWorldClock(id)

    suspend fun getPreference(key: String, defaultValue: String): String {
        return dao.getPreference(key) ?: defaultValue
    }

    suspend fun setPreference(key: String, value: String) {
        dao.setPreference(UserPreferenceEntity(key, value))
    }
}
