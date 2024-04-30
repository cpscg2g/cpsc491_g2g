package com.example.got2go.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.got2go.data.Address
import com.example.got2go.data.Coordinates
import com.example.got2go.data.FirebaseRestroomsRepository
import com.example.got2go.data.Restroom
import com.example.got2go.data.RestroomsRepository
import com.example.got2go.data.Result
import com.example.got2go.data.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val restrooms: List<Restroom>
    ) : UiState

    data class Error(
        val throwable: Throwable?
    ) : UiState
}

/**
 * ViewModel for the map screen.
 */
class MapViewModel(
    private val restroomsRepository: RestroomsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _restroomFlow = MutableStateFlow<UiState>(UiState.Loading)
    val restroomFlow: StateFlow<UiState> = _restroomFlow.asStateFlow()

    fun getNearbyRestrooms(coordinates: Coordinates, radius: Double, limit: Int) {
        viewModelScope.launch {
            restroomsRepository
                .getNearbyRestrooms(coordinates, radius, limit).asResult()
                .collect { result ->
                    _restroomFlow.update {
                        when (result) {
                            is Result.Loading -> UiState.Loading
                            is Result.Success -> UiState.Success(result.data)
                            is Result.Error -> UiState.Error(result.exception)
                        }
                    }
                }
        }
    }

    fun addRestroom(name: String, address: Address, coordinates: Coordinates, status: String) {
        viewModelScope.launch {
            restroomsRepository.addRestroom(name, address, coordinates, status)
        }
    }

    companion object {
        private const val ECS_LATITUDE = 33.6405
        private const val ECS_LONGITUDE = -117.8443
        private const val DEFAULT_RADIUS = 0.01
        private const val DEFAULT_LIMIT = 10
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val restroomsRepository = FirebaseRestroomsRepository()
                MapViewModel(restroomsRepository, savedStateHandle)
            }
        }
    }
}
