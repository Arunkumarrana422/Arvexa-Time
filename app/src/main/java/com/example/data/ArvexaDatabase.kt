package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SavedSessionEntity::class,
        AlarmEntity::class,
        TimerPresetEntity::class,
        WorldClockEntity::class,
        UserPreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ArvexaDatabase : RoomDatabase() {
    abstract fun arvexaDao(): ArvexaDao

    companion object {
        @Volatile
        private var INSTANCE: ArvexaDatabase? = null

        fun getDatabase(context: Context): ArvexaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArvexaDatabase::class.java,
                    "arvexa_time_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultData(database.arvexaDao())
                    }
                }
            }

            private suspend fun populateDefaultData(dao: ArvexaDao) {
                // Default timer presets
                val defaultPresets = listOf(
                    TimerPresetEntity(title = "Study", durationMillis = 25 * 60 * 1000L, category = "Study"),
                    TimerPresetEntity(title = "Workout", durationMillis = 45 * 60 * 1000L, category = "Workout"),
                    TimerPresetEntity(title = "Cooking", durationMillis = 15 * 60 * 1000L, category = "Cooking"),
                    TimerPresetEntity(title = "Meditation", durationMillis = 10 * 60 * 1000L, category = "Meditation"),
                    TimerPresetEntity(title = "Break", durationMillis = 5 * 60 * 1000L, category = "Break"),
                    TimerPresetEntity(title = "Exam Practice", durationMillis = 60 * 60 * 1000L, category = "Exam Practice")
                )
                defaultPresets.forEach { dao.insertTimerPreset(it) }

                // Default world clocks
                val defaultClocks = listOf(
                    WorldClockEntity(cityName = "London", countryName = "United Kingdom", timeZoneId = "Europe/London", sortOrder = 0),
                    WorldClockEntity(cityName = "New York", countryName = "United States", timeZoneId = "America/New_York", sortOrder = 1),
                    WorldClockEntity(cityName = "Tokyo", countryName = "Japan", timeZoneId = "Asia/Tokyo", sortOrder = 2),
                    WorldClockEntity(cityName = "Dubai", countryName = "United Arab Emirates", timeZoneId = "Asia/Dubai", sortOrder = 3),
                    WorldClockEntity(cityName = "Singapore", countryName = "Singapore", timeZoneId = "Asia/Singapore", sortOrder = 4)
                )
                defaultClocks.forEach { dao.insertWorldClock(it) }
            }
        }
    }
}
