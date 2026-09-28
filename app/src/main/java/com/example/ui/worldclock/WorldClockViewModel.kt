package com.example.ui.worldclock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArvexaDatabase
import com.example.data.ArvexaRepository
import com.example.data.WorldClockEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CityTimeZone(
    val cityName: String,
    val countryName: String,
    val zoneId: String
)

class WorldClockViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ArvexaRepository(ArvexaDatabase.getDatabase(application).arvexaDao())

    val worldClocks: StateFlow<List<WorldClockEntity>> = repository.allWorldClocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableCities = listOf(
        CityTimeZone("London", "United Kingdom", "Europe/London"),
        CityTimeZone("New York", "United States", "America/New_York"),
        CityTimeZone("Los Angeles", "United States", "America/Los_Angeles"),
        CityTimeZone("Tokyo", "Japan", "Asia/Tokyo"),
        CityTimeZone("Dubai", "United Arab Emirates", "Asia/Dubai"),
        CityTimeZone("Singapore", "Singapore", "Asia/Singapore"),
        CityTimeZone("Sydney", "Australia", "Australia/Sydney"),
        CityTimeZone("Paris", "France", "Europe/Paris"),
        CityTimeZone("New Delhi", "India", "Asia/Kolkata"),
        CityTimeZone("Toronto", "Canada", "America/Toronto"),
        CityTimeZone("Berlin", "Germany", "Europe/Berlin"),
        CityTimeZone("Hong Kong", "China", "Asia/Hong_Kong")
    )

    fun addCity(city: CityTimeZone) {
        viewModelScope.launch {
            val clock = WorldClockEntity(
                cityName = city.cityName,
                countryName = city.countryName,
                timeZoneId = city.zoneId
            )
            repository.insertWorldClock(clock)
        }
    }

    fun deleteCity(id: Long) {
        viewModelScope.launch {
            repository.deleteWorldClock(id)
        }
    }
}
