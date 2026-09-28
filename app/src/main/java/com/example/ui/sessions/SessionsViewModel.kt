package com.example.ui.sessions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ArvexaDatabase
import com.example.data.ArvexaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ArvexaRepository(ArvexaDatabase.getDatabase(application).arvexaDao())

    val sessions = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    fun renameSession(id: Long, newName: String) {
        viewModelScope.launch {
            repository.updateSessionName(id, newName)
        }
    }
}
