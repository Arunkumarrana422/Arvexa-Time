package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_sessions")
data class SavedSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val type: String, // "stopwatch" or "timer"
    val durationMillis: Long,
    val lapCount: Int,
    val lapsSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean,
    val daysOfWeek: String, // e.g. "1,2,3,4,5,6,7" or ""
    val soundUri: String = "",
    val isVibrate: Boolean = true,
    val snoozeMinutes: Int = 5
)

@Entity(tableName = "timer_presets")
data class TimerPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val durationMillis: Long,
    val category: String
)

@Entity(tableName = "world_clocks")
data class WorldClockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cityName: String,
    val countryName: String,
    val timeZoneId: String,
    val sortOrder: Int = 0
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
