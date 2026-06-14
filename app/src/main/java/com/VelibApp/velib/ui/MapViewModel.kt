package com.VelibApp.velib.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.VelibApp.velib.data.model.Station
import com.VelibApp.velib.data.repository.VelibRepository
import kotlinx.coroutines.delay
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

    private var refreshJob: kotlinx.coroutines.Job? = null

    fun loadStations() {
        viewModelScope.launch {
            _uiState.value = StationsUiState.Loading
            try {
                android.util.Log.d("VELIB", "Début chargement stations...")
                val stations = repository.getAllStations()
                android.util.Log.d("VELIB", "Stations reçues: ${stations.size}")
                _uiState.value = StationsUiState.Success(stations)
            } catch (e: Exception) {
                android.util.Log.e("VELIB", "ERREUR: ${e.message}", e)
                _uiState.value = StationsUiState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(60_000) // Attendre 60 secondes
                try {
                    val stations = repository.getAllStations()
                    _uiState.value = StationsUiState.Success(stations)
                } catch (_: Exception) {
                    // On ignore l'erreur en arrière-plan pour ne pas couper l'expérience utilisateur
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}