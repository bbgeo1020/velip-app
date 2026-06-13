package com.VelibApp.velib.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.VelibApp.velib.data.model.Station
import com.VelibApp.velib.data.repository.VelibRepository
import kotlinx.coroutines.launch

sealed class StationsUiState {
    object Loading : StationsUiState()
    data class Success(val stations: List<Station>) : StationsUiState()
    data class Error(val message: String) : StationsUiState()
}

class MapViewModel : ViewModel() {
    private val repository = VelibRepository()

    private val _uiState = MutableLiveData<StationsUiState>(StationsUiState.Loading)
    val uiState: LiveData<StationsUiState> = _uiState

    fun loadStations() {
        viewModelScope.launch {
            _uiState.value = StationsUiState.Loading
            try {
                val stations = repository.getAllStations()
                _uiState.value = StationsUiState.Success(stations)
            } catch (e: Exception) {
                _uiState.value = StationsUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}