package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArvexaDao {
    // Saved Sessions
    @Query("SELECT * FROM saved_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SavedSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SavedSessionEntity)

    @Query("DELETE FROM saved_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("UPDATE saved_sessions SET name = :newName WHERE id = :id")
    suspend fun updateSessionName(id: Long, newName: String)

    // Alarms
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Long)

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAlarmEnabled(id: Long, enabled: Boolean)

    // Timer Presets
    @Query("SELECT * FROM timer_presets ORDER BY id ASC")
    fun getAllTimerPresets(): Flow<List<TimerPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimerPreset(preset: TimerPresetEntity)

    @Query("DELETE FROM timer_presets WHERE id = :id")
    suspend fun deleteTimerPreset(id: Long)

    // World Clocks
    @Query("SELECT * FROM world_clocks ORDER BY sortOrder ASC")
    fun getAllWorldClocks(): Flow<List<WorldClockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldClock(clock: WorldClockEntity)

    @Query("DELETE FROM world_clocks WHERE id = :id")
    suspend fun deleteWorldClock(id: Long)

    // Preferences
    @Query("SELECT value FROM user_preferences WHERE `key` = :key")
    suspend fun getPreference(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(pref: UserPreferenceEntity)
}
